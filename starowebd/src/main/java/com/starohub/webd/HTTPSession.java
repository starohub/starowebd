/*
 **SH**
 *
 * Ref: https://github.com/andreivisan/AndroidHttpServer
 * Ref: https://github.com/andreivisan/AndroidHttpServer/blob/master/app/src/main/java/server/http/android/androidhttpserver/server/NanoHTTPD.java
 *
 * Maintained @ 2020 Staro Hub [ https://github.com/starohub ]
 *
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 **SH**
 */

package com.starohub.webd;

import java.io.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class HTTPSession implements IHTTPSession {

    public static final int BUFSIZE = 8192;

    private final TempFileManager tempFileManager;

    private final OutputStream outputStream;

    private final PushbackInputStream inputStream;

    private int splitbyte;

    private int rlen;

    private String uri;

    private Method method;

    private Map<String, String> parms;

    private Map<String, String> headers;

    private CookieHandler cookies;

    private String queryParameterString;

    private String remoteIp;

    private String protocolVersion;

    private WebD webd;

    public HTTPSession(WebD webd, TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
        this.webd = webd;
        this.tempFileManager = tempFileManager;
        this.inputStream = new PushbackInputStream(inputStream, HTTPSession.BUFSIZE);
        this.outputStream = outputStream;
    }

    public HTTPSession(WebD webd, TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
        this.webd = webd;
        this.tempFileManager = tempFileManager;
        this.inputStream = new PushbackInputStream(inputStream, HTTPSession.BUFSIZE);
        this.outputStream = outputStream;
        this.remoteIp = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "127.0.0.1" : inetAddress.getHostAddress().toString();
        this.headers = new HashMap<String, String>();
    }

    /**
     * Decodes the sent headers and loads the data into Key/value pairs
     */
    private void decodeHeader(BufferedReader in, Map<String, String> pre, Map<String, String> parms, Map<String, String> headers) throws ResponseException {
        try {
            // Read the request line
            String inLine = in.readLine();
            if (inLine == null) {
                return;
            }

            StringTokenizer st = new StringTokenizer(inLine);
            if (!st.hasMoreTokens()) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
            }

            pre.put("method", st.nextToken());

            if (!st.hasMoreTokens()) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
            }

            String uri = st.nextToken();

            // Decode parameters from the URI
            int qmi = uri.indexOf('?');
            if (qmi >= 0) {
                decodeParms(uri.substring(qmi + 1), parms);
                uri = Tool.decodePercent(uri.substring(0, qmi));
            } else {
                uri = Tool.decodePercent(uri);
            }

            // If there's another token, its protocol version,
            // followed by HTTP headers.
            // NOTE: this now forces header names lower case since they are
            // case insensitive and vary by client.
            if (st.hasMoreTokens()) {
                protocolVersion = st.nextToken();
            } else {
                protocolVersion = "HTTP/1.1";
                Tool.LOG.log(Level.FINE, "no protocol version specified, strange. Assuming HTTP/1.1.");
            }
            String line = in.readLine();
            while (line != null && line.trim().length() > 0) {
                int p = line.indexOf(':');
                if (p >= 0) {
                    headers.put(line.substring(0, p).trim().toLowerCase(Locale.US), line.substring(p + 1).trim());
                }
                line = in.readLine();
            }

            pre.put("uri", uri);
        } catch (IOException ioe) {
            throw new ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage(), ioe);
        }
    }

    /**
     * Decodes the Multipart Body data and put it into Key/Value pairs.
     */
    private void decodeMultipartFormData(String boundary, ByteBuffer fbuf, Map<String, String> parms, Map<String, String> files) throws ResponseException {
        try {
            int[] boundary_idxs = getBoundaryPositions(fbuf, boundary.getBytes());
            if (boundary_idxs.length < 2) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings.");
            }

            final int MAX_HEADER_SIZE = 1024;
            byte[] part_header_buff = new byte[MAX_HEADER_SIZE];
            for (int bi = 0; bi < boundary_idxs.length - 1; bi++) {
                fbuf.position(boundary_idxs[bi]);
                int len = (fbuf.remaining() < MAX_HEADER_SIZE) ? fbuf.remaining() : MAX_HEADER_SIZE;
                fbuf.get(part_header_buff, 0, len);
                ByteArrayInputStream bais = new ByteArrayInputStream(part_header_buff, 0, len);
                BufferedReader in = new BufferedReader(new InputStreamReader(bais, Charset.forName("US-ASCII")));

                // First line is boundary string
                String mpline = in.readLine();
                if (!mpline.contains(boundary)) {
                    throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary.");
                }

                String part_name = null, file_name = null, content_type = null;
                // Parse the reset of the header lines
                mpline = in.readLine();
                while (mpline != null && mpline.trim().length() > 0) {
                    Matcher matcher = Tool.CONTENT_DISPOSITION_PATTERN.matcher(mpline);
                    if (matcher.matches()) {
                        String attributeString = matcher.group(2);
                        matcher = Tool.CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(attributeString);
                        while (matcher.find()) {
                            String key = matcher.group(1);
                            if (key.equalsIgnoreCase("name")) {
                                part_name = matcher.group(2);
                            } else if (key.equalsIgnoreCase("filename")) {
                                file_name = matcher.group(2);
                            }
                        }
                    }
                    matcher = Tool.CONTENT_TYPE_PATTERN.matcher(mpline);
                    if (matcher.matches()) {
                        content_type = matcher.group(2).trim();
                    }
                    mpline = in.readLine();
                }

                // Read the part data
                int part_header_len = len - (int) in.skip(MAX_HEADER_SIZE);
                if (part_header_len >= len - 4) {
                    throw new ResponseException(Status.INTERNAL_ERROR, "Multipart header size exceeds MAX_HEADER_SIZE.");
                }
                int part_data_start = boundary_idxs[bi] + part_header_len;
                int part_data_end = boundary_idxs[bi + 1] - 4;

                fbuf.position(part_data_start);
                if (content_type == null) {
                    // Read the part into a string
                    byte[] data_bytes = new byte[part_data_end - part_data_start];
                    fbuf.get(data_bytes);
                    parms.put(part_name, new String(data_bytes));
                } else {
                    // Read it into a file
                    String path = saveTmpFile(fbuf, part_data_start, part_data_end - part_data_start);
                    if (!files.containsKey(part_name)) {
                        files.put(part_name, path);
                    } else {
                        int count = 2;
                        while (files.containsKey(part_name + count)) {
                            count++;
                        }
                        files.put(part_name + count, path);
                    }
                    parms.put(part_name, file_name);
                }
            }
        } catch (ResponseException re) {
            throw re;
        } catch (Exception e) {
            throw new ResponseException(Status.INTERNAL_ERROR, e.toString());
        }
    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g.
     * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
     * Map. NOTE: this doesn't support multiple identical keys due to the
     * simplicity of Map.
     */
    private void decodeParms(String parms, Map<String, String> p) {
        if (parms == null) {
            this.queryParameterString = "";
            return;
        }

        this.queryParameterString = parms;
        StringTokenizer st = new StringTokenizer(parms, "&");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                String key = Tool.decodePercent(e.substring(0, sep)).trim();
                String curVal = Tool.decodePercent(e.substring(sep + 1));
                if (p.containsKey(key)) {
                    curVal = p.get(key) + "~" + curVal;
                }
                p.put(key, curVal);
            } else {
                String key = Tool.decodePercent(e).trim();
                String curVal = "";
                if (p.containsKey(key)) {
                    curVal = p.get(key) + "~" + curVal;
                }
                p.put(key, curVal);
            }
        }
    }

    public void execute() throws IOException {
        Response r = null;
        try {
            // Read the first 8192 bytes.
            // The full header should fit in here.
            // Apache's default header limit is 8KB.
            // Do NOT assume that a single read will get the entire header
            // at once!
            byte[] buf = new byte[HTTPSession.BUFSIZE];
            this.splitbyte = 0;
            this.rlen = 0;

            int read = -1;
            try {
                read = this.inputStream.read(buf, 0, HTTPSession.BUFSIZE);
            } catch (Exception e) {
                Tool.safeClose(this.inputStream);
                Tool.safeClose(this.outputStream);
                throw new SocketException("StaroWebD Shutdown");
            }
            if (read == -1) {
                // socket was been closed
                Tool.safeClose(this.inputStream);
                Tool.safeClose(this.outputStream);
                throw new SocketException("StaroWebD Shutdown");
            }
            while (read > 0) {
                this.rlen += read;
                this.splitbyte = findHeaderEnd(buf, this.rlen);
                if (this.splitbyte > 0) {
                    break;
                }
                read = this.inputStream.read(buf, this.rlen, HTTPSession.BUFSIZE - this.rlen);
            }

            if (this.splitbyte < this.rlen) {
                this.inputStream.unread(buf, this.splitbyte, this.rlen - this.splitbyte);
            }

            this.parms = new HashMap<String, String>();
            if (null == this.headers) {
                this.headers = new HashMap<String, String>();
            } else {
                this.headers.clear();
            }

            if (null != this.remoteIp) {
                this.headers.put("remote-addr", this.remoteIp);
                this.headers.put("http-client-ip", this.remoteIp);
            }

            // Create a BufferedReader for parsing the header.
            BufferedReader hin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, this.rlen)));

            // Decode the header into parms and header java properties
            Map<String, String> pre = new HashMap<String, String>();
            decodeHeader(hin, pre, this.parms, this.headers);

            this.method = Method.lookup(pre.get("method"));
            if (this.method == null) {
                throw new ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Syntax error.");
            }

            this.uri = pre.get("uri");

            this.cookies = new CookieHandler(this.headers);

            String connection = this.headers.get("connection");
            boolean keepAlive = protocolVersion.equals("HTTP/1.1") && (connection == null || !connection.matches("(?i).*close.*"));

            // Ok, now do the serve()
            r = this.webd.serve(this);
            if (r == null) {
                throw new ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
            } else {
                String acceptEncoding = this.headers.get("accept-encoding");
                this.cookies.unloadQueue(r);
                r.setRequestMethod(this.method);
                r.setGzipEncoding(Tool.useGzipWhenAccepted(r) && acceptEncoding != null && acceptEncoding.contains("gzip"));
                r.setKeepAlive(keepAlive);
                r.send(this.outputStream);
            }
            if (!keepAlive || "close".equalsIgnoreCase(r.getHeader("connection"))) {
                throw new SocketException("NanoHttpd Shutdown");
            }
        } catch (SocketException e) {
            // throw it out to close socket object (finalAccept)
            throw e;
        } catch (SocketTimeoutException ste) {
            // treat socket timeouts the same way we treat socket exceptions
            // i.e. close the stream & finalAccept object by throwing the
            // exception up the call stack.
            throw ste;
        } catch (IOException ioe) {
            Response resp = Tool.newFixedLengthResponse(Status.INTERNAL_ERROR, Tool.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            resp.send(this.outputStream);
            Tool.safeClose(this.outputStream);
        } catch (ResponseException re) {
            Response resp = Tool.newFixedLengthResponse(re.getStatus(), Tool.MIME_PLAINTEXT, re.getMessage());
            resp.send(this.outputStream);
            Tool.safeClose(this.outputStream);
        } finally {
            Tool.safeClose(r);
            this.tempFileManager.clear();
        }
    }

    /**
     * Find byte index separating header from body. It must be the last byte
     * of the first two sequential new lines.
     */
    private int findHeaderEnd(final byte[] buf, int rlen) {
        int splitbyte = 0;
        while (splitbyte + 3 < rlen) {
            if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
                return splitbyte + 4;
            }
            splitbyte++;
        }
        return 0;
    }

    /**
     * Find the byte positions where multipart boundaries start. This reads
     * a large block at a time and uses a temporary buffer to optimize
     * (memory mapped) file access.
     */
    private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
        int[] res = new int[0];
        if (b.remaining() < boundary.length) {
            return res;
        }

        int search_window_pos = 0;
        byte[] search_window = new byte[4 * 1024 + boundary.length];

        int first_fill = (b.remaining() < search_window.length) ? b.remaining() : search_window.length;
        b.get(search_window, 0, first_fill);
        int new_bytes = first_fill - boundary.length;

        do {
            // Search the search_window
            for (int j = 0; j < new_bytes; j++) {
                for (int i = 0; i < boundary.length; i++) {
                    if (search_window[j + i] != boundary[i])
                        break;
                    if (i == boundary.length - 1) {
                        // Match found, add it to results
                        int[] new_res = new int[res.length + 1];
                        System.arraycopy(res, 0, new_res, 0, res.length);
                        new_res[res.length] = search_window_pos + j;
                        res = new_res;
                    }
                }
            }
            search_window_pos += new_bytes;

            // Copy the end of the buffer to the start
            System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0, boundary.length);

            // Refill search_window
            new_bytes = search_window.length - boundary.length;
            new_bytes = (b.remaining() < new_bytes) ? b.remaining() : new_bytes;
            b.get(search_window, boundary.length, new_bytes);
        } while (new_bytes > 0);
        return res;
    }

    public CookieHandler getCookies() {
        return this.cookies;
    }

    public final Map<String, String> getHeaders() {
        return this.headers;
    }

    public final InputStream getInputStream() {
        return this.inputStream;
    }

    public final Method getMethod() {
        return this.method;
    }

    public final Map<String, String> getParms() {
        return this.parms;
    }

    public String getQueryParameterString() {
        return this.queryParameterString;
    }

    private RandomAccessFile getTmpBucket() {
        try {
            TempFile tempFile = this.tempFileManager.createTempFile();
            return new RandomAccessFile(tempFile.getName(), "rw");
        } catch (Exception e) {
            throw new Error(e); // we won't recover, so throw an error
        }
    }

    public final String getUri() {
        return this.uri;
    }

    public void parseBody(Map<String, String> files) throws IOException, ResponseException {
        final int REQUEST_BUFFER_LEN = 512;
        final int MEMORY_STORE_LIMIT = 1024;
        RandomAccessFile randomAccessFile = null;
        try {
            long size;
            if (this.headers.containsKey("content-length")) {
                size = Integer.parseInt(this.headers.get("content-length"));
            } else if (this.splitbyte < this.rlen) {
                size = this.rlen - this.splitbyte;
            } else {
                size = 0;
            }

            ByteArrayOutputStream baos = null;
            DataOutput request_data_output = null;

            // Store the request in memory or a file, depending on size
            if (size < MEMORY_STORE_LIMIT) {
                baos = new ByteArrayOutputStream();
                request_data_output = new DataOutputStream(baos);
            } else {
                randomAccessFile = getTmpBucket();
                request_data_output = randomAccessFile;
            }

            // Read all the body and write it to request_data_output
            byte[] buf = new byte[REQUEST_BUFFER_LEN];
            while (this.rlen >= 0 && size > 0) {
                this.rlen = this.inputStream.read(buf, 0, (int) Math.min(size, REQUEST_BUFFER_LEN));
                size -= this.rlen;
                if (this.rlen > 0) {
                    request_data_output.write(buf, 0, this.rlen);
                }
            }

            ByteBuffer fbuf = null;
            if (baos != null) {
                fbuf = ByteBuffer.wrap(baos.toByteArray(), 0, baos.size());
            } else {
                fbuf = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
                randomAccessFile.seek(0);
            }

            // If the method is POST, there may be parameters
            // in data section, too, read it:
            if (Method.POST.equals(this.method)) {
                String contentType = "";
                String contentTypeHeader = this.headers.get("content-type");

                System.out.println("Content-Type: " + contentTypeHeader);

                StringTokenizer st = null;
                if (contentTypeHeader != null) {
                    st = new StringTokenizer(contentTypeHeader, ",; ");
                    if (st.hasMoreTokens()) {
                        contentType = st.nextToken();
                    }
                }

                if ("multipart/form-data".equalsIgnoreCase(contentType)) {
                    // Handle multipart/form-data
                    if (!st.hasMoreTokens()) {
                        throw new ResponseException(Status.BAD_REQUEST,
                                "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                    }

                    String boundaryStartString = "boundary=";
                    int boundaryContentStart = contentTypeHeader.indexOf(boundaryStartString) + boundaryStartString.length();
                    String boundary = contentTypeHeader.substring(boundaryContentStart, contentTypeHeader.length());
                    if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                        boundary = boundary.substring(1, boundary.length() - 1);
                    }

                    decodeMultipartFormData(boundary, fbuf, this.parms, files);
                } else {
                    byte[] postBytes = new byte[fbuf.remaining()];
                    fbuf.get(postBytes);
                    String postLine = new String(postBytes).trim();

                    System.out.println("PostLine: " + postLine);

                    // Handle application/x-www-form-urlencoded
                    if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType)) {
                        decodeParms(postLine, this.parms);
                    } else if (postLine.length() != 0) {
                        // Special case for raw POST data => create a
                        // special files entry "postData" with raw content
                        // data
                        files.put("postData", postLine);
                    }
                }
            } else if (Method.PUT.equals(this.method)) {
                files.put("content", saveTmpFile(fbuf, 0, fbuf.limit()));
            }
        } finally {
            Tool.safeClose(randomAccessFile);
        }
    }

    /**
     * Retrieves the content of a sent file and saves it to a temporary
     * file. The full path to the saved file is returned.
     */
    private String saveTmpFile(ByteBuffer b, int offset, int len) {
        String path = "";
        if (len > 0) {
            FileOutputStream fileOutputStream = null;
            try {
                TempFile tempFile = this.tempFileManager.createTempFile();
                ByteBuffer src = b.duplicate();
                fileOutputStream = new FileOutputStream(tempFile.getName());
                FileChannel dest = fileOutputStream.getChannel();
                src.position(offset).limit(offset + len);
                dest.write(src.slice());
                path = tempFile.getName();
            } catch (Exception e) { // Catch exception if any
                throw new Error(e); // we won't recover, so throw an error
            } finally {
                Tool.safeClose(fileOutputStream);
            }
        }
        return path;
    }
}

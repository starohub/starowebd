/*
 **SH**
 *
 * %%
 * Copyright (C) 2020 Staro Hub [ https://github.com/starohub ]
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
 * 3. Neither the name of the StaroHub, StaroWebD nor the names of its contributors
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

// ====================================================================================================

/*
 **SH**
 *
 * Ref: https://github.com/andreivisan/AndroidHttpServer
 * Ref: https://github.com/andreivisan/AndroidHttpServer/blob/master/app/src/main/java/server/http/android/androidhttpserver/server/NanoHTTPD.java
 *
 * Maintained @ 2020 StaroHub [ https://github.com/progrocus/starohub ]
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

// ====================================================================================================

package com.starohub.webd;

import com.starohub.platies.Platies;
import jsx.webd.PageResponse;

import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Tool {
    private static Platies __platies = new Platies();

    public static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";

    public static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(CONTENT_DISPOSITION_REGEX, Pattern.CASE_INSENSITIVE);

    public static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";

    public static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE);

    public static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";

    public static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile(CONTENT_DISPOSITION_ATTRIBUTE_REGEX);

    /**
     * Maximum time to wait on Socket.getInputStream().read() (in milliseconds)
     * This is required as the Keep-Alive HTTP connections would otherwise block
     * the socket reading thread forever (or as long the browser is open).
     */
    public static final int SOCKET_READ_TIMEOUT = 5000;

    /**
     * Common MIME type for dynamic content: plain text
     */
    public static final String MIME_PLAINTEXT = "text/plain";

    /**
     * Common MIME type for dynamic content: html
     */
    public static final String MIME_HTML = "text/html";

    /**
     * Common MIME type for JSON content: application/json
     */
    public static final String MIME_JSON = "application/json";

    /**
     * Pseudo-Parameter to use to store the actual query string in the
     * parameters map for later re-processing.
     */
    public static final String QUERY_STRING_PARAMETER = "WebD.QUERY_STRING";

    public static final Logger LOG = Logger.getLogger(WebD.class.getName());

    public static String toJson(Object src) {
        return __platies.toJson(src);
    }

    public static Map jsonToMap(String json) {
        return __platies.fromJson(json, Map.class);
    }

    public static List jsonToList(String json) {
        return __platies.fromJson(json, List.class);
    }

    public static final void safeClose(Object closeable) {
        try {
            if (closeable != null) {
                if (closeable instanceof Closeable) {
                    ((Closeable) closeable).close();
                } else if (closeable instanceof Socket) {
                    ((Socket) closeable).close();
                } else if (closeable instanceof ServerSocket) {
                    ((ServerSocket) closeable).close();
                } else {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (IOException e) {
            Tool.LOG.log(Level.SEVERE, "Could not close", e);
        }
    }

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and an
     * array of loaded KeyManagers. These objects must properly
     * loaded/initialized by the caller.
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManager[] keyManagers) throws IOException {
        SSLServerSocketFactory res = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(loadedKeyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
            res = ctx.getServerSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return res;
    }

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and a
     * loaded KeyManagerFactory. These objects must properly loaded/initialized
     * by the caller.
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManagerFactory loadedKeyFactory) throws IOException {
        SSLServerSocketFactory res = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(loadedKeyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(loadedKeyFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            res = ctx.getServerSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return res;
    }

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a KeyStore resource with your
     * certificate and passphrase
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase) throws IOException {
        SSLServerSocketFactory res = null;
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream keystoreStream = WebD.class.getResourceAsStream(keyAndTrustStoreClasspathPath);
            keystore.load(keystoreStream, passphrase);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keystore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            res = ctx.getServerSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return res;
    }

    /**
     * Decode percent encoded <code>String</code> values.
     *
     * @param str
     *            the percent encoded <code>String</code>
     * @return expanded form of the input, for example "foo%20bar" becomes
     *         "foo bar"
     */
    public static String decodePercent(String str) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
            Tool.LOG.log(Level.WARNING, "Encoding not supported, ignored", ignored);
        }
        return decoded;
    }

    /**
     * Create a response with unknown length (using HTTP 1.1 chunking).
     */
    public static Response newChunkedResponse(IStatus status, String mimeType, InputStream data) {
        return new Response(status, mimeType, data, -1);
    }

    /**
     * Create a response with known length.
     */
    public static Response newFixedLengthResponse(IStatus status, String mimeType, InputStream data, long totalBytes) {
        return new Response(status, mimeType, data, totalBytes);
    }

    /**
     * Create a text response with known length.
     */
    public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
        if (txt == null) {
            return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(new byte[0]), 0);
        } else {
            byte[] bytes;
            try {
                bytes = txt.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                Tool.LOG.log(Level.SEVERE, "encoding problem, responding nothing", e);
                bytes = new byte[0];
            }
            return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(bytes), bytes.length);
        }
    }

    public static Response newMapResponse(Map target) {
        return newFixedLengthResponse(Status.OK, Tool.MIME_JSON, Tool.toJson(target));
    }

    public static Response newListResponse(List target) {
        return newFixedLengthResponse(Status.OK, Tool.MIME_JSON, Tool.toJson(target));
    }

    public static Response newJsonResponse(String json) {
        return newFixedLengthResponse(Status.OK, Tool.MIME_JSON, json);
    }

    public static Response newHtmlResponse(String html) {
        return newFixedLengthResponse(Status.OK, Tool.MIME_HTML, html);
    }

    public static Response newNotFoundResponse() {
        return newFixedLengthResponse(Status.NOT_FOUND, Tool.MIME_PLAINTEXT, "Not Found");
    }

    /**
     * Create a text response with known length.
     */
    public static Response newFixedLengthResponse(String msg) {
        return newFixedLengthResponse(Status.OK, Tool.MIME_HTML, msg);
    }

    /**
     * Decode parameters from a URL, handing the case where a single parameter
     * name might have been supplied several times, by return lists of values.
     * In general these lists will contain a single element.
     *
     * @param queryString
     *            a query string pulled from the URL.
     * @return a map of <code>String</code> (parameter name) to
     *         <code>List&lt;String&gt;</code> (a list of the values supplied).
     */
    public static Map<String, List<String>> decodeParameters(String queryString) {
        Map<String, List<String>> parms = new HashMap<String, List<String>>();
        if (queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                String propertyName = sep >= 0 ? Tool.decodePercent(e.substring(0, sep)).trim() : Tool.decodePercent(e).trim();
                if (!parms.containsKey(propertyName)) {
                    parms.put(propertyName, new ArrayList<String>());
                }
                String propertyValue = sep >= 0 ? Tool.decodePercent(e.substring(sep + 1)) : null;
                if (propertyValue != null) {
                    parms.get(propertyName).add(propertyValue);
                }
            }
        }
        return parms;
    }

    /**
     * @return true if the gzip compression should be used if the client
     *         accespts it. Default this option is on for text content and off
     *         for everything else.
     */
    public static boolean useGzipWhenAccepted(Response r) {
        return r.getMimeType() != null && r.getMimeType().toLowerCase().contains("text/");
    }

    public static void writeText(String filename, String content) throws Exception {
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(content.getBytes("UTF-8"));
        fos.close();
    }

    public static String readText(String filename) throws Exception {
        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read = fis.read(buffer, 0, buffer.length);
        while (read > 0) {
            baos.write(buffer, 0, read);
            read = fis.read(buffer, 0, buffer.length);
        }
        fis.close();
        return new String(baos.toByteArray(), "UTF-8");
    }

    public static byte[] loadResource(String path) {
        return loadResource(Tool.class, path);
    }

    public static byte[] loadResource(Class clazz, String path) {
        byte[] tag = new byte[0];
        try {
            InputStream inputStream = clazz.getResourceAsStream(path);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = inputStream.read(buffer, 0, buffer.length);
            while (read > 0) {
                baos.write(buffer, 0, read);
                read = inputStream.read(buffer, 0, buffer.length);
            }
            inputStream.close();
            tag = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

    public static String stacktrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String stacktrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static boolean hasError(Map inputMap) {
        if (inputMap == null) return true;
        return inputMap.containsKey("_error");
    }

    public static void copyError(Map src, Map tag) {
        if (src == null || tag == null) return;
        tag.put("_error", src.get("_error"));
    }

    public static void copyError(PageResponse src, Exception e) {
        if (src == null) return;
        src.get("_error").value(stacktrace(e));
    }

    public static void copyError(PageResponse src, Throwable e) {
        if (src == null) return;
        src.get("_error").value(stacktrace(e));
    }

    public static void copyError(Map src, Exception e) {
        if (src == null) return;
        src.put("_error", stacktrace(e));
    }

    public static void copyError(Map src, Throwable e) {
        if (src == null) return;
        src.put("_error", stacktrace(e));
    }

    public static String replaceAll(String src, String find, String repl) {
        String tag = "";
        int begin = 0;
        int idx = src.indexOf(find, begin);
        while (idx >= 0) {
            tag += src.substring(begin, idx) + repl;
            begin = idx + find.length();
            idx = src.indexOf(find, begin);
        }
        tag += src.substring(begin);
        return tag;
    }

    public static String mapItemToString(Map inputMap, String key) {
        if (inputMap == null) return "";
        if (!inputMap.containsKey(key)) return "";
        return inputMap.get(key) + "";
    }

    public static List mapItemToList(Map inputMap, String key) {
        if (inputMap == null) return new ArrayList();
        if (!inputMap.containsKey(key)) return new ArrayList();
        return __platies.fromJson(toJson(inputMap.get(key)), List.class);
    }

    public static Map mapItemToMap(Map inputMap, String key) {
        if (inputMap == null) return new HashMap();
        if (!inputMap.containsKey(key)) return new HashMap();
        return __platies.fromJson(toJson(inputMap.get(key)), Map.class);
    }

    public static List listItemToList(List inputList, int idx) {
        if (inputList == null) return new ArrayList();
        if (idx < 0 || idx >= inputList.size()) return new ArrayList();
        return __platies.fromJson(toJson(inputList.get(idx)), List.class);
    }

    public static Map listItemToMap(List inputList, int idx) {
        if (inputList == null) return new HashMap();
        if (idx < 0 || idx >= inputList.size()) return new HashMap();
        return __platies.fromJson(toJson(inputList.get(idx)), Map.class);
    }

    public static byte[] decodeBase64(byte[] b) throws Exception {
        return Base64.getDecoder().decode(new String(b, "UTF-8"));
    }

    public static byte[] encodeBase64(byte[] b) throws Exception {
        return Base64.getEncoder().encode(b);
    }
}

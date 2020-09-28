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

package jsx.webd;

import com.starohub.webd.Tool;
import jsb.io.SException;
import jsb.webd.SSession;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Platform {
    private Config _config;

    public Config config() {
        return _config;
    }

    public Platform config(Config cfg) {
        _config = cfg;
        return this;
    }

    public String encodeBase64(byte[] src) {
        return Base64.getEncoder().withoutPadding().encodeToString(src);
    }

    public byte[] decodeBase64(String src) {
        return Base64.getDecoder().decode(src);
    }

    public Platform log(Exception e) {
        log("==> Exception: " + Tool.stacktrace(e));
        return this;
    }

    public Platform log(SException e) {
        log("==> SException: " + Tool.stacktrace(e));
        return this;
    }

    public Platform log(Throwable e) {
        log("==> Throwable: " + Tool.stacktrace(e));
        return this;
    }

    public Platform log(String line) {
        saveLog(line);
        return this;
    }

    protected Platform saveLog(String line) {
        try {
            if (config() != null) {
                if (config().dataFolder((SSession)null) != null) {
                    if (new File(config().dataFolder((SSession)null)).exists()) {
                        String logFile = new File(config().dataFolder((SSession)null), "StaroWebD.txt").getAbsolutePath();
                        if (new File(logFile).exists()) {
                            FileOutputStream fos = new FileOutputStream(logFile, true);
                            fos.write(("\n" + line).getBytes("UTF-8"));
                            fos.close();
                        } else {
                            FileOutputStream fos = new FileOutputStream(logFile);
                            fos.write(("\n" + line).getBytes("UTF-8"));
                            fos.close();
                        }
                        return this;
                    }
                }
            }
            System.err.println(line);
        } catch (Throwable e) {
        }
        return this;
    }

    public String proxy(String host, String reqJson) {
        String resJson = "{\"res\": {}}";
        try {
            VHost vh = config().vhostList().find(host);
            if (vh != null && vh.proxyEndpoint() != null) {
                String url = vh.proxyEndpoint();
                HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
                String urlParameters  = "token=" + URLEncoder.encode(vh.proxyToken(), "UTF-8") + "&req=" + URLEncoder.encode(reqJson, "UTF-8");
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;
                conn.setDoOutput( true );
                conn.setInstanceFollowRedirects( false );
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty( "charset", "utf-8");
                conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                conn.setUseCaches( false );
                try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                    wr.write( postData );
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                resJson = content.toString();
            }
        } catch(Throwable e) {
            log(e);
        }
        return resJson;
    }
}

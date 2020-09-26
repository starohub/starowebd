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
 * 3. Neither the name of the Staro Hub, Staro WebD nor the names of its contributors
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

package jsx.webd.defaultpages;

import com.starohub.webd.Tool;
import jsb.webd.SSession;
import jsx.webd.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DefaultSenderProxyPage extends Page {

    public DefaultSenderProxyPage(WebDApi api) {
        super(api,"system.default_sender_proxy", "Default Sender Proxy", "Default sender proxy page of StaroWebD.");
    }

    public boolean accepted(final jsb.webd.SSession session) {
        VHost vh = config().vhostList().find(session.host());
        if (vh == null) return false;
        if (!vh.hasPageProxy()) return false;
        if (!vh.hasPageSender()) return false;
        String path = session.uri();
        if ("/proxy.yo".equalsIgnoreCase(path)) {
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/**");
        pr.put(new PageItem("req", "Request JSON", "Request JSON from proxy receiver.", "get", "", ""));
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        ps.put(new PageItem("res", "Response JSON", "Response JSON to proxy receiver.", "string", "", ""));
        return ps;
    }

    public PageRequest sessionToRequest(final jsb.webd.SSession session) {
        session.files();
        PageRequest pr = requestPattern().clone();
        pr.get("_session").value(session);
        pr.get("req").value(session.params().get("req"));
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            SSession session = (SSession)request.get("_session").value();
            String reqJson = URLDecoder.decode(request.get("req").value() + "", "UTF-8");
            Map rqMap = Tool.jsonToMap(reqJson);
            Map sesMap = Tool.mapItemToMap(rqMap, "_session");
            session.fromMap(sesMap);
            java.util.List<String> keys = new ArrayList<>();
            for (String key : session.files().keySet()) {
                if (key.startsWith("base64_")) {
                    keys.add(key);
                }
            }
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String keyB = key.substring("base64_".length());
                try {
                    String tmpFile = File.createTempFile("starowebd", null).getAbsolutePath();
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    byte[] buffer = api().config().platform().decodeBase64(session.files().get(key));
                    fos.write(buffer);
                    fos.close();
                    session.files().remove(key);
                    session.files().put(keyB, tmpFile);
                } catch (Throwable e) {
                }
            }
            rqMap.remove("_session");
            rqMap.put("_session", session);
            request.fromMap(rqMap);

            final String path = session.uri();
            if (path.startsWith("/fonts/") || path.startsWith("/styles/") || path.startsWith("/scripts/") || path.startsWith("/images/")) {
                PageResponse ps3 = responsePattern().clone();
                String uri = session.uri().substring(1);
                if (uri.endsWith("/")) {
                    uri = uri.substring(0, uri.length() - 1);
                }
                uri = "/resources/" + uri;
                FileItem item = new FileItem(api().sbObject(), uri);
                if (!item.kind().equalsIgnoreCase("not_found")) {
                    api().markup().renderFile(ps3, uri, item.mime());
                    Map resMap = ps3.toMap();
                    String resJson = Tool.toJson(resMap);
                    ps.get("res").value(resJson);
                    return ps;
                }
                return ps;
            }

            PageResponse ps2 = api().originPageFactory().run(session);
            if (ps2 != null) {
                Map resMap = ps2.toMap();
                String resJson = Tool.toJson(resMap);
                ps.get("res").value(resJson);
                return ps;
            } else {
                ps.get("res").value("\"null\"");
            }
        } catch (Throwable e) {
            Tool.LOG.log(Level.SEVERE, "Failed to view page: ", e);
            config().platform().log("Failed to view page: " + Tool.stacktrace(e));
            Tool.copyError(ps, e);
        }
        return ps;
    }
}

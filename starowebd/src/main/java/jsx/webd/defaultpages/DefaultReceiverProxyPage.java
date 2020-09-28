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
import com.starohub.webd.sandbox.webd.MasterPage;
import jsb.webd.SSession;
import jsx.webd.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DefaultReceiverProxyPage extends MasterPage {
    public DefaultReceiverProxyPage(WebDApi api) {
        super(api,"system.default_receiver_proxy", "Default Receiver Proxy", "Default receiver proxy page of StaroWebD.");
    }

    public boolean accepted(final SSession session) {
        VHost vh = config().vhostList().find(session.host());
        if (vh == null) return false;
        if (!vh.hasPageProxy()) return false;
        if (!vh.hasPageReceiver()) return false;
        return true;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/**");
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    public PageRequest sessionToRequest(final SSession session) {
        PageRequest pr = requestPattern().clone();
        pr.get("_session").value(session);
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            SSession session = (SSession)request.get("_session").value();
            VHost vh = config().vhostList().find(session.host());
            java.util.List<String> keys = new ArrayList<>();
            for (String key : session.files().keySet()) {
                keys.add(key);
            }
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                try {
                    FileInputStream fis = new FileInputStream(session.files().get(key));
                    byte[] buffer = new byte[1024];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int read = fis.read(buffer, 0, buffer.length);
                    while (read > 0) {
                        baos.write(buffer, 0, read);
                        read = fis.read(buffer, 0, buffer.length);
                    }
                    fis.close();
                    session.files().put("base64_" + key, api().config().platform().encodeBase64(baos.toByteArray()));
                    session.files().remove(key);
                } catch (Throwable e) {

                }
            }
            Map rqMap = request.toMap();
            rqMap.remove("_session");
            rqMap.put("_session", session.toMap());
            String requestJson = Tool.toJson(rqMap);
            String responseJson = config().platform().proxy(session.host(), requestJson);
            Map resMap = Tool.jsonToMap(responseJson);
            if ("null".equalsIgnoreCase(resMap.get("res") + "")) {
                return null;
            }
            resMap = Tool.jsonToMap(resMap.get("res") + "");
            String[] findArr = new String[] {"_redirect", "_error", "_return_list", "_return_html", "_return_bytes", "_return_json"};
            boolean found = false;
            for (String key : findArr) {
                if (resMap.containsKey(key)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                ps.fromMap(resMap);
            } else {
                ps.get("_return_json").value(Tool.toJson(resMap));
            }
            return ps;
        } catch (Exception e) {
            log("Failed to view page: " + stacktrace(e));
            copyError(ps, e);
        }
        return ps;
    }
}

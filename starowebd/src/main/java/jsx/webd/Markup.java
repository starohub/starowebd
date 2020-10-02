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

package jsx.webd;

import com.starohub.jsb.SBObject;
import com.starohub.webd.Tool;
import com.starohub.webd.sandbox.DefaultSBObject;
import jsb.SFile;
import jsb.io.SException;
import jsb.io.SInputStream;
import jsb.webd.SSession;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Markup {
    private WebDApi _api;

    public Markup(WebDApi api) {
        _api = api;
    }

    public WebDApi api() {
        return _api;
    }

    public final Markup renderFile(SSession session, PageResponse ps, String filepath, String mime) throws SException {
        try {
            SFile file = api().sbObject(session).sandbox().machine().mnt().newFile(filepath);
            SInputStream fis = file.inputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer, 0, buffer.length);
            while (read > 0) {
                baos.write(buffer, 0, read);
                read = fis.read(buffer, 0, buffer.length);
            }
            fis.close();
            String base64 = api().config().platform().encodeBase64(baos.toByteArray());
            baos.close();
            ps.get("_return_bytes").value(base64);
            ps.get("_return_mime").value(mime);
        } catch (Throwable e) {
            throw api().sbObject(session).sandbox().machine().io().newException(e);
        }
        return this;
    }

    public final Markup renderJSB(SSession session, PageResponse ps, String scriptName, String uri, String filepath) throws SException {
        try {
            SFile file = api().sbObject(session).sandbox().machine().mnt().newFile(filepath);
            SInputStream fis = file.inputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer, 0, buffer.length);
            while (read > 0) {
                baos.write(buffer, 0, read);
                read = fis.read(buffer, 0, buffer.length);
            }
            fis.close();
            String js = new String(baos.toByteArray(), "UTF-8");
            Map more = new HashMap();
            for (Object key : api().more().keySet()) {
                more.put(key, api().more().get(key));
            }
            more.put("session", session);
            more.put("api", api());
            BluePrint blueprint = api().blueprint(session);
            SBObject sbo = new DefaultSBObject(js, blueprint.host().pageTimeout(), api(), session, more);
            sbo.set("_script", scriptName);
            Map iMap = new HashMap();
            if (uri.length() == 0) {
                uri = "/";
            }
            iMap.put("uri", uri);
            iMap.put("session", session);
            Map oMap = new HashMap();
            Object oRS = sbo.exec("main", iMap);
            if (oRS != null) {
                oMap = (Map)oRS;
                ps.fromMap(oMap);
            }
        } catch (Throwable e) {
            throw api().sbObject(session).sandbox().machine().io().newException(e);
        }
        return this;
    }

    public final Markup renderJSM(SSession session, Page page, PageResponse ps, String uri, String filepath) throws SException {
        try {
            Map args = new HashMap();
            SFile file = api().sbObject(session).sandbox().machine().mnt().newFile(filepath);
            String html = new String(file.readFile(), "UTF-8");

            Map pm = new HashMap();
            session.files();
            for (String key : session.params().keySet()) {
                pm.put(key, session.params().get(key));
            }

            String find = "<!-- jsb.data:";
            int idx = html.indexOf(find);
            if (idx >= 0) {
                int idx2 = html.indexOf("-->", idx + find.length());
                if (idx2 >= 0) {
                    String dataFilePath = html.substring(idx + find.length(), idx2).trim();
                    dataFilePath = Tool.replaceAll(Tool.replaceAll(page.mergeHtml(dataFilePath, pm), "{", ""),"}", "");
                    SFile dataFile = api().sbObject(session).sandbox().machine().mnt().newFile(dataFilePath);
                    if (dataFile.exists()) {
                        String jsonStr = new String(dataFile.readFile(), "UTF-8");
                        args = Tool.jsonToMap(jsonStr);
                        for (Object key : pm.keySet()) {
                            args.put(key, pm.get(key));
                        }
                    }
                }
            } else {
                find = "<!-- jsb.dataset:";
                idx = html.indexOf(find);
                if (idx >= 0) {
                    int idx2 = html.indexOf("-->", idx + find.length());
                    if (idx2 >= 0) {
                        String dsCode = html.substring(idx + find.length(), idx2).trim();
                        dsCode = Tool.replaceAll(Tool.replaceAll(page.mergeHtml(dsCode, pm), "{", ""),"}", "");
                        if (api().blueprint(session) != null) {
                            if (api().blueprint(session).dataset(dsCode) != null) {
                                String jsonStr = api().blueprint(session).dataset(dsCode).jsonData(uri);
                                args = Tool.jsonToMap(jsonStr);
                                for (Object key : pm.keySet()) {
                                    args.put(key, pm.get(key));
                                }
                            }
                        }
                    }
                }
            }
            html = page.mergeHtml(html, args);
            String base64 = api().config().platform().encodeBase64(html.getBytes("UTF-8"));
            ps.get("_return_bytes").value(base64);
            ps.get("_return_mime").value("text/html");
        } catch (Throwable e) {
            throw api().sbObject(session).sandbox().machine().io().newException(e);
        }
        return this;
    }
}

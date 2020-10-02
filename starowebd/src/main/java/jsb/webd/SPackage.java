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

package jsb.webd;

import com.starohub.webd.Tool;
import jsb.SFile;
import jsb.SMachine;
import jsb.io.SException;
import jsb.io.SInputStream;
import jsx.webd.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SPackage extends jsb.SPackage {
    private Map<String, SBluePrint> _blueprintMap = new HashMap<>();
    private WebDApi _api;

    public SPackage(WebDApi api, SMachine machine, Map more) {
        super(machine, more);
        _api = api;
    }

    protected final WebDApi api() {
        return _api;
    }

    public final List<SBluePrint> blueprintList() {
        List<SBluePrint> tag = new ArrayList<>();
        for (String host : _blueprintMap.keySet()) {
            tag.add(_blueprintMap.get(host));
        }
        return tag;
    }

    public final SBluePrint blueprint(SSession session) {
        VHost vh = api().config().vhostList().find(session.host());
        if (session.proxyHost() != null) {
            vh = api().config().vhostList().find(session.proxyHost());
        }
        if (!_blueprintMap.containsKey(vh.host())) {
            _blueprintMap.put(vh.host(), createBluePrint(this, api().blueprint(session)));
        }
        return _blueprintMap.get(vh.host());
    }

    protected abstract SBluePrint createBluePrint(jsb.webd.SPackage pkg, BluePrint blueprint);

    public final SPackage theme(SSession session, Map outputMap, String uri, Map args) throws Exception {
        outputMap.put("_return_html", merge(template(session, uri), args));
        return this;
    }

    public final String template(SSession session, String uri) throws Exception {
        String tag = "";
        FileItem item = new FileItem(api().blueprint(session).sbObject(), uri);
        if (item.kind().equalsIgnoreCase("file")) {
            SFile file = machine().mnt().newFile(uri);
            try {
                SInputStream fis = file.inputStream();
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int read = fis.read(buffer, 0, buffer.length);
                while (read > 0) {
                    baos.write(buffer, 0, read);
                    read = fis.read(buffer, 0, buffer.length);
                }
                fis.close();
                tag = new String(baos.toByteArray(), "UTF-8");
                baos.close();
            } catch (SException e) {
                throw new Exception(e);
            }
        }
        return tag;
    }

    public final String merge(String template, Map args) throws Exception {
        VelocityEngine engine = new VelocityEngine();
        engine.init();
        VelocityContext ctx = new VelocityContext();
        for (Object key : args.keySet()) {
            ctx.put(key + "", args.get(key));
        }
        Writer writer = new StringWriter();
        engine.evaluate(ctx, writer, "", template);
        return writer.toString();
    }

    public final Map<String, String> getQueryMap(final jsb.webd.SSession session) throws Exception {
        return session.params();
    }

    public final Map<String, String> postQueryMap(final jsb.webd.SSession session) throws Exception {
        return session.params();
    }

    public final Map postJsonMap(final jsb.webd.SSession session) throws Exception {
        return Tool.jsonToMap(session.files().get("postData") + "");
    }

    public final String stringParam(Map<String, String> params, String key, String defaultValue) {
        String target = defaultValue;
        if (params.containsKey(key)) {
            target = params.get(key);
        }
        return target;
    }

    public final List<String> stringListParam(Map<String, String> params, String key) {
        if (params.containsKey(key)) {
            String[] paramArray = params.get(key).split("~");
            List<String> target = new ArrayList<>();
            for (int i = 0; i < paramArray.length; i++) {
                target.add(paramArray[i]);
            }
            return target;
        }
        return new ArrayList<>();
    }

    public final int intParam(Map<String, String> params, String key, int defaultValue) {
        int target = defaultValue;
        if (params.containsKey(key)) {
            target = Integer.parseInt(params.get(key));
        }
        return target;
    }

    public final long longParam(Map<String, String> params, String key, long defaultValue) {
        long target = defaultValue;
        if (params.containsKey(key)) {
            target = Long.parseLong(params.get(key));
        }
        return target;
    }

    public final SPackage showAds(int adsStart, int adsTime, String adsUrl) {
        _api.config().platform().showAds(machine(), adsStart, adsTime, adsUrl);
        return this;
    }

    public final SPackage hideAds() {
        _api.config().platform().hideAds(machine());
        return this;
    }
}

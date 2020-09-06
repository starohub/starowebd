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

package com.starohub.webd.sandbox.webd;

import jsb.SMachine;
import com.starohub.webd.FileItem;
import com.starohub.webd.Tool;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Package extends jsb.SPackage {
    private com.starohub.webd.Config _webConfig;
    public com.starohub.webd.Config webConfig() { return _webConfig; }

    public Package(SMachine machine, com.starohub.webd.Config webConfig) {
        super(machine);
        _webConfig = webConfig;
    }

    public void theme(Map outputMap, String uri, Map args) throws Exception {
        outputMap.put("_return_html", merge(template(uri), args));
    }

    public String template(String uri) throws Exception {
        FileItem webDir = new FileItem(webConfig().dataFolder());
        FileItem item = new FileItem(webDir.filepath(), uri);
        if (item.kind().equalsIgnoreCase("file")) {
            FileInputStream fis = new FileInputStream(item.filepath());
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            String tpl = new String(buffer, "UTF-8");
            return tpl;
        } else {
            return "";
        }
    }

    public String merge(String template, Map args) throws Exception {
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

    public Map<String, String> getQueryMap(final com.starohub.webd.IHTTPSession session) throws Exception {
        return session.getParms();
    }

    public Map<String, String> postQueryMap(final com.starohub.webd.IHTTPSession session) throws Exception {
        Map<String, String> files = new HashMap<String, String>();
        session.parseBody(files);
        return session.getParms();
    }

    public  Map postJsonMap(final com.starohub.webd.IHTTPSession session) throws Exception {
        Map<String, String> files = new HashMap<String, String>();
        session.parseBody(files);
        return Tool.jsonToMap(files.get("postData") + "");
    }

    public String stringParam(Map<String, String> params, String key, String defaultValue) {
        String target = defaultValue;
        if (params.containsKey(key)) {
            target = params.get(key);
        }
        return target;
    }

    public List<String> stringListParam(Map<String, String> params, String key) {
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

    public int intParam(Map<String, String> params, String key, int defaultValue) {
        int target = defaultValue;
        if (params.containsKey(key)) {
            target = Integer.parseInt(params.get(key));
        }
        return target;
    }

    public long longParam(Map<String, String> params, String key, long defaultValue) {
        long target = defaultValue;
        if (params.containsKey(key)) {
            target = Long.parseLong(params.get(key));
        }
        return target;
    }
}

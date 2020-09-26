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

import com.starohub.jsb.SBObject;
import com.starohub.webd.*;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public abstract class Page {
    private String _code;
    private String _name;
    private String _desc;
    private PageRequest _requestPattern;
    private PageResponse _responsePattern;
    private boolean _docVisible;
    private WebDApi _api;

    public Page(WebDApi api, String code, String name, String desc) {
        _api = api;
        _code = code;
        _name = name;
        _desc = desc;
        _requestPattern = createRequestPattern();
        _responsePattern = createResponsePattern();
        _docVisible = false;
    }

    public WebDApi api() {
        return _api;
    }

    protected SBObject sbObject() {
        return api().sbObject();
    }

    public boolean docVisible() {
        return _docVisible;
    }

    public Page docVisible(boolean src) {
        _docVisible = src;
        return this;
    }

    protected PageRequest createRequestPattern() {
        return null;
    }

    protected PageResponse createResponsePattern() {
        return null;
    }

    public PageRequest requestPattern() {
        return _requestPattern;
    }

    public Page requestPattern(PageRequest pr) {
        _requestPattern = pr;
        return this;
    }

    public PageResponse responsePattern() {
        return _responsePattern;
    }

    public Page responsePattern(PageResponse ps) {
        _responsePattern = ps;
        return this;
    }

    public String code() {
        return _code;
    }

    public Page code(String src) {
        _code = src;
        return this;
    }

    public String name() {
        return _name;
    }

    public Page name(String src) {
        _name = src;
        return this;
    }

    public String desc() {
        return _desc;
    }

    public Page desc(String src) {
        _desc = src;
        return this;
    }

    public Config config() {
        return api().config();
    }

    public boolean accepted(final String code) {
        return _code.equalsIgnoreCase(code);
    }

    public abstract boolean accepted(final jsb.webd.SSession session);

    public abstract PageRequest sessionToRequest(final jsb.webd.SSession session);

    public abstract PageResponse run(PageRequest request);

    protected void theme(PageResponse output, String template, Map args) throws Exception {
        output.get("_return_html").value(merge(template, args));
    }

    protected void theme(Map outputMap, String template, Map args) throws Exception {
        outputMap.put("_return_html", merge(template, args));
    }

    protected String merge(String path, Map args) throws Exception {
        String template = new String(loadTemplate(path), "UTF-8");
        try {
            Velocity.init();
        } catch (Throwable e) {
            if (config().platform() != null) {
                config().platform().log(e);
            }
        }
        VelocityContext ctx = new VelocityContext();
        for (Object key : args.keySet()) {
            ctx.put(key + "", args.get(key));
        }
        Writer writer = new StringWriter();
        Velocity.evaluate(ctx, writer, new File(path).getName(), template);
        return writer.toString();
    }

    protected String mergeHtml(String template, Map args) throws Exception {
        try {
            Velocity.init();
        } catch (Throwable e) {
            if (config().platform() != null) {
                config().platform().log(e);
            }
        }
        VelocityContext ctx = new VelocityContext();
        for (Object key : args.keySet()) {
            ctx.put(key + "", args.get(key));
        }
        Writer writer = new StringWriter();
        Velocity.evaluate(ctx, writer, "", template);
        return writer.toString();
    }

    protected byte[] loadResource(String path) {
        Class clazz = WebDApi.class;
        return Tool.loadResource(clazz, path);
    }

    protected byte[] loadTemplate(String path) {
        String filename = "/templates/" + path;
        return loadResource(filename);
    }
}

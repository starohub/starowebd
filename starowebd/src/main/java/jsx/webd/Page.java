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
import jsb.io.SException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.PrintWriter;
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
    private BluePrint _blueprint;

    public Page(BluePrint bluePrint, String code, String name, String desc) {
        _blueprint = bluePrint;
        _code = code;
        _name = name;
        _desc = desc;
        _requestPattern = createRequestPattern();
        _responsePattern = createResponsePattern();
        _docVisible = false;
    }

    protected final BluePrint blueprint() {
        return _blueprint;
    }

    protected final SBObject sbObject() {
        return _blueprint.sbObject();
    }

    public final boolean docVisible() {
        return _docVisible;
    }

    public final Page docVisible(boolean src) {
        _docVisible = src;
        return this;
    }

    protected abstract PageRequest createRequestPattern();

    protected abstract PageResponse createResponsePattern();

    public final PageRequest requestPattern() {
        return _requestPattern;
    }

    public final Page requestPattern(PageRequest pr) {
        _requestPattern = pr;
        return this;
    }

    public final PageResponse responsePattern() {
        return _responsePattern;
    }

    public final Page responsePattern(PageResponse ps) {
        _responsePattern = ps;
        return this;
    }

    public final String code() {
        return _code;
    }

    public final Page code(String src) {
        _code = src;
        return this;
    }

    public final String name() {
        return _name;
    }

    public final Page name(String src) {
        _name = src;
        return this;
    }

    public final String desc() {
        return _desc;
    }

    public final Page desc(String src) {
        _desc = src;
        return this;
    }

    protected Platform platform() {
        if (blueprint() != null) {
            return blueprint().platform();
        }
        return null;
    }

    public final boolean accepted(final String code) {
        return _code.equalsIgnoreCase(code);
    }

    public abstract boolean accepted(final jsb.webd.SSession session);

    public abstract PageRequest sessionToRequest(final jsb.webd.SSession session);

    public abstract PageResponse run(PageRequest request);

    protected final Page theme(PageResponse output, String template, Map args) throws Exception {
        output.get("_return_html").value(merge(template, args));
        return this;
    }

    protected final Page theme(Map outputMap, String template, Map args) throws Exception {
        outputMap.put("_return_html", merge(template, args));
        return this;
    }

    protected final String merge(String path, Map args) throws Exception {
        String template = new String(loadTemplate(path), "UTF-8");
        try {
            Velocity.init();
        } catch (Throwable e) {
            if (platform() != null) {
                platform().log(e);
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

    protected final String mergeHtml(String template, Map args) throws Exception {
        try {
            Velocity.init();
        } catch (Throwable e) {
            if (platform() != null) {
                platform().log(e);
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

    protected Class createResourceClass() {
        return Page.class;
    }

    protected final byte[] loadResource(String path) {
        return Tool.loadResource(createResourceClass(), path);
    }

    protected final byte[] loadTemplate(String path) {
        String filename = "/templates/" + path;
        return loadResource(filename);
    }

    protected final String stacktrace(Exception e) {
        return Tool.stacktrace(e);
    }

    protected final String stacktrace(Throwable e) {
        return Tool.stacktrace(e);
    }

    protected final boolean hasError(Map inputMap) {
        return Tool.hasError(inputMap);
    }

    protected final Page copyError(Map src, Map tag) {
        Tool.copyError(src, tag);
        return this;
    }

    protected final Page copyError(PageResponse src, Exception e) {
        Tool.copyError(src, e);
        return this;
    }

    protected final Page copyError(PageResponse src, Throwable e) {
        Tool.copyError(src, e);
        return this;
    }

    protected final Page copyError(Map src, Exception e) {
        Tool.copyError(src, e);
        return this;
    }

    protected final Page copyError(Map src, Throwable e) {
        Tool.copyError(src, e);
        return this;
    }

    protected final Page log(Throwable e) {
        if (platform() != null) {
            platform().log(e);
        }
        return this;
    }

    protected final Page log(Exception e) {
        if (platform() != null) {
            platform().log(e);
        }
        return this;
    }

    protected final Page log(SException e) {
        if (platform() != null) {
            platform().log(e);
        }
        return this;
    }

    protected final Page log(String line) {
        if (platform() != null) {
            platform().log(line);
        }
        return this;
    }
}

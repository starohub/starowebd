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

import com.starohub.platies.Platies;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public abstract class DataSet {
    private jsx.seller.PSoftware _license;
    private BluePrint _blueprint;
    private TagReaderFactory _tagReaderFactory;

    private String _code;
    private String _name;
    private String _version;
    private String _desc;

    public DataSet(BluePrint blueprint, String licFile) {
        _blueprint = blueprint;
        setInfo();
        _license = createLicense(blueprint, licFile);
        createPages();
        _tagReaderFactory = createTagReaderFactory(blueprint, this);
    }

    protected abstract void createPages();

    public final jsx.seller.PSoftware license() {
        return _license;
    }

    public final BluePrint blueprint() {
        return _blueprint;
    }

    protected abstract void setInfo();
    protected abstract jsx.seller.PSoftware createLicense(BluePrint blueprint, String licFile);

    public String jsonData(String uri) throws Exception {
        return "{}";
    }

    public final String code() {
        return _code;
    }

    protected final DataSet code(String src) {
        _code = src;
        return this;
    }

    public final String name() {
        return _name;
    }

    protected final DataSet name(String src) {
        _name = src;
        return this;
    }

    public final String version() {
        return _version;
    }

    protected final DataSet version(String src) {
        _version = src;
        return this;
    }

    public final String desc() {
        return _desc;
    }

    protected final DataSet desc(String src) {
        _desc = src;
        return this;
    }

    public final Map mergeMap(String code, String dsCode, String path, String data, Map args) throws Exception {
        String template = data;
        if (data == null) {
            template = new String(loadTemplate(dsCode, path), "UTF-8");
        }
        Map jsonMap = new Platies().fromJson(_tagReaderFactory.create(code, dsCode, path, template, args), Map.class);
        return jsonMap;
    }

    public final java.util.List mergeList(String code, String dsCode, String path, String data, Map args) throws Exception {
        String template = data;
        if (data == null) {
            template = new String(loadTemplate(dsCode, path), "UTF-8");
        }
        java.util.List jsonList = new Platies().fromJson(_tagReaderFactory.create(code, dsCode, path, template, args), java.util.List.class);
        return jsonList;
    }

    public final String mergeJsonObject(String code, String dsCode, String path, String data, Map args) throws Exception {
        String template = data;
        if (data == null) {
            template = new String(loadTemplate(dsCode, path), "UTF-8");
        }
        Map jsonMap = new Platies().fromJson(_tagReaderFactory.create(code, dsCode, path, template, args), Map.class);
        String json = new Platies().toJson(jsonMap);
        return json;
    }

    public final String mergeJsonList(String code, String dsCode, String path, String data, Map args) throws Exception {
        String template = data;
        if (data == null) {
            template = new String(loadTemplate(dsCode, path), "UTF-8");
        }
        java.util.List jsonList = new Platies().fromJson(_tagReaderFactory.create(code, dsCode, path, template, args), java.util.List.class);
        String json = new Platies().toJson(jsonList);
        return json;
    }

    public final String mergeHtml(String code, String path, String data, Map args) throws Exception {
        String template = data;
        if (data == null) {
            template = new String(loadTemplate(code, path), "UTF-8");
        }
        try {
            Velocity.init();
        } catch (Throwable e) {
            if (blueprint().platform() != null) {
                blueprint().platform().log(e);
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

    protected abstract TagReaderFactory createTagReaderFactory(BluePrint blueprint, DataSet dataset);

    protected abstract byte[] loadResource(String code, String path);

    public abstract DataSet mergeFile(String code, String dsCode, String path, String data, Map args, String tagFile) throws Exception;

    protected final byte[] loadTemplate(String code, String path) {
        String filename = "/templates/" + path;
        return loadResource(code, filename);
    }
}

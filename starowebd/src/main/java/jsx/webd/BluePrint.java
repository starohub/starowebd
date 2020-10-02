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
import com.starohub.webd.sandbox.DefaultSBObject;
import jsb.SFile;
import jsb.webd.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BluePrint {
    private jsx.seller.PSoftware _license;
    private SSession _session;
    private PageFactory _pageFactory;
    private Map<String, Kernel> _kernelMap = new HashMap<>();
    private Map<String, ArtWork> _artworkMap = new HashMap<>();
    private Map<String, DataSet> _datasetMap = new HashMap<>();
    private Redirect _redirect;
    private VHost _host;
    private SBObject _sbObject;
    private Map _more;
    private WebDApi _api;
    private Platform _platform;

    private String _code;
    private String _name;
    private String _version;
    private String _desc;

    public BluePrint(Map more) {
        _session = (SSession)more.get("session");
        _host = (VHost)more.get("host");
        _host.session(_session);
        _host.blueprint(this);
        _more = more;
        _api = (WebDApi)more.get("api");
        _platform = (Platform)more.get("platform");
        more.remove("api");
        more.remove("session");
        more.remove("host");
        setInfo();
        _pageFactory = createPageFactory();
        _redirect = createRedirect();
        _license = createLicense(this, _host.blueprintLicense());
        createKernelMap(_kernelMap);
        createArtWorkMap(_artworkMap);
        createDataSetMap(_datasetMap);
        createPages(_pageFactory);
        syncVUsers();
    }

    public final Platform platform() {
        return _platform;
    }

    public final Map more() {
        return _more;
    }

    public final SSession session() {
        return _session;
    }

    public final VHost host() {
        return _host;
    }

    public final PageFactory pageFactory() {
        return _pageFactory;
    }

    private final PageFactory createPageFactory() {
        return new PageFactory(this);
    }

    private BluePrint syncVUsers() {
        for (int i = 0; i < host().users().size(); i++) {
            VUser u = host().users().get(i);
            u.load();
        }
        return this;
    }

    public final Redirect redirect() { return _redirect; }

    public final SBObject sbObject() {
        if (_sbObject == null) {
            String js = "function __exec__(data) {}";
            Map more = new HashMap();
            more.put("session", session());
            more.put("api", _api);
            if (more().containsKey("license.debug")) {
                more.put("license.debug", "true".equalsIgnoreCase(more().get("license.debug") + ""));
            }
            more.put("logger", new Logger(this));
            _sbObject = new DefaultSBObject(js, host().pageTimeout(), _api, session(), more);
        }
        return _sbObject;
    }

    protected final Redirect createRedirect() {
        return new jsx.webd.Redirect(this);
    }

    public final jsx.seller.PSoftware license() {
        return _license;
    }

    protected abstract void createPages(PageFactory pageFactory);
    protected abstract void setInfo();
    protected abstract jsx.seller.PSoftware createLicense(BluePrint blueprint, String licFile);
    public abstract SBluePrint createSBluePrint(SPackage pkg, BluePrint blueprint);
    public abstract SArtWork createSArtWork(SPackage pkg, SBluePrint blueprint, ArtWork artwork);
    public abstract SDataSet createSDataSet(SPackage pkg, SBluePrint blueprint, DataSet dataset);
    public abstract SKernel createSKernel(SPackage pkg, SBluePrint blueprint, Kernel kernel);

    protected final BluePrint createKernelMap(Map<String, Kernel> map) {
        try {
            String libDir = "/bpt/" + code() + "/kernel";
            SFile libFile = sbObject().sandbox().machine().mnt().newFile(libDir);
            if (!libFile.exists()) return this;
            List<SFile> modFiles = libFile.listFiles();

            for (int i = 0; i < modFiles.size(); i++) {
                SFile modDir = modFiles.get(i);
                if (!modDir.isFolder()) continue;
                String infoPath = modDir.path() + "/kernel.json";
                String json = sbObject().sandbox().machine().lang().newString(sbObject().sandbox().machine().mnt().newFile(infoPath).readFile(), "UTF-8");
                Map infoMap = sbObject().sandbox().machine().tool().jsonToMap(json);
                String code = infoMap.get("code").toString();
                String name = infoMap.get("name").toString();
                Class<?> clazz = Class.forName(code + ".Kernel");
                Class<? extends Kernel> newClass = clazz.asSubclass(Kernel.class);
                for (Constructor<?> ctor : newClass.getConstructors()) {
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    if (2 == paramTypes.length) {
                        Kernel kn = (Kernel) ctor.newInstance((BluePrint)this, libDir + "/" + code + "/license.lic");
                        if (kn.license() != null && kn.license().valid()) {
                            map.put(code, kn);
                            platform().log("Loaded kernel [" + code + "] (" + name + ") ...");
                        } else {
                            platform().log("Failed to load kernel [" + code + "] (" + name + "): Invalid license ...");
                            platform().log("License is null: " + (kn.license() == null));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            platform().log(e);
        }
        return this;
    }

    public final List<String> kernelList() {
        List<String> tag = new ArrayList<>();
        for (String key : _kernelMap.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public final Kernel kernel(String code) {
        if (_kernelMap.containsKey(code)) {
            return _kernelMap.get(code);
        } else {
            return null;
        }
    }

    protected final BluePrint createArtWorkMap(Map<String, ArtWork> map) {
        try {
            String libDir = "/bpt/" + code() + "/artwork";
            SFile libFile = sbObject().sandbox().machine().mnt().newFile(libDir);
            if (!libFile.exists()) return this;
            List<SFile> modFiles = libFile.listFiles();

            for (int i = 0; i < modFiles.size(); i++) {
                SFile modDir = modFiles.get(i);
                if (!modDir.isFolder()) continue;
                String infoPath = modDir.path() + "/artwork.json";
                String json = sbObject().sandbox().machine().lang().newString(sbObject().sandbox().machine().mnt().newFile(infoPath).readFile(), "UTF-8");
                Map infoMap = sbObject().sandbox().machine().tool().jsonToMap(json);
                String code = infoMap.get("code").toString();
                String name = infoMap.get("name").toString();
                Class<?> clazz = Class.forName(code + ".ArtWork");
                Class<? extends ArtWork> newClass = clazz.asSubclass(ArtWork.class);
                for (Constructor<?> ctor : newClass.getConstructors()) {
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    if (2 == paramTypes.length) {
                        ArtWork kn = (ArtWork) ctor.newInstance((BluePrint)this, libDir + "/" + code + "/license.lic");
                        if (kn.license() != null && kn.license().valid()) {
                            map.put(code, kn);
                            platform().log("Loaded artwork [" + code + "] (" + name + ") ...");
                        } else {
                            platform().log("Failed to load artwork [" + code + "] (" + name + "): Invalid license ...");
                            platform().log("License is null: " + (kn.license() == null));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            platform().log(e);
        }
        return this;
    }

    public final List<String> artworkList() {
        List<String> tag = new ArrayList<>();
        for (String key : _artworkMap.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public final ArtWork artwork(String code) {
        if (_artworkMap.containsKey(code)) {
            return _artworkMap.get(code);
        } else {
            return null;
        }
    }

    protected final BluePrint createDataSetMap(Map<String, DataSet> map) {
        try {
            String libDir = "/bpt/" + code() + "/dataset";
            SFile libFile = sbObject().sandbox().machine().mnt().newFile(libDir);
            if (!libFile.exists()) return this;
            List<SFile> modFiles = libFile.listFiles();

            for (int i = 0; i < modFiles.size(); i++) {
                SFile modDir = modFiles.get(i);
                if (!modDir.isFolder()) continue;
                String infoPath = modDir.path() + "/dataset.json";
                String json = sbObject().sandbox().machine().lang().newString(sbObject().sandbox().machine().mnt().newFile(infoPath).readFile(), "UTF-8");
                Map infoMap = sbObject().sandbox().machine().tool().jsonToMap(json);
                String code = infoMap.get("code").toString();
                String name = infoMap.get("name").toString();
                Class<?> clazz = Class.forName(code + ".DataSet");
                Class<? extends DataSet> newClass = clazz.asSubclass(DataSet.class);
                for (Constructor<?> ctor : newClass.getConstructors()) {
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    if (2 == paramTypes.length) {
                        DataSet kn = (DataSet) ctor.newInstance((BluePrint)this, libDir + "/" + code + "/license.lic");
                        if (kn.license() != null && kn.license().valid()) {
                            map.put(code, kn);
                            platform().log("Loaded dataset [" + code + "] (" + name + ") ...");
                        } else {
                            platform().log("Failed to load dataset [" + code + "] (" + name + "): Invalid license ...");
                            platform().log("License is null: " + (kn.license() == null));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            platform().log(e);
        }
        return this;
    }

    public final List<String> dataSetList() {
        List<String> tag = new ArrayList<>();
        for (String key : _datasetMap.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public final DataSet dataset(String code) {
        if (_datasetMap.containsKey(code)) {
            return _datasetMap.get(code);
        } else {
            return null;
        }
    }

    public final String code() {
        return _code;
    }

    protected final BluePrint code(String src) {
        _code = src;
        return this;
    }

    public final String name() {
        return _name;
    }

    protected final BluePrint name(String src) {
        _name = src;
        return this;
    }

    public final String version() {
        return _version;
    }

    protected final BluePrint version(String src) {
        _version = src;
        return this;
    }

    public final String desc() {
        return _desc;
    }

    protected final BluePrint desc(String src) {
        _desc = src;
        return this;
    }
}

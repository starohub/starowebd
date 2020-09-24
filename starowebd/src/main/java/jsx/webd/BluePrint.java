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
import jsb.SFile;
import jsb.webd.*;

import javax.xml.crypto.Data;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BluePrint {
    private jsx.seller.PSoftware _license;
    private WebDApi _api;
    private Map<String, Kernel> _kernelMap = new HashMap<>();
    private Map<String, ArtWork> _artWorkMap = new HashMap<>();
    private Map<String, DataSet> _dataSetMap = new HashMap<>();

    private String _code;
    private String _name;
    private String _version;
    private String _desc;

    public BluePrint(WebDApi api) {
        _api = api;
        setInfo();
        _license = createLicense(api, api.config().blueprintLicense());
        createKernelMap(_kernelMap);
        createArtWorkMap(_artWorkMap);
        createDataSetMap(_dataSetMap);
        createPages(api);
    }

    public WebDApi api() {
        return _api;
    }

    public SBObject sbObject() {
        return api().sbObject();
    }

    public Config config() { return api().config(); }

    public jsx.seller.PSoftware license() {
        return _license;
    }

    protected abstract void createPages(WebDApi api);
    protected abstract void setInfo();
    protected abstract jsx.seller.PSoftware createLicense(WebDApi api, String licFile);
    public abstract SBluePrint createSBluePrint(SPackage pkg, WebDApi api, BluePrint blueprint);
    public abstract SArtWork createSArtWork(SPackage pkg, WebDApi api, ArtWork artwork);
    public abstract SDataSet createSDataSet(SPackage pkg, WebDApi api, DataSet dataset);
    public abstract SKernel createSKernel(SPackage pkg, WebDApi api, Kernel kernel);

    protected BluePrint createKernelMap(Map<String, Kernel> map) {
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
                            api().config().platform().log("Loaded kernel [" + code + "] (" + name + ") ...");
                        } else {
                            api().config().platform().log("Failed to load kernel [" + code + "] (" + name + "): Invalid license ...");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            api().config().platform().log(e);
        }
        return this;
    }

    public List<String> kernelList() {
        List<String> tag = new ArrayList<>();
        for (String key : _kernelMap.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public Kernel kernel(String code) {
        if (_kernelMap.containsKey(code)) {
            return _kernelMap.get(code);
        } else {
            return null;
        }
    }

    protected BluePrint createArtWorkMap(Map<String, ArtWork> map) {
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
                            api().config().platform().log("Loaded artwork [" + code + "] (" + name + ") ...");
                        } else {
                            api().config().platform().log("Failed to load artwork [" + code + "] (" + name + "): Invalid license ...");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            api().config().platform().log(e);
        }
        return this;
    }

    public List<String> artWorkList() {
        List<String> tag = new ArrayList<>();
        for (String key : _artWorkMap.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public ArtWork artWork(String code) {
        if (_artWorkMap.containsKey(code)) {
            return _artWorkMap.get(code);
        } else {
            return null;
        }
    }

    protected BluePrint createDataSetMap(Map<String, DataSet> map) {
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
                            api().config().platform().log("Loaded dataset [" + code + "] (" + name + ") ...");
                        } else {
                            api().config().platform().log("Failed to load dataset [" + code + "] (" + name + "): Invalid license ...");
                        }
                    }
                }
            }
        } catch (Throwable e) {
            api().config().platform().log(e);
        }
        return this;
    }

    public List<String> dataSetList() {
        List<String> tag = new ArrayList<>();
        for (String key : _dataSetMap.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public DataSet dataSet(String code) {
        if (_dataSetMap.containsKey(code)) {
            return _dataSetMap.get(code);
        } else {
            return null;
        }
    }

    public String code() {
        return _code;
    }

    protected BluePrint code(String src) {
        _code = src;
        return this;
    }

    public String name() {
        return _name;
    }

    protected BluePrint name(String src) {
        _name = src;
        return this;
    }

    public String version() {
        return _version;
    }

    protected BluePrint version(String src) {
        _version = src;
        return this;
    }

    public String desc() {
        return _desc;
    }

    protected BluePrint desc(String src) {
        _desc = src;
        return this;
    }
}

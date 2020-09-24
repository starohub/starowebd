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

import jsb.SCustomVisiblePatternStore;
import jsb.SDefaultVisiblePatternStore;
import jsx.webd.BluePrint;
import jsx.webd.WebDApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SBluePrint {
    private jsb.webd.SPackage _pkg;
    private WebDApi _api;
    private BluePrint _blueprint;
    private Map<String, SArtWork> _artworkMap = new HashMap<>();
    private Map<String, SDataSet> _datasetMap = new HashMap<>();
    private Map<String, SKernel> _kernelMap = new HashMap<>();
    private SDefaultVisiblePatternStore _defaultVisibleStore;
    private SCustomVisiblePatternStore _customVisibleStore;

    public SBluePrint(jsb.webd.SPackage pkg, WebDApi api, BluePrint blueprint) {
        _pkg = pkg;
        _api = api;
        _blueprint = blueprint;
        _defaultVisibleStore = new SDefaultVisiblePatternStore();
        _customVisibleStore = new SCustomVisiblePatternStore();
        setupVisible();
        createArtWorkMap(_artworkMap);
        createDataSetMap(_datasetMap);
        createKernelMap(_kernelMap);
    }

    protected BluePrint blueprint() {
        return _blueprint;
    }

    public jsb.webd.SPackage pkg() {
        return _pkg;
    }

    protected WebDApi api() {
        return _api;
    }

    public boolean available() {
        return api().blueprint() != null;
    }

    public List<String> dataSetList() {
        if (!available()) return new ArrayList<>();
        return api().blueprint().dataSetList();
    }

    public SDataSet dataSet(String code) {
        if (_datasetMap.containsKey(code)) {
            return _datasetMap.get(code);
        } else {
            return null;
        }
    }

    public List<String> artWorkList() {
        if (!available()) return new ArrayList<>();
        return api().blueprint().artWorkList();
    }

    public SArtWork artWork(String code) {
        if (_artworkMap.containsKey(code)) {
            return _artworkMap.get(code);
        } else {
            return null;
        }
    }

    public List<String> kernelList() {
        if (!available()) return new ArrayList<>();
        return api().blueprint().kernelList();
    }

    public SKernel kernel(String code) {
        if (_kernelMap.containsKey(code)) {
            return _kernelMap.get(code);
        } else {
            return null;
        }
    }

    protected abstract void createArtWorkMap(Map<String, SArtWork> artworkMap);
    protected abstract void createDataSetMap(Map<String, SDataSet> datasetMap);
    protected abstract void createKernelMap(Map<String, SKernel> kernelMap);

    protected final SBluePrint defaultVisible(String pattern, boolean startsWith) {
        _defaultVisibleStore.visible(pattern, startsWith);
        return this;
    }

    protected final SBluePrint customVisible(String pattern, boolean startsWith) {
        _customVisibleStore.visible(pattern, startsWith);
        return this;
    }

    protected final SBluePrint defaultInvisible(String pattern, boolean startsWith) {
        _defaultVisibleStore.invisible(pattern, startsWith);
        return this;
    }

    protected final SBluePrint customInvisible(String pattern, boolean startsWith) {
        _customVisibleStore.invisible(pattern, startsWith);
        return this;
    }

    protected SBluePrint setupVisible() {
        return this;
    }

    public final SDefaultVisiblePatternStore defaultVisibleStore() {
        return _defaultVisibleStore.copy();
    }

    public final SCustomVisiblePatternStore customVisibleStore() {
        return _customVisibleStore.copy();
    }

    public final boolean visibleToScripts(String className) {
        if (_defaultVisibleStore.visibleToScripts(className)) return true;
        if (_customVisibleStore.visibleToScripts(className)) return true;

        List<String> codes;

        codes = artWorkList();
        for (String code : codes) {
            if (artWork(code).visibleToScripts(className)) return true;
        }

        codes = dataSetList();
        for (String code : codes) {
            if (dataSet(code).visibleToScripts(className)) return true;
        }

        codes = kernelList();
        for (String code : codes) {
            if (kernel(code).visibleToScripts(className)) return true;
        }

        return false;
    }

    public boolean invisibleToScripts(String className) {
        if (_defaultVisibleStore.invisibleToScripts(className)) return true;
        if (_customVisibleStore.invisibleToScripts(className)) return true;

        List<String> codes;

        codes = artWorkList();
        for (String code : codes) {
            if (artWork(code).invisibleToScripts(className)) return true;
        }

        codes = dataSetList();
        for (String code : codes) {
            if (dataSet(code).invisibleToScripts(className)) return true;
        }

        codes = kernelList();
        for (String code : codes) {
            if (kernel(code).invisibleToScripts(className)) return true;
        }

        return false;
    }
}

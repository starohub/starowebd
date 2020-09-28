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
import jsb.io.SException;
import jsx.webd.ArtWork;
import jsx.webd.PageResponse;
import java.util.Map;

public class SArtWork {
    private jsb.webd.SPackage _pkg;
    private ArtWork _artwork;
    private SBluePrint _blueprint;
    private SDefaultVisiblePatternStore _defaultVisibleStore;
    private SCustomVisiblePatternStore _customVisibleStore;

    public SArtWork(jsb.webd.SPackage pkg, SBluePrint blueprint, ArtWork artwork) {
        _pkg = pkg;
        _artwork = artwork;
        _blueprint = blueprint;
        _defaultVisibleStore = new SDefaultVisiblePatternStore();
        _customVisibleStore = new SCustomVisiblePatternStore();
        setupVisible();
    }

    protected final ArtWork artwork() {
        return _artwork;
    }

    public final jsb.webd.SPackage pkg() {
        return _pkg;
    }

    protected final SBluePrint blueprint() {
        return _blueprint;
    }

    public final SArtWork theme(PageResponse output, String code, String path, String data, Map args) throws SException {
        try {
            artwork().theme(output, code, path, data, args);
        } catch (Throwable e) {
            throw pkg().machine().io().newException(e);
        }
        return this;
    }

    public final SArtWork theme(Map outputMap, String code, String path, String data, Map args) throws SException {
        try {
            artwork().theme(outputMap, code, path, data, args);
        } catch (Throwable e) {
            throw pkg().machine().io().newException(e);
        }
        return this;
    }

    public final String mergeHtml(String code, String path, String data, Map args) throws SException {
        try {
            return artwork().mergeHtml(code, path, data, args);
        } catch (Throwable e) {
            throw pkg().machine().io().newException(e);
        }
    }

    public final SArtWork mergeFile(String code, String path, String data, Map args, String tagFile) throws SException {
        try {
            artwork().mergeFile(code, path, data, args, tagFile);
            return this;
        } catch (Throwable e) {
            throw pkg().machine().io().newException(e);
        }
    }

    protected final SArtWork defaultVisible(String pattern, boolean startsWith) {
        _defaultVisibleStore.visible(pattern, startsWith);
        return this;
    }

    protected final SArtWork customVisible(String pattern, boolean startsWith) {
        _customVisibleStore.visible(pattern, startsWith);
        return this;
    }

    protected final SArtWork defaultInvisible(String pattern, boolean startsWith) {
        _defaultVisibleStore.invisible(pattern, startsWith);
        return this;
    }

    protected final SArtWork customInvisible(String pattern, boolean startsWith) {
        _customVisibleStore.invisible(pattern, startsWith);
        return this;
    }

    protected SArtWork setupVisible() {
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
        return false;
    }

    public final boolean invisibleToScripts(String className) {
        if (_defaultVisibleStore.invisibleToScripts(className)) return true;
        if (_customVisibleStore.invisibleToScripts(className)) return true;
        return false;
    }
}

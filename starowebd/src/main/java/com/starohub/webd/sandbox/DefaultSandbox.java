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

package com.starohub.webd.sandbox;

import jsb.SMachine;
import jsx.webd.WebDApi;

import java.util.Map;

public class DefaultSandbox extends com.starohub.jsb.Sandbox {
    private WebDApi _api;

    public DefaultSandbox(String javascript, int timeout, WebDApi api, Map more) {
        super(javascript, timeout, api.config().dataFolder(), api.config().cfgReadonly(), api.config().cfgWritable(), api.config().cfgMounter(), more);
        _api = api;
    }

    @Override
    protected boolean customVisibleToScripts(String className) {
        if ("com.starohub.webd.sandbox.WebDMod".equals(className)) return true;
        if ("com.starohub.webd.sandbox.DefaultMachine".equals(className)) return true;
        if ("jsb.webd.SPackage".equals(className)) return true;
        if ("jsb.webd.SSession".equals(className)) return true;
        if ("jsb.webd.SBluePrint".equals(className)) return true;
        if ("jsb.webd.SArtWork".equals(className)) return true;
        if ("jsb.webd.SDataSet".equals(className)) return true;
        if ("jsb.webd.SRedirect".equals(className)) return true;
        if (pkg().blueprint().available()) {
            if (pkg().blueprint().visibleToScripts(className)) return true;
        }
        if (pkg().redirect().visibleToScripts(className)) return true;
        return false;
    }

    protected WebDMod mod() {
        return (WebDMod)machine().mod("webd");
    }

    protected jsb.webd.SPackage pkg() {
        return (jsb.webd.SPackage)mod().pkg("webd");
    }

    @Override
    protected boolean customInvisibleToScripts(String className) {
        if (pkg().blueprint().available()) {
            if (pkg().blueprint().invisibleToScripts(className)) return true;
        }
        if (pkg().redirect().invisibleToScripts(className)) return true;
        return false;
    }

    @Override
    protected SMachine createMachine(Map more) {
        return new DefaultMachine((WebDApi)more.get("api"), more);
    }
}

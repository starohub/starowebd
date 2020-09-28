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

import com.starohub.jsb.Sandbox;
import jsb.webd.SSession;
import jsx.webd.Config;
import jsx.webd.WebDApi;

import java.util.Map;

public class DefaultSBObject extends com.starohub.jsb.SBObject {
    public DefaultSBObject(String javascript, int timeout, WebDApi api, SSession session, Map more) {
        super(javascript, timeout, api.config().dataFolder(session), api.config().cfgReadonly(), api.config().cfgWritable(), api.config().cfgMounter(), more);
    }

    @Override
    protected Sandbox createSandbox(String javascript, int timeout, String cfgJsonReadonly, String cfgJsonWritable, String cfgJsonMounter, Map more) {
        SSession session = (SSession)more.get("session");
        WebDApi api = (WebDApi)more.get("api");
        return new DefaultSandbox(createBaseJS() + "\n\n" + javascript, timeout, api, session, more);
    }
}

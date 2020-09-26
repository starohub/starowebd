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

import com.starohub.webd.IHTTPSession;
import jsx.webd.WebDApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultSession extends jsb.webd.SSession {
    private IHTTPSession _session;

    public DefaultSession(WebDApi api, Object source) {
        super(api, source);
        if (source instanceof IHTTPSession) {
            _session = (IHTTPSession)source;
        }
        setup();
    }

    @Override
    protected String createHost() {
        String rawHost = getHostFromHeaders();
        int idx = rawHost.lastIndexOf(":");
        if (idx >= 0) {
            return rawHost.substring(0, idx);
        }
        return rawHost;
    }

    @Override
    protected int createPort() {
        String rawHost = getHostFromHeaders();
        int idx = rawHost.lastIndexOf(":");
        if (idx >= 0) {
            return Integer.parseInt(rawHost.substring(idx + 1));
        }
        return 80;
    }

    @Override
    protected String createSessionId() {
        if (_session != null) {
            String ts = _session.getCookies().read("SESSION_ID");
            if (ts == null) {
                ts = UUID.randomUUID().toString().replaceAll("-", "");
                _session.getCookies().set("SESSION_ID", ts, 1000 * 60 * 60 * 24 * 365 * 10);
            }
            return ts;
        }
        return null;
    }

    @Override
    protected String createUri() {
        if (_session != null) {
            return _session.getUri();
        }
        return null;
    }

    @Override
    protected String createMethod() {
        if (_session != null) {
            return _session.getMethod().name();
        }
        return null;
    }

    @Override
    protected Map<String, String> createParams() {
        if (_session != null) {
            return _session.getParms();
        }
        return null;
    }

    @Override
    protected Map<String, String> createHeaders() {
        if (_session != null) {
            return _session.getHeaders();
        }
        return null;
    }

    @Override
    public Map<String, String> createFiles() {
        if (_session != null) {
            Map<String, String> files = new HashMap<>();
            try {
                _session.parseBody(files);
            } catch (Throwable e) {
            }
            return files;
        }
        return null;
    }

    @Override
    public Map<String, String> posts() {
        return _session.getParms();
    }

    @Override
    public Map<String, String> gets() {
        return _session.getParms();
    }

    @Override
    public String queryString() {
        return _session.getQueryParameterString();
    }
}

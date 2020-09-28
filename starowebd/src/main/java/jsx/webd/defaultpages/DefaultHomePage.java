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

package jsx.webd.defaultpages;

import com.starohub.webd.*;
import com.starohub.webd.sandbox.webd.MasterPage;
import jsx.webd.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DefaultHomePage extends MasterPage {
    public DefaultHomePage(WebDApi api) {
        super(api,"system.default_home", "Default Home", "Default home page of StaroWebD.");
    }

    public boolean accepted(final jsb.webd.SSession session) {
        if (!config().hasDefaultHomePage()) return false;
        String path = session.uri().toLowerCase();
        if ("/".equalsIgnoreCase(path) || "/index.yo".equalsIgnoreCase(path)) {
            if (config().hasDefaultFileSystemPage()) {
                FileItem webDir = new FileItem(api().blueprint(session).sbObject(), "/");
                if (!webDir.kind().equalsIgnoreCase("not_found")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/index.yo");
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    public PageRequest sessionToRequest(final jsb.webd.SSession session) {
        PageRequest pr = requestPattern().clone();
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            Map args = new HashMap();
            theme(ps,"Home.vm", args);
        } catch (Exception e) {
            log("Failed to view page: " + stacktrace(e));
            copyError(ps, e);
        }
        return ps;
    }
}

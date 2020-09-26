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

import com.starohub.webd.Tool;
import com.starohub.webd.sandbox.webd.PseudoSession;
import jsb.webd.SSession;
import jsx.webd.*;

import java.util.logging.Level;

public class DefaultRedirectPage extends Page {
    public DefaultRedirectPage(WebDApi api) {
        super(api,"system.default_redirect", "Default Redirect", "Default redirect page of StaroWebD.");
    }

    public boolean accepted(final jsb.webd.SSession session) {
        String uri = session.uri();
        String newUri = api().redirect().redirect(uri);
        if (newUri == null) return false;
        return true;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/**");
        pr.put(new PageItem("uri", "URI of page", "", String.class.getName(), "", ""));
        pr.put(new PageItem("error", "Stacktrace of error", "", String.class.getName(), "", ""));
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    public PageRequest sessionToRequest(final jsb.webd.SSession session) {
        PageRequest pr = requestPattern().clone();
        pr.get("uri").value(session.uri());
        pr.get("_session").value(session);
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            SSession css = (SSession)request.get("_session").value();
            String uri = request.get("uri").value().toString();
            String newUri = api().redirect().redirect(uri);
            if (newUri != null) {
                SSession pss = new PseudoSession(api(), css, newUri, css.method(), css.host(), css.port(), css.headers(), css.params(), css.files(), css.sessionId());
                return api().originPageFactory().run(pss);
            }
            throw new Exception("Not recognized item.");
        } catch (Throwable e) {
            Tool.LOG.log(Level.SEVERE, "Failed to view page: ", e);
            config().platform().log("Failed to view page: " + Tool.stacktrace(e));
            Tool.copyError(ps, e);
        }
        return ps;
    }
}

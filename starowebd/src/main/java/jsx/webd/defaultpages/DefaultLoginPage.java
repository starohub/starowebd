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
import com.starohub.webd.sandbox.webd.MasterPage;
import jsb.webd.SSession;
import jsx.webd.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DefaultLoginPage extends MasterPage {
    public DefaultLoginPage(WebDApi api) {
        super(api,"system.default_login", "Default Login Page", "Provide authentication.");
    }

    public boolean accepted(final jsb.webd.SSession session) {
        VHost vh = config().vhostList().find(session.host());
        if (session.proxyHost() != null) {
            vh = config().vhostList().find(session.proxyHost());
            if (vh == null) return false;
        } else {
            if (vh == null) return false;
            if (!vh.passwordProtected()) return false;
            if (api().sessionData().getOnline(session)) return false;
        }
        String path = session.uri().toLowerCase();
        if (path.startsWith("/login.yo")) {
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/public-ip.yo");
        pr.put(new PageItem("returnUrl", "Return Url", "Return url", "get", "", ""));
        pr.put(new PageItem("username", "Username", "Username", "get", "", ""));
        pr.put(new PageItem("password", "Password", "Password", "get", "", ""));
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    public PageRequest sessionToRequest(final jsb.webd.SSession session) {
        PageRequest pr = requestPattern().clone();
        pr.get("_session").value(session);

        String[] fieldArr = new String[] { "username", "password", "returnUrl" };
        for (String field : fieldArr) {
            if (session.params().containsKey(field)) {
                pr.get(field).value(session.params().get(field));
            } else {
                pr.get(field).value("");
            }
        }
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            SSession session = (SSession)request.get("_session").value();
            String returnUrl = request.get("returnUrl").value() + "";
            String username = request.get("username").value() + "";
            String password = request.get("password").value() + "";
            String message = "";
            if ("POST".equalsIgnoreCase(session.method())) {
                VHost vh = config().vhostList().find(session.host());
                if (session.proxyHost() != null) {
                    vh = config().vhostList().find(session.proxyHost());
                }
                if (!vh.validate(username, password)) {
                    message = "Username and password do not match!";
                } else {
                    api().sessionData().setOnline(session, true);
                    api().sessionData().set(session, "username_" + vh.host(), username);
                    if (returnUrl == null || returnUrl.trim().length() == 0) {
                        returnUrl = "/";
                    }
                    ps.get("_redirect").value(returnUrl);
                    return ps;
                }
            }

            Map args = new HashMap();
            args.put("returnUrl", returnUrl);
            args.put("username", username);
            args.put("password", password);
            args.put("message", message);
            theme(ps, "Login.vm", args);
        } catch (Exception e) {
            platform().log("Failed to view page: " + stacktrace(e));
            copyError(ps, e);
        }
        return ps;
    }
}

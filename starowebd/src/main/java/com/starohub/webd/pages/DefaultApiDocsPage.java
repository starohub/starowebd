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

package com.starohub.webd.pages;

import com.starohub.webd.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class DefaultApiDocsPage extends Page {

    public DefaultApiDocsPage(Config config) {
        super(config,"system.default_api_docs", "Default API Docs", "Default API documents page of StaroWebD.");
    }

    public boolean accepted(final IHTTPSession session) {
        if (!config().hasDefaultAPIDocsPage()) return false;
        String path = session.getUri().toLowerCase();
        if ("/api-docs.yo".equalsIgnoreCase(path)) {
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/api-docs.yo");
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    public PageRequest sessionToRequest(final IHTTPSession session) {
        PageRequest pr = requestPattern().clone();
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            Map args = new HashMap();

            List<Map> api = new ArrayList<>();
            List<String> codes = config().pageFactory().pages();
            for (int i = 0; i < codes.size(); i++) {
                Page page = config().pageFactory().get(codes.get(i));
                if (page == null) continue;
                if (!page.docVisible()) continue;
                Map p = new HashMap();
                p.put("code", page.code());
                p.put("name", page.name());
                p.put("desc", page.desc());
                p.put("uri", page.requestPattern().uri());
                p.put("method", page.requestPattern().method());

                List rq = new ArrayList();
                List<String> items = page.requestPattern().items();
                for (int j = 0; j < items.size(); j++) {
                    String code = items.get(j);
                    PageItem pi = page.requestPattern().get(code);
                    if (pi.system() && !page.requestPattern().systemVisible()) {
                        continue;
                    }
                    if (pi.hidden() && !page.requestPattern().hiddenVisible()) {
                        continue;
                    }
                    rq.add(pi);
                }
                p.put("request", rq);

                List rs = new ArrayList();
                items = page.responsePattern().items();
                for (int j = 0; j < items.size(); j++) {
                    String code = items.get(j);
                    PageItem pi = page.responsePattern().get(code);
                    if (pi.system() && !page.responsePattern().systemVisible()) {
                        continue;
                    }
                    if (pi.hidden() && !page.responsePattern().hiddenVisible()) {
                        continue;
                    }
                    rs.add(pi);
                }
                p.put("response", rs);
                api.add(p);
            }
            args.put("api", api);

            theme(ps,"APIDocs.vm", args);
        } catch (Exception e) {
            Tool.LOG.log(Level.SEVERE, "Failed to view page: ", e);
            Tool.copyError(ps, e);
        }
        return ps;
    }
}

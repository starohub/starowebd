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

import jsx.webd.defaultpages.*;

import java.util.ArrayList;
import java.util.List;

public class PageFactory {

    private WebDApi _api;
    private List<Page> _pageList = new ArrayList<Page>();

    public PageFactory(WebDApi api) {
        _api = api;
        load();
    }

    public WebDApi api() {
        return _api;
    }

    protected void load() {
        _pageList.add(createFileSystemPage());
        _pageList.add(createPublicIPPage());
        _pageList.add(createApiDocsPage());
        _pageList.add(createErrorPage());
        _pageList.add(createNotFoundPage());
        _pageList.add(createHomePage());
    }

    protected Page createFileSystemPage() {
        return new DefaultFileSystemPage(api());
    }

    protected Page createPublicIPPage() {
        return new PublicIPPage(api());
    }

    protected Page createApiDocsPage() {
        return new DefaultApiDocsPage(api());
    }

    protected Page createErrorPage() {
        return new DefaultErrorPage(api());
    }

    protected Page createNotFoundPage() {
        return new DefaultNotFoundPage(api());
    }

    protected Page createHomePage() {
        return new DefaultHomePage(api());
    }

    public List<String> pages() {
        List<String> tag = new ArrayList<>();
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            tag.add(page.code());
        }
        return tag;
    }

    public Page get(String code) {
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            if (page.code().equalsIgnoreCase(code)) {
                return page;
            }
        }
        return null;
    }

    public PageFactory add(Page p) {
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            if (page.code().equalsIgnoreCase(p.code())) {
                return this;
            }
        }
        _pageList.add(p);
        return this;
    }

    public PageResponse run(final jsb.webd.SSession session) {
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            if (page.accepted(session)) {
                PageRequest pr = page.sessionToRequest(session);
                if (pr != null) {
                    PageResponse ps = page.run(pr);
                    if (ps != null) {
                       return ps;
                    }
                }
            }
        }
        return null;
    }

    public PageResponse run(final String code, final jsb.webd.SSession session) {
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            if (page.accepted(code)) {
                PageRequest pr = page.sessionToRequest(session);
                if (pr != null) {
                    PageResponse ps = page.run(pr);
                    if (ps != null) {
                        return ps;
                    }
                }
            }
        }
        return null;
    }

    public PageResponse run(final String code, PageRequest pr) {
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            if (page.accepted(code)) {
                return page.run(pr);
            }
        }
        return null;
    }

    public PageRequest sessionToRequest(final String code, final jsb.webd.SSession session) {
        for (int i = 0; i < _pageList.size(); i++) {
            Page page = _pageList.get(i);
            if (page.accepted(code)) {
                PageRequest pr = page.sessionToRequest(session);
                if (pr != null) {
                    return pr;
                }
            }
        }
        return null;
    }
}

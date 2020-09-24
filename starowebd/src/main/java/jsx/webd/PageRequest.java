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

import com.starohub.webd.IHTTPSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRequest {
    private String _code;
    private String _name;
    private String _desc;
    private String _method;
    private String _uri;
    private Map<String, PageItem> _items;
    private boolean _systemVisible;
    private boolean _hiddenVisible;

    public PageRequest(String code, String name, String desc, String method, String uri) {
        _code = code;
        _name = name;
        _desc = desc;
        _method = method;
        _uri = uri;
        _items = new HashMap<>();
        _systemVisible = false;
        _hiddenVisible = false;
        setup();
    }

    protected PageRequest setup() {
        put(new PageItem("_session", "Transferred Session", "Session which is transferred. NULL if not used.", IHTTPSession.class.getName(), null, null));
        get("_session").system(true);
        return this;
    }

    public boolean hiddenVisible() {
        return _hiddenVisible;
    }

    public PageRequest hiddenVisible(boolean src) {
        _hiddenVisible = src;
        return this;
    }

    public boolean systemVisible() {
        return _systemVisible;
    }

    public PageRequest systemVisible(boolean src) {
        _systemVisible = src;
        return this;
    }

    public boolean has(String code) {
        if (_items.containsKey(code)) {
            PageItem pi = _items.get(code);
            return pi.value() != null;
        } else {
            return false;
        }
    }

    public PageRequest put(PageItem src) {
        _items.put(src.code(), src);
        return this;
    }

    public PageItem get(String code) {
        return _items.get(code);
    }

    public List<String> items() {
        List<String> tag = new ArrayList<>();
        for (String key : _items.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public String uri() {
        return _uri;
    }

    public PageRequest uri(String src) {
        _uri = src;
        return this;
    }

    public String code() {
        return _code;
    }

    public PageRequest code(String src) {
        _code = src;
        return this;
    }

    public String name() {
        return _name;
    }

    public PageRequest name(String src) {
        _name = src;
        return this;
    }

    public String desc() {
        return _desc;
    }

    public PageRequest desc(String src) {
        _desc = src;
        return this;
    }

    public String method() {
        return _method;
    }

    public PageRequest method(String src) {
        _method = src;
        return this;
    }

    public PageRequest clone() {
        PageRequest pr = new PageRequest(code(), name(), desc(), method(), uri());
        List<String> items = items();
        for (int i = 0; i < items.size(); i++) {
            PageItem pi = get(items.get(i));
            pr.put(pi.clone());
        }
        return pr;
    }

    public Map toMap() {
        Map tag = new HashMap();
        List<String> items = items();
        for (int i = 0; i < items.size(); i++) {
            PageItem pi = get(items.get(i));
            tag.put(pi.code(), pi.value());
        }
        return tag;
    }

    public PageRequest fromMap(Map src) {
        List<String> items = items();
        for (int i = 0; i < items.size(); i++) {
            PageItem pi = get(items.get(i));
            if (src.containsKey(pi.code())) {
                pi.value(src.get(pi.code()));
            }
        }
        return this;
    }
}

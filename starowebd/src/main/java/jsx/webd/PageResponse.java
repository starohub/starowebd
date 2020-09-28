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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageResponse {
    private String _code;
    private String _name;
    private String _desc;
    private Map<String, PageItem> _items;
    private boolean _systemVisible;
    private boolean _hiddenVisible;

    public PageResponse(String code, String name, String desc) {
        _code = code;
        _name = name;
        _desc = desc;
        _items = new HashMap<>();
        _systemVisible = false;
        _hiddenVisible = false;
        setup();
    }

    private PageResponse setup() {
        put(new PageItem("_redirect", "Redirected URL", "URL to be redirected. NULL if not used.", String.class.getName(), null, null));
        put(new PageItem("_error", "Error Stacktrace", "Stacktrace of thrown exception. NULL if not used.", String.class.getName(), null, null));
        put(new PageItem("_return_list", "Returned Array List", "Array list which is returned. NULL if not used.", List.class.getName(), null, null));
        put(new PageItem("_return_html", "Returned HTML", "HTML code which is returned. NULL if not used.", String.class.getName(), null, null));
        put(new PageItem("_return_bytes", "Returned Byte Array in Base64 String", "Byte array in base64 string which is returned. NULL if not used.", String.class.getName(), null, null));
        put(new PageItem("_return_mime", "Returned Mime Type", "Mime type which is returned. NULL if not used.", String.class.getName(), null, null));
        put(new PageItem("_return_json", "Returned JSON Object", "JSON object which is returned. NULL if not used.", String.class.getName(), null, null));
        get("_redirect").system(true);
        get("_error").system(true);
        get("_return_list").system(true);
        get("_return_html").system(true);
        get("_return_bytes").system(true);
        get("_return_mime").system(true);
        return this;
    }

    public final boolean hiddenVisible() {
        return _hiddenVisible;
    }

    public final PageResponse hiddenVisible(boolean src) {
        _hiddenVisible = src;
        return this;
    }

    public final boolean systemVisible() {
        return _systemVisible;
    }

    public final PageResponse systemVisible(boolean src) {
        _systemVisible = src;
        return this;
    }

    public final boolean has(String code) {
        if (_items.containsKey(code)) {
            PageItem pi = _items.get(code);
            return pi.value() != null;
        } else {
            return false;
        }
    }

    public final PageResponse put(PageItem src) {
        _items.put(src.code(), src);
        return this;
    }

    public final PageItem get(String code) {
        return _items.get(code);
    }

    public final List<String> items() {
        List<String> tag = new ArrayList<>();
        for (String key : _items.keySet()) {
            tag.add(key);
        }
        return tag;
    }

    public final String code() {
        return _code;
    }

    public final PageResponse code(String src) {
        _code = src;
        return this;
    }

    public final String name() {
        return _name;
    }

    public final PageResponse name(String src) {
        _name = src;
        return this;
    }

    public final String desc() {
        return _desc;
    }

    public final PageResponse desc(String src) {
        _desc = src;
        return this;
    }

    public final PageResponse clone() {
        PageResponse pr = new PageResponse(code(), name(), desc());
        List<String> items = items();
        for (int i = 0; i < items.size(); i++) {
            PageItem pi = get(items.get(i));
            pr.put(pi.clone());
        }
        return pr;
    }

    public final Map toMap() {
        Map tag = new HashMap();
        List<String> items = items();
        for (int i = 0; i < items.size(); i++) {
            PageItem pi = get(items.get(i));
            tag.put(pi.code(), pi.value());
        }
        return tag;
    }

    public final PageResponse fromMap(Map src) {
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

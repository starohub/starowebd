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

import com.starohub.webd.Tool;

public class PageItem {
    private String _code;
    private String _name;
    private String _desc;
    private String _type;
    private Object _value;
    private Object _defaultValue;
    private boolean _system;
    private boolean _hidden;

    public PageItem(String code, String name, String desc, String type, Object value, Object defaultValue) {
        _code = code;
        _name = name;
        _desc = desc;
        _type = type;
        _value = value;
        _defaultValue = defaultValue;
        _system = false;
        _hidden = false;
    }

    public final boolean hidden() {
        return _hidden;
    }

    public final PageItem hidden(boolean src) {
        _hidden = src;
        return this;
    }

    public final boolean system() {
        return _system;
    }

    public final PageItem system(boolean src) {
        _system = src;
        return this;
    }

    public final String code() {
        return _code;
    }

    public final PageItem code(String src) {
        _code = src;
        return this;
    }

    public final String name() {
        return _name;
    }

    public final PageItem name(String src) {
        _name = src;
        return this;
    }

    public final String desc() {
        return _desc;
    }

    public final PageItem desc(String src) {
        _desc = src;
        return this;
    }

    public final String type() {
        return _type;
    }

    public final PageItem type(String src) {
        _type = src;
        return this;
    }

    public final Object value() {
        return _value;
    }

    public final PageItem value(Object src) {
        _value = src;
        return this;
    }

    public final Object defaultValue() {
        return _defaultValue;
    }

    public final String defaultValueString() {
        return Tool.toJson(defaultValue());
    }

    public final PageItem defaultValue(Object src) {
        _defaultValue = src;
        return this;
    }

    public final PageItem clone() {
        return new PageItem(code(), name(), desc(), type(), value(), defaultValue());
    }
}

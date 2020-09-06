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

package com.starohub.webd;

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

    public boolean hidden() {
        return _hidden;
    }

    public PageItem hidden(boolean src) {
        _hidden = src;
        return this;
    }

    public boolean system() {
        return _system;
    }

    public PageItem system(boolean src) {
        _system = src;
        return this;
    }

    public String code() {
        return _code;
    }

    public PageItem code(String src) {
        _code = src;
        return this;
    }

    public String name() {
        return _name;
    }

    public PageItem name(String src) {
        _name = src;
        return this;
    }

    public String desc() {
        return _desc;
    }

    public PageItem desc(String src) {
        _desc = src;
        return this;
    }

    public String type() {
        return _type;
    }

    public PageItem type(String src) {
        _type = src;
        return this;
    }

    public Object value() {
        return _value;
    }

    public PageItem value(Object src) {
        _value = src;
        return this;
    }

    public Object defaultValue() {
        return _defaultValue;
    }

    public String defaultValueString() {
        return Tool.toJson(defaultValue());
    }

    public PageItem defaultValue(Object src) {
        _defaultValue = src;
        return this;
    }

    public PageItem clone() {
        return new PageItem(code(), name(), desc(), type(), value(), defaultValue());
    }
}

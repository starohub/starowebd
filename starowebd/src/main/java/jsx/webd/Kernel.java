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

package jsx.webd;

public abstract class Kernel {
    private jsx.seller.PSoftware _license;
    private BluePrint _blueprint;

    private String _code;
    private String _name;
    private String _version;
    private String _desc;

    public Kernel(BluePrint blueprint, String licFile) {
        _blueprint = blueprint;
        setInfo();
        _license = createLicense(blueprint, licFile);
        createPages();
    }

    public final jsx.seller.PSoftware license() {
        return _license;
    }

    public final BluePrint blueprint() {
        return _blueprint;
    }

    protected abstract void setInfo();
    protected abstract void createPages();
    protected abstract jsx.seller.PSoftware createLicense(BluePrint blueprint, String licFile);

    public final String code() {
        return _code;
    }

    protected final Kernel code(String src) {
        _code = src;
        return this;
    }

    public final String name() {
        return _name;
    }

    protected final Kernel name(String src) {
        _name = src;
        return this;
    }

    public final String version() {
        return _version;
    }

    protected final Kernel version(String src) {
        _version = src;
        return this;
    }

    public final String desc() {
        return _desc;
    }

    protected final Kernel desc(String src) {
        _desc = src;
        return this;
    }
}

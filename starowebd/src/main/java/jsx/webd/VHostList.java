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

import com.starohub.webd.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VHostList {
    private List<VHost> _hosts = new ArrayList<>();

    public List<VHost> hosts() {
        return _hosts;
    }

    public VHost find(String host) {
        for (int i = 0; i < _hosts.size(); i++) {
            if (host.equalsIgnoreCase(_hosts.get(i).host())) {
                return _hosts.get(i);
            }
        }
        return null;
    }

    public List toList() {
        List tag = new ArrayList();
        for (VHost h : _hosts) {
            tag.add(h.toMap());
        }
        return tag;
    }

    public VHostList fromList(List srcList) {
        _hosts = new ArrayList<>();
        for (int i = 0; i < srcList.size(); i++) {
            Map srcMap = Tool.listItemToMap(srcList, i);
            VHost h = new VHost(srcMap.get("host") + "", 80);
            h.fromMap(srcMap);
            _hosts.add(h);
        }
        return this;
    }
}

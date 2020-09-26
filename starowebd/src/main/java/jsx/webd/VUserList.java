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

public class VUserList {
    private List<VUser> _users = new ArrayList<>();
    private String _host;

    public VUserList(String host) {
        _host = host;
    }

    public String host() {
        return _host;
    }

    public List<VUser> users() {
        return _users;
    }

    public VUser find(String username) {
        for (int i = 0; i < _users.size(); i++) {
            if (username.equalsIgnoreCase(_users.get(i).username())) {
                return _users.get(i);
            }
        }
        return null;
    }

    public List toList() {
        List tag = new ArrayList();
        for (VUser h : _users) {
            tag.add(h.toMap());
        }
        return tag;
    }

    public VUserList fromList(List srcList) {
        _users = new ArrayList<>();
        for (int i = 0; i < srcList.size(); i++) {
            Map srcMap = Tool.listItemToMap(srcList, i);
            VUser h = new VUser(host());
            h.fromMap(null, srcMap);
            _users.add(h);
        }
        return this;
    }
}

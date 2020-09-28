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
import java.util.UUID;

public final class VUserList {
    private List<VUser> _users = new ArrayList<>();
    private VHost _host;
    private boolean _locked = false;
    private String _masterToken = UUID.randomUUID().toString().replaceAll("-", "");

    public final boolean locked() {
        return _locked;
    }

    public final VUserList lock(String masterToken) {
        if (_locked) return this;
        _locked = true;
        _masterToken = masterToken;
        return this;
    }

    public final VUserList save(VUser u, String masterToken) {
        if (!host().validToken(masterToken)) return this;
        if (u == null) return this;
        if (!u.locked()) return this;
        if (!u.validToken(masterToken)) return this;
        for (int i = 0; i < _users.size(); i++) {
            if (_users.get(i).username().equalsIgnoreCase(u.username())) {
                if (!_users.get(i).locked()) return this;
                if (!_users.get(i).validToken(masterToken)) return this;
                u.saveToFS(masterToken);
                _users.set(i, u);
            }
        }
        return this;
    }

    public final VUserList edit(VUser u, String masterToken) {
        if (!host().validToken(masterToken)) return this;
        if (u == null) return this;
        if (u.locked()) return this;
        for (int i = 0; i < _users.size(); i++) {
            if (_users.get(i).username().equalsIgnoreCase(u.username())) {
                if (u.locked()) return this;
                u.lock(masterToken);
                _users.set(i, u);
            }
        }
        return this;
    }

    public VUserList(VHost host) {
        _host = host;
    }

    public final VHost host() {
        return _host;
    }

    public final int size() {
        return _users.size();
    }

    public final VUser get(int idx) {
        return _users.get(idx);
    }

    public final VUserList add(VUser user) {
        if (locked()) return this;
        _users.add(user);
        return this;
    }

    public final VUserList add(VUser user, String masterToken) {
        if (!host().validToken(masterToken)) return this;
        if (user.locked()) return this;
        user.lock(masterToken);
        _users.add(user);
        return this;
    }

    public final VUser find(String username) {
        for (int i = 0; i < _users.size(); i++) {
            if (username.equalsIgnoreCase(_users.get(i).username())) {
                return _users.get(i);
            }
        }
        return null;
    }

    public final List toList() {
        List tag = new ArrayList();
        for (VUser h : _users) {
            tag.add(h.toMap());
        }
        return tag;
    }

    public final VUserList fromList(List srcList) {
        if (locked()) return this;
        _users = new ArrayList<>();
        for (int i = 0; i < srcList.size(); i++) {
            Map srcMap = Tool.listItemToMap(srcList, i);
            VUser h = new VUser(host());
            h.fromMap(srcMap);
            _users.add(h);
        }
        return this;
    }
}

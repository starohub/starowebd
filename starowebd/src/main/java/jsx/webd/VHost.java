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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VHost {
    private boolean _hasPageSender = false;
    private boolean _hasPageReceiver = false;
    private boolean _passwordProtected = false;
    private String _proxyEndpoint = null;
    private String _host;
    private int _port;
    private VUserList _users;

    public VHost(String host, int port) {
        _host = host;
        _port = port;
        _users = new VUserList(host);
    }

    public boolean validate(String username, String password) {
        VUser u = users().find(username);
        if (u == null) return false;
        return u.matched(password);
    }

    public boolean passwordProtected() {
        return _passwordProtected;
    }

    public VHost passwordProtected(boolean src) {
        _passwordProtected = src;
        return this;
    }

    public VUserList users() {
        return _users;
    }

    public int port() {
        return _port;
    }

    public VHost port(int src) {
        _port = src;
        return this;
    }

    public String host() {
        return _host;
    }

    public VHost host(String src) {
        _host = src;
        return this;
    }

    public String proxyEndpoint() {
        return _proxyEndpoint;
    }

    public VHost proxyEndpoint(String src) {
        _proxyEndpoint = src;
        return this;
    }

    public boolean hasPageProxy() {
        return hasPageReceiver() || hasPageSender();
    }

    public boolean hasPageSender() {
        return _hasPageSender;
    }

    public VHost hasPageSender(boolean src) {
        _hasPageSender = src;
        return this;
    }

    public boolean hasPageReceiver() {
        return _hasPageReceiver;
    }

    public VHost hasPageReceiver(boolean src) {
        _hasPageReceiver = src;
        return this;
    }

    public Map toMap() {
        Map tag = new HashMap();
        tag.put("host", host());
        tag.put("port", port());
        tag.put("hasPageReceiver", _hasPageReceiver ? "on" : "off");
        tag.put("hasPageSender", _hasPageSender ? "on" : "off");
        tag.put("proxyEndpoint", _proxyEndpoint);
        tag.put("passwordProtected", passwordProtected());
        tag.put("users", users().toList());
        return tag;
    }

    public VHost fromMap(Map srcMap) {
        if (srcMap.containsKey("passwordProtected")) {
            _passwordProtected = "on".equalsIgnoreCase(srcMap.get("passwordProtected") + "");
        }
        if (srcMap.containsKey("hasPageReceiver")) {
            _hasPageReceiver = "on".equalsIgnoreCase(srcMap.get("hasPageReceiver") + "");
        }
        if (srcMap.containsKey("hasPageSender")) {
            _hasPageSender = "on".equalsIgnoreCase(srcMap.get("hasPageSender") + "");
        }
        if (srcMap.containsKey("proxyEndpoint")) {
            _proxyEndpoint = srcMap.get("proxyEndpoint") + "";
        }
        if (srcMap.containsKey("host")) {
            _host = srcMap.get("host") + "";
        }
        if (srcMap.containsKey("port")) {
            _port = Integer.parseInt((srcMap.get("port") + "").replaceAll("\\.0", ""));
        }
        if (srcMap.containsKey("users")) {
            List srcList = Tool.mapItemToList(srcMap, "users");
            _users = new VUserList(host());
            _users.fromList(srcList);
        }
        return this;
    }
}

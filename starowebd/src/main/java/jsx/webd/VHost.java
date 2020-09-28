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
import jsb.webd.SSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class VHost {
    private boolean _hasPageSender = false;
    private boolean _hasPageReceiver = false;
    private boolean _passwordProtected = false;
    private String _proxyEndpoint = null;
    private String _proxyToken = null;
    private String _dataFolder = null;
    private String _host;
    private int _port;
    private VUserList _users;
    private SSession _session;
    private String _blueprintClass;
    private String _blueprintLicense;
    private BluePrint _blueprint;
    private int _pageTimeout = 60;
    private boolean _isMaster = false;
    private boolean _locked = false;
    private String _masterToken = UUID.randomUUID().toString().replaceAll("-", "");
    private VHostList _hostList;

    public final VHost saveUser(VUser u) {
        if (!locked()) return this;
        if (!isMaster()) return this;
        users().save(u, _masterToken);
        return this;
    }

    public final VHost editUser(VUser u) {
        if (!locked()) return this;
        if (!isMaster()) return this;
        users().edit(u, _masterToken);
        return this;
    }

    public final boolean locked() {
        return _locked;
    }

    public final VHost lock(String masterToken) {
        if (_locked) return this;
        _locked = true;
        _masterToken = masterToken;
        users().lock(masterToken);
        return this;
    }

    public final boolean validToken(String masterToken) {
        if (_hostList == null) return false;
        if (!_hostList.validToken(masterToken)) return false;
        if (!_masterToken.equalsIgnoreCase(masterToken)) return false;
        return true;
    }

    public final BluePrint blueprint() {
        return _blueprint;
    }

    public final VHost blueprint(BluePrint src) {
        if (locked()) {
            if (_blueprint == null) {
                _blueprint = src;
            }
            return this;
        }
        _blueprint = src;
        return this;
    }

    public VHost(String host, int port, VHostList hostList) {
        _hostList = hostList;
        _host = host;
        _port = port;
        _users = new VUserList(this);
    }

    public final String blueprintLicense() {
        return _blueprintLicense;
    }

    public final VHost blueprintLicense(String src) {
        if (locked()) return this;
        _blueprintLicense = src;
        return this;
    }

    public final String blueprintClass() {
        return _blueprintClass;
    }

    public final VHost blueprintClass(String src) {
        if (locked()) return this;
        _blueprintClass = src;
        return this;
    }

    public final VHost session(SSession session) {
        if (locked()) {
            if (_session == null) {
                _session = session;
            } else {
                if (session.proxyHost() != null) {
                    if (_session.proxyHost().equalsIgnoreCase(session.host())) {
                        _session = session;
                    }
                } else {
                    if (_session.host().equalsIgnoreCase(session.host())) {
                        _session = session;
                    }
                }
            }
            return this;
        }
        _session = session;
        return this;
    }

    public final SSession session() {
        return _session;
    }

    public final String dataFolder() {
        return _dataFolder;
    }

    public final VHost dataFolder(String src) {
        if (locked()) return this;
        _dataFolder = src;
        return this;
    }

    public final boolean validate(String username, String password) {
        VUser u = users().find(username);
        if (u == null) return false;
        return u.matched(password);
    }

    public final boolean passwordProtected() {
        return _passwordProtected;
    }

    public final VHost passwordProtected(boolean src) {
        if (locked()) return this;
        _passwordProtected = src;
        return this;
    }

    public final VUserList users() {
        return _users;
    }

    public final String proxyToken() {
        return _proxyToken;
    }

    public final VHost proxyToken(String src) {
        if (locked()) return this;
        _proxyToken = src;
        return this;
    }

    public final int port() {
        return _port;
    }

    public final VHost port(int src) {
        if (locked()) return this;
        _port = src;
        return this;
    }

    public final String host() {
        return _host;
    }

    public final VHost host(String src) {
        if (locked()) return this;
        _host = src;
        return this;
    }

    public final String proxyEndpoint() {
        return _proxyEndpoint;
    }

    public final VHost proxyEndpoint(String src) {
        if (locked()) return this;
        _proxyEndpoint = src;
        return this;
    }

    public final boolean hasPageProxy() {
        return hasPageReceiver() || hasPageSender();
    }

    public final boolean hasPageSender() {
        return _hasPageSender;
    }

    public final VHost hasPageSender(boolean src) {
        if (locked()) return this;
        _hasPageSender = src;
        return this;
    }

    public final boolean hasPageReceiver() {
        return _hasPageReceiver;
    }

    public final VHost hasPageReceiver(boolean src) {
        if (locked()) return this;
        _hasPageReceiver = src;
        return this;
    }

    public final int pageTimeout() {
        return _pageTimeout;
    }

    public final VHost pageTimeout(int src) {
        if (locked()) return this;
        _pageTimeout = src;
        return this;
    }

    public final boolean isMaster() {
        return _isMaster;
    }

    public final VHost isMaster(boolean src) {
        if (locked()) return this;
        _isMaster = src;
        return this;
    }

    public final Map toMap() {
        Map tag = new HashMap();
        tag.put("host", host());
        tag.put("port", port());
        tag.put("hasPageReceiver", hasPageReceiver() ? "on" : "off");
        tag.put("hasPageSender", hasPageSender() ? "on" : "off");
        tag.put("proxyEndpoint", proxyEndpoint());
        tag.put("passwordProtected", passwordProtected());
        tag.put("users", users().toList());
        tag.put("proxyToken", proxyToken());
        tag.put("dataFolder", dataFolder());
        tag.put("blueprintClass", blueprintClass());
        tag.put("blueprintLicense", blueprintLicense());
        tag.put("pageTimeout", pageTimeout());
        tag.put("isMaster", isMaster());
        return tag;
    }

    public final VHost fromMap(Map srcMap) {
        if (locked()) return this;
        if (srcMap.containsKey("isMaster")) {
            isMaster("true".equalsIgnoreCase(srcMap.get("isMaster") + ""));
        }
        if (srcMap.containsKey("passwordProtected")) {
            passwordProtected("on".equalsIgnoreCase(srcMap.get("passwordProtected") + ""));
        }
        if (srcMap.containsKey("hasPageReceiver")) {
            hasPageReceiver("on".equalsIgnoreCase(srcMap.get("hasPageReceiver") + ""));
        }
        if (srcMap.containsKey("hasPageSender")) {
            hasPageSender("on".equalsIgnoreCase(srcMap.get("hasPageSender") + ""));
        }
        if (srcMap.containsKey("proxyEndpoint")) {
            proxyEndpoint(srcMap.get("proxyEndpoint") + "");
        }
        if (srcMap.containsKey("dataFolder")) {
            dataFolder(srcMap.get("dataFolder") + "");
        }
        if (srcMap.containsKey("host")) {
            host(srcMap.get("host") + "");
        }
        if (srcMap.containsKey("proxyToken")) {
            proxyToken(srcMap.get("proxyToken") + "");
        }
        if (srcMap.containsKey("port")) {
            port(Integer.parseInt((srcMap.get("port") + "").replaceAll("\\.0", "")));
        }
        if (srcMap.containsKey("pageTimeout")) {
            pageTimeout(Integer.parseInt((srcMap.get("pageTimeout") + "").replaceAll("\\.0", "")));
        }
        if (srcMap.containsKey("blueprintClass")) {
            blueprintClass(srcMap.get("blueprintClass") + "");
        }
        if (srcMap.containsKey("blueprintLicense")) {
            blueprintLicense(srcMap.get("blueprintLicense") + "");
        }
        if (srcMap.containsKey("users")) {
            List srcList = Tool.mapItemToList(srcMap, "users");
            _users = new VUserList(this);
            _users.fromList(srcList);
        }
        return this;
    }
}

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
import jsb.SFile;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VUser {
    private String _username;
    private String _password;
    private boolean _encrypted;
    private VHost _host;
    private Map _more = new HashMap();
    private boolean _locked = false;
    private String _masterToken = UUID.randomUUID().toString().replaceAll("-", "");
    private boolean _loaded = false;

    public final boolean validToken(String masterToken) {
        if (host() == null) return false;
        if (!host().validToken(masterToken)) return false;
        if (!_masterToken.equalsIgnoreCase(masterToken)) return false;
        return true;
    }

    public final boolean locked() {
        return _locked;
    }

    public final VUser lock(String masterToken) {
        if (_locked) return this;
        _locked = true;
        _masterToken = masterToken;
        return this;
    }

    public final Map more() {
        return _more;
    }

    public final VUser more(Map src) {
        if (locked()) return this;
        _more = src;
        return this;
    }

    public VUser(VHost host) {
        _host = host;
    }

    public final VHost host() {
        return _host;
    }

    public final String username() {
        return _username;
    }

    public final VUser username(String src) {
        if (locked()) return this;
        _username = src;
        return this;
    }

    public final String password() {
        return _password;
    }

    public final VUser password(String src) {
        if (locked()) return this;
        _password = src;
        return this;
    }

    public final boolean encrypted() {
        return _encrypted;
    }

    public final VUser encrypted(boolean src) {
        if (locked()) return this;
        _encrypted = src;
        return this;
    }

    public final String md5(String src) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
            return myHash;
        } catch (Throwable e) {
            host().blueprint().platform().log(e);
            return src;
        }
    }

    public final boolean matched(String password) {
        if (encrypted()) {
            String md5 = md5(password);
            return md5.equals(password());
        } else {
            String md5 = md5(password());
            host().blueprint().platform().log("VUser is not encrypted, please encrypt it: {\"username\": " + username() + ", \"encrypted\": true, \"password\": " + md5 + "}");
            return password.equals(password());
        }
    }

    public final Map toMap() {
        Map tag = new HashMap();
        tag.put("username", username());
        tag.put("password", password());
        tag.put("encrypted", encrypted());
        tag.put("more", more());
        return tag;
    }

    public final VUser saveToFS(String masterToken) {
        try {
            if (!validToken(masterToken)) return this;
            String filename = "/etc/" + host().host() + "/" + username() + ".json";
            host().blueprint().sbObject().sandbox().machine().mnt().newFile("/etc/" + host().host()).mkdirs();
            SFile sfile = host().blueprint().sbObject().sandbox().machine().mnt().newFile(filename);
            sfile.writeFile(Tool.toJson(toMap()).getBytes("UTF-8"));
        } catch (Throwable e) {}
        return this;
    }

    public final VUser clone() {
        Map um = toMap();
        VUser u = new VUser(host());
        u.fromMap(um);
        return u;
    }

    public final VUser load() {
        if (_loaded) return this;
        _loaded = true;
        String filename = "/etc/" + host().host() + "/" + username() + ".json";
        host().blueprint().sbObject().sandbox().machine().mnt().newFile("/etc/" + host().host()).mkdirs();
        SFile sfile = host().blueprint().sbObject().sandbox().machine().mnt().newFile(filename);
        if (sfile.exists()) {
            try {
                String jsonStr = new String(sfile.readFile(), "UTF-8");
                Map srcMap2 = Tool.jsonToMap(jsonStr);
                if (srcMap2.containsKey("username")) {
                    _username = srcMap2.get("username") + "";
                }
                if (srcMap2.containsKey("password")) {
                    _password = srcMap2.get("password") + "";
                }
                if (srcMap2.containsKey("encrypted")) {
                    _encrypted = "true".equalsIgnoreCase(srcMap2.get("encrypted") + "");
                }
                if (srcMap2.containsKey("more")) {
                    _more = Tool.mapItemToMap(srcMap2, "more");
                }
            } catch (Throwable e) {
            }
        }
        return this;
    }

    public final VUser fromMap(Map srcMap) {
        if (locked()) return this;
        if (srcMap.containsKey("username")) {
            _username = srcMap.get("username") + "";
        }
        if (srcMap.containsKey("password")) {
            _password = srcMap.get("password") + "";
        }
        if (srcMap.containsKey("encrypted")) {
            _encrypted = "true".equalsIgnoreCase(srcMap.get("encrypted") + "");
        }
        if (srcMap.containsKey("more")) {
            _more = Tool.mapItemToMap(srcMap, "more");
        }
        return this;
    }
}

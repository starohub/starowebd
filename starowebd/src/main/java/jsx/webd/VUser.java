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

public class VUser {
    private String _username;
    private String _password;
    private boolean _encrypted;
    private String _host;
    private Map _more = new HashMap();

    public Map more() {
        return _more;
    }

    public VUser more(Map src) {
        _more = src;
        return this;
    }

    public VUser(String host) {
        _host = host;
    }

    public String host() {
        return _host;
    }

    public String username() {
        return _username;
    }

    public VUser username(String src) {
        _username = src;
        return this;
    }

    public String password() {
        return _password;
    }

    public VUser password(String src) {
        _password = src;
        return this;
    }

    public boolean encrypted() {
        return _encrypted;
    }

    public VUser encrypted(boolean src) {
        _encrypted = src;
        return this;
    }

    public String md5(String src) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            byte[] digest = md.digest();
            String myHash = DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
            return myHash;
        } catch (Throwable e) {
            return src;
        }
    }

    public boolean matched(String password) {
        if (encrypted()) {
            String md5 = md5(password);
            return md5.equals(password());
        } else {
            String md5 = md5(password());
            System.out.println("VUser is not encrypted, please encrypt it: {\"username\": " + username() + ", \"encrypted\": true, \"password\": " + md5 + "}");
            return password.equals(password());
        }
    }

    public Map toMap() {
        Map tag = new HashMap();
        tag.put("username", username());
        tag.put("password", password());
        tag.put("encrypted", encrypted());
        tag.put("more", more());
        return tag;
    }

    public VUser saveToFS(WebDApi api) {
        try {
            String filename = "/etc/" + host() + "/" + username() + ".json";
            api.sbObject().sandbox().machine().mnt().newFile("/etc/" + host()).mkdirs();
            SFile sfile = api.sbObject().sandbox().machine().mnt().newFile(filename);
            sfile.writeFile(Tool.toJson(toMap()).getBytes("UTF-8"));
        } catch (Throwable e) {}
        return this;
    }
    public VUser fromMap(WebDApi api, Map srcMap) {
        if (api == null) {
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
        } else {
            String filename = "/etc/" + host() + "/" + srcMap.get("username") + ".json";
            api.sbObject().sandbox().machine().mnt().newFile("/etc/" + host()).mkdirs();
            SFile sfile = api.sbObject().sandbox().machine().mnt().newFile(filename);
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
            } else {
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
            }
        }
        return this;
    }
}

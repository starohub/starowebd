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

import java.util.Map;

public class Redirect {
    private BluePrint _blueprint;

    public Redirect(BluePrint blueprint) {
        _blueprint = blueprint;
    }

    public final BluePrint blueprint() {
        return _blueprint;
    }

    public final String findJSRFile(String uri) {
        String path = uri;
        int end = path.length();
        int idx = path.lastIndexOf("/", end);
        while (idx >= 0) {
            path = path.substring(0, idx);
            try {
                SFile sfile = blueprint().sbObject().sandbox().machine().mnt().newFile(path + "/index.jsr");
                if (sfile.exists()) return path + "/index.jsr";
            } catch (Throwable e) {

            }
            end = idx;
            idx = path.lastIndexOf("/", end);
        }
        return null;
    }

    public final String redirect(String uri) {
        String jsrFile = findJSRFile(uri);
        if (jsrFile == null) return null;
        return redirect(jsrFile, uri);
    }

    public final String rootURI(String uri) {
        String jsrFile = findJSRFile(uri);
        if (jsrFile == null) return "/";
        return rootJSR(jsrFile);
    }

    public final String rootJSR(String jsrFile) {
        String root = jsrFile;
        int idx = root.lastIndexOf("/");
        if (idx >= 0) {
            root = jsrFile.substring(0, idx);
        }
        return root;
    }

    public final String redirect(String jsrFile, String uri) {
        String root = rootJSR(jsrFile);
        String path = uri.substring(root.length());
        try {
            SFile sfile = blueprint().sbObject().sandbox().machine().mnt().newFile(jsrFile);
            String jsonStr = new String(sfile.readFile(), "UTF-8");
            java.util.List rules = Tool.jsonToList(jsonStr);
            for (int i = 0; i < rules.size(); i++) {
                Map item = Tool.listItemToMap(rules, i);
                String find = item.get("find") + "";
                String repl = item.get("replace") + "";
                String newPath = path.replaceAll(find, repl);
                if (!newPath.equalsIgnoreCase(path)) {
                    return root + newPath;
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }
}

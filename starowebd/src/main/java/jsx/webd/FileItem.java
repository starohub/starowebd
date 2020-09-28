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

package jsx.webd;

import com.starohub.jsb.SBObject;
import com.starohub.webd.Tool;
import jsb.SFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class FileItem {
    private SBObject _sbobject;
    private String _name;
    private String _ext;
    private String _kind;
    private String _parent;
    private String _mime;
    private List<FileItem> _children = null;

    public final String name() { return _name; }
    public final FileItem name(String val) { _name = val; return this; }

    public final String ext() { return _ext; }
    public final FileItem ext(String val) { _ext = val; return this; }

    public final String kind() { return _kind; }
    public final FileItem kind(String val) { _kind = val; return this; }

    public final String parent() { return _parent; }
    public final FileItem parent(String val) { _parent = val; return this; }

    public final String mime() { return _mime; }
    //public final FileItem mime(String val) { _mime = val; return this; }

    public FileItem(SBObject sbobject, String path) {
        _sbobject = sbobject;
        filepath(path);
    }

    public FileItem(SBObject sbobject, String parent, String path) {
        _sbobject = sbobject;
        filepath(new File(parent, path).getAbsolutePath());
    }

    public final Map map() {
        Map tag = new HashMap();
        tag.put("name", _name);
        tag.put("ext", _ext);
        tag.put("kind", _kind);
        tag.put("parent", _parent);
        tag.put("mime", _mime);
        return tag;
    }

    public final FileItem map(Map val) {
        if (val.containsKey("name")) {
            _name = val.get("name").toString();
        }
        if (val.containsKey("ext")) {
            _ext = val.get("ext").toString();
        }
        if (val.containsKey("kind")) {
            _kind = val.get("kind").toString();
        }
        if (val.containsKey("parent")) {
            _parent = val.get("parent").toString();
        }
        if (val.containsKey("mime")) {
            _mime = val.get("mime").toString();
        }
        return this;
    }

    public final String filepath() {
        try {
            String path = _parent;
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.length() > 1) {
                if (!path.endsWith("/")) {
                    path += "/";
                }
            }
            return path + _name;
        } catch (Exception e) {
            return "/";
        }
    }

    public final FileItem mimeFromExt() {
        _mime = "application/download";
        if (_ext.equalsIgnoreCase("jpg")) {
            _mime = "image/jpeg";
        }
        if (_ext.equalsIgnoreCase("jpeg")) {
            _mime = "image/jpeg";
        }
        if (_ext.equalsIgnoreCase("png")) {
            _mime = "image/png";
        }
        if (_ext.equalsIgnoreCase("gif")) {
            _mime = "image/gif";
        }
        if (_ext.equalsIgnoreCase("css")) {
            _mime = "text/css";
        }
        if (_ext.equalsIgnoreCase("txt")) {
            _mime = "text/plain";
        }
        if (_ext.equalsIgnoreCase("js")) {
            _mime = "application/javascript";
        }
        if (_ext.equalsIgnoreCase("jsb")) {
            _mime = "application/javascript-sandbox";
        }
        if (_ext.equalsIgnoreCase("jsr")) {
            _mime = "application/javascript-redirect";
        }
        if (_ext.equalsIgnoreCase("html") || _ext.equalsIgnoreCase("htm")) {
            _mime = "text/html";
        }
        if (_ext.equalsIgnoreCase("jsm")) {
            _mime = "application/javascript-markup";
        }
        return this;
    }

    private String getParent(String path) {
        String tag = "/";
        int idx = path.lastIndexOf("/");
        if (idx >= 0) {
            tag = path.substring(0, idx);
        }
        return tag;
    }

    public final FileItem filepath(String path) {
        try {
            SFile file = _sbobject.sandbox().machine().mnt().newFile(path);
            _name = file.name();
            _parent = getParent(file.path());
            _ext = "";
            int idx = _name.lastIndexOf(".");
            if (idx >= 0) {
                _ext = _name.substring(idx + 1);
            }
            if (!file.exists()) {
                _kind = "not_found";
            } else if (file.isFolder()) {
                _kind = "folder";
            } else if (file.isFile()) {
                _kind = "file";
            }
            mimeFromExt();
        } catch (Exception e) {
            Tool.LOG.log(Level.WARNING, "Failed to parse filepath: ", e);
        }
        return this;
    }

    public final List<FileItem> children() {
        if (_children == null) {
            _children = new ArrayList<FileItem>();
            try {
                SFile file = _sbobject.sandbox().machine().mnt().newFile(filepath());
                List<SFile> files = file.listFiles();
                for (SFile f : files) {
                    String path = f.path();
                    FileItem fi = new FileItem(_sbobject, path);
                    _children.add(fi);
                }
            } catch (Exception e) {
                Tool.LOG.log(Level.SEVERE, "Failed to list folder: ", e);
                _children = new ArrayList<FileItem>();
            }
        }
        return _children;
    }
}

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

package jsb.webd;

import com.starohub.webd.Tool;
import jsb.SFile;
import jsb.io.SException;
import jsb.io.SOutputStream;
import jsx.webd.WebDApi;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class SSession {
    private WebDApi _api;
    private Object _source;
    protected String _sessionId;
    protected String _uri;
    protected String _method;
    protected String _host;
    protected int _port;
    protected Map<String, String> _params;
    protected Map<String, String> _headers;
    protected Map<String, String> _files;
    protected String _proxyHost;

    public final String proxyHost() {
        return _proxyHost;
    }

    public final SSession proxyHost(String src) {
        _proxyHost = src;
        return this;
    }

    public SSession(WebDApi api, Object source) {
        _api = api;
        _source = source;
    }

    protected final SSession setup() {
        _uri = createUri();
        _method = createMethod();
        _sessionId = createSessionId();
        _params = createParams();
        _headers = createHeaders();
        _files = createFiles();
        _host = createHost();
        _port = createPort();
        return this;
    }

    protected final WebDApi api() {
        return _api;
    }

    public Object source() {
        return _source;
    }

    protected abstract String createHost();

    public final String host() { return _host; }

    protected abstract int createPort();

    public final int port() { return _port; }

    public final String sessionId() {
        return _sessionId;
    }

    protected String createSessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    protected abstract String createUri();

    public final String uri() {
        return _uri;
    }

    protected abstract String createMethod();

    public final String method() {
        return _method;
    }

    public final Map<String, String> params() { return _params; };

    protected abstract Map<String, String> createParams();

    public final Map<String, String> headers() {
        return _headers;
    }

    protected abstract Map<String, String> createHeaders();

    public abstract Map<String, String> createFiles();

    public final Map<String, String> files() { return _files; }

    public abstract Map<String, String> posts();

    public abstract Map<String, String> gets();

    public abstract String queryString();

    public final SSession setData(String name, Object value) {
        api().sessionData().set(this, name, value);
        return this;
    }

    public final Object getData(String name, Object defValue) {
        return api().sessionData().get(this, name, defValue);
    }

    public final SSession clearData() {
        api().sessionData().clear(this);
        return this;
    }

    public final boolean hasFile(String name) {
        return files().containsKey(name);
    }

    public final SSession saveFile(String name, String path) throws SException {
        if (!hasFile(name)) return this;
        try {
            String parent = path;
            int idx = parent.lastIndexOf("/");
            if (idx >= 0) {
                parent = parent.substring(0, idx);
            }
            api().sbObject(this).sandbox().machine().mnt().newFile(parent).mkdirs();

            String tmpFile = files().get(name);
            FileInputStream fis = new FileInputStream(tmpFile);
            SFile sfile = api().sbObject(this).sandbox().machine().mnt().newFile(path);
            SOutputStream fos = sfile.outputStream();
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer, 0, buffer.length);
            while (read > 0) {
                fos.write(buffer, 0, read);
                read = fis.read(buffer, 0, buffer.length);
            }
            fis.close();
            fos.close();
        } catch (Throwable e) {
            throw api().sbObject(this).sandbox().machine().io().newException(e);
        }
        return this;
    }

    protected final String getHostFromHeaders() {
        String tag = "localhost:80";
        for (String key : _headers.keySet()) {
            if ("host".equalsIgnoreCase(key)) {
                tag = _headers.get(key);
            }
        }
        return tag;
    }

    public final Map toMap() {
        Map tag = new HashMap();
        tag.put("uri", uri());
        tag.put("sessionId", sessionId());
        tag.put("method", method());
        tag.put("host", host());
        tag.put("port", port());
        tag.put("params", params());
        tag.put("headers", headers());
        tag.put("files", files());
        tag.put("proxyHost", proxyHost());
        return tag;
    }

    public final SSession fromMap(Map srcMap) {
        _uri = srcMap.get("uri") + "";
        _method = srcMap.get("method") + "";
        _sessionId = srcMap.get("sessionId") + "";
        _host = srcMap.get("host") + "";
        if (srcMap.get("proxyHost") == null) {
            _proxyHost = null;
        } else {
            _proxyHost = srcMap.get("proxyHost") + "";
        }
        try {
            _port = Integer.parseInt((srcMap.get("port") + "").replaceAll("\\.0", ""));
        } catch (Exception e) {
            _port = 80;
        }
        _params = new HashMap<>();
        Map src = Tool.mapItemToMap(srcMap, "params");
        for (Object key : src.keySet()) {
            _params.put(key + "", src.get(key) + "");
        }
        _headers = new HashMap<>();
        src = Tool.mapItemToMap(srcMap, "headers");
        for (Object key : src.keySet()) {
            _headers.put(key + "", src.get(key) + "");
        }
        _files = new HashMap<>();
        src = Tool.mapItemToMap(srcMap, "files");
        for (Object key : src.keySet()) {
            _files.put(key + "", src.get(key) + "");
        }
        return this;
    }
}

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

package jsx.webd.defaultpages;

import com.starohub.jsb.SBObject;
import com.starohub.webd.*;
import com.starohub.webd.sandbox.DefaultMachine;
import com.starohub.webd.sandbox.DefaultSBObject;
import com.starohub.webd.sandbox.DefaultSandbox;
import jsb.SFile;
import jsb.io.SInputStream;
import jsx.webd.*;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.logging.Level;

public class DefaultFileSystemPage extends Page {
    public DefaultFileSystemPage(WebDApi api) {
        super(api,"system.default_file_system", "Default File System", "Default file system page of StaroWebD.");
    }

    public boolean accepted(final jsb.webd.SSession session) {
        if (!config().hasDefaultFileSystemPage()) return false;

        FileItem item = new FileItem(sbObject(), session.uri());
        if (item.kind().equalsIgnoreCase("not_found")) return false;

        return true;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/**");
        pr.put(new PageItem("uri", "URI of page which causes error", "", String.class.getName(), "/error.yo", "/error.yo"));
        pr.put(new PageItem("error", "Stacktrace of error", "", String.class.getName(), "", ""));
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    public PageRequest sessionToRequest(final jsb.webd.SSession session) {
        PageRequest pr = requestPattern().clone();
        pr.get("uri").value(session.uri());
        pr.get("_session").value(session);
        return pr;
    }

    public PageResponse run(PageRequest request) {
        PageResponse ps = responsePattern().clone();
        try {
            String uri = request.get("uri").value().toString().substring(1);
            if (uri.endsWith("/")) {
                uri = uri.substring(0, uri.length() - 1);
            }
            uri = "/" + uri;
            FileItem item = new FileItem(sbObject(), uri);

            if (item.kind().equalsIgnoreCase("folder")) {
                String filepath = item.filepath();
                if (filepath.endsWith("/")) {
                    filepath = filepath.substring(0, filepath.length() - 1);
                }
                FileItem indexItem = new FileItem(sbObject(), filepath + "/index.jsb");
                if (!indexItem.kind().equalsIgnoreCase("not_found") && indexItem.mime().equalsIgnoreCase("application/javascript-sandbox")) {
                    item = indexItem;
                }
            }
            if (item.mime().equalsIgnoreCase("application/javascript-sandbox")) {
                SFile file = sbObject().sandbox().machine().mnt().newFile(item.filepath());
                SInputStream fis = file.inputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read = fis.read(buffer, 0, buffer.length);
                while (read > 0) {
                    baos.write(buffer, 0, read);
                    read = fis.read(buffer, 0, buffer.length);
                }
                fis.close();
                String js = new String(baos.toByteArray(), "UTF-8");
                SBObject sbo = new DefaultSBObject(js, 60, api(), api().more());
                sbo.set("_script", item.name());
                Map iMap = new HashMap();
                if (uri.length() == 0) {
                    uri = "/";
                }
                iMap.put("uri", uri);
                iMap.put("session", request.get("_session").value());
                Map oMap = new HashMap();
                Object oRS = sbo.exec("main", iMap);
                if (oRS != null) {
                    oMap = (Map)oRS;
                    ps.fromMap(oMap);
                }
                return ps;
            }
            if (item.kind().equalsIgnoreCase("file")) {
                SFile file = sbObject().sandbox().machine().mnt().newFile(item.filepath());
                SInputStream fis = file.inputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read = fis.read(buffer, 0, buffer.length);
                while (read > 0) {
                    baos.write(buffer, 0, read);
                    read = fis.read(buffer, 0, buffer.length);
                }
                fis.close();
                String base64 = config().platform().encodeBase64(baos.toByteArray());
                baos.close();
                ps.get("_return_bytes").value(base64);
                ps.get("_return_mime").value(item.mime());
                return ps;
            }
            if (item.kind().equalsIgnoreCase("folder")) {
                Map args = new HashMap();
                List<FileItem> items = item.children();
                FileItem current = new FileItem(sbObject(), item.filepath());
                FileItem parent = new FileItem(sbObject(), item.parent());
                current.name("[ . ]");
                current.kind("current");
                parent.name("[ .. ]");
                parent.kind("parent");
                items.add(0, current);
                items.add(0, parent);
                args.put("items", items);
                args.put("folder", item.name());
                args.put("uri", uri.substring(1));
                theme(ps,"FileSystemFolder.vm", args);
                return ps;
            }

            throw new Exception("Not recognized item.");
        } catch (Throwable e) {
            Tool.LOG.log(Level.SEVERE, "Failed to view page: ", e);
            config().platform().log("Failed to view page: " + Tool.stacktrace(e));
            Tool.copyError(ps, e);
        }
        return ps;
    }
}

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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    private int _apiPort = 1103;
    private String _dataFolder;
    private String _stopSignalFile;
    private int _threadCount = 2000;
    private jsb.webd.SSession _session;
    private int _cookieExpires = 1000 * 60 * 60 * 24 * 365 * 10;
    private PageFactory _pageFactory;
    private boolean _hasDefaultHomePage = true;
    private boolean _hasPublicIPPage = true;
    private boolean _hasDefaultNotFoundPage = true;
    private boolean _hasDefaultErrorPage = true;
    private boolean _hasDefaultFileSystemPage = true;
    private boolean _hasDefaultAPIDocsPage = true;
    private boolean _hasDefaultIndexPage = true;
    private String _cfgReadonly = "{}";
    private String _cfgWritable = "{}";
    private String _cfgMounter = "{}";
    private Platform _platform = new Platform();
    private Map _more = new HashMap();
    private VHostList _vhostList = new VHostList();
    private int _maxWebDApi = 1;

    public final int maxWebDApi() {
        return _maxWebDApi;
    }

    public final Config maxWebDApi(int src) {
        _maxWebDApi = src;
        return this;
    }

    public final VHostList vhostList() {
        return _vhostList;
    }

    public final boolean hasDefaultIndexPage() {
        return _hasDefaultIndexPage;
    }

    public final Config hasDefaultIndexPage(boolean src) {
        _hasDefaultIndexPage = src;
        return this;
    }

    public final Map more() {
        return _more;
    }

    public final Config more(Map src) {
        _more = src;
        return this;
    }

    public Config() {
        _platform.config(this);
    }

    public final Platform platform() {
        return _platform;
    }

    public final Config platform(Platform src) {
        _platform = src;
        if (_platform.config() == null) {
            _platform.config(this);
        }
        return this;
    }

    public final String cfgReadonly() {
        return _cfgReadonly;
    }

    public final Config cfgReadonly(String val) {
        _cfgReadonly = val;
        return this;
    }

    public final String cfgWritable() {
        return _cfgWritable;
    }

    public final Config cfgWritable(String val) {
        _cfgWritable = val;
        return this;
    }

    public final String cfgMounter() {
        return _cfgMounter;
    }

    public final Config cfgMounter(String val) {
        _cfgMounter = val;
        return this;
    }

    public final boolean hasDefaultAPIDocsPage() {
        return _hasDefaultAPIDocsPage;
    }

    public final Config hasDefaultAPIDocsPage(boolean src) {
        _hasDefaultAPIDocsPage = src;
        return this;
    }

    public final boolean hasDefaultFileSystemPage() {
        return _hasDefaultFileSystemPage;
    }

    public final Config hasDefaultFileSystemPage(boolean val) {
        _hasDefaultFileSystemPage = val;
        return this;
    }

    public final boolean hasPublicIPPage() {
        return _hasPublicIPPage;
    }

    public final Config hasPublicIPPage(boolean val) {
        _hasPublicIPPage = val;
        return this;
    }

    public final boolean hasDefaultErrorPage() {
        return _hasDefaultErrorPage;
    }

    public final Config hasDefaultErrorPage(boolean val) {
        _hasDefaultErrorPage = val;
        return this;
    }

    public final boolean hasDefaultHomePage() {
        return _hasDefaultHomePage;
    }

    public final Config hasDefaultHomePage(boolean val) {
        _hasDefaultHomePage = val;
        return this;
    }

    public final boolean hasDefaultNotFoundPage() {
        return _hasDefaultNotFoundPage;
    }

    public final Config hasDefaultNotFoundPage(boolean val) {
        _hasDefaultNotFoundPage = val;
        return this;
    }

    public final PageFactory pageFactory() {
        return _pageFactory;
    }

    public final Config pageFactory(PageFactory pageFactory) {
        _pageFactory = pageFactory;
        return this;
    }

    public final int cookieExpires() {
        return _cookieExpires;
    }

    public final Config cookieExpires(int cookieExpires) {
        _cookieExpires = cookieExpires;
        return this;
    }

    public final int threadCount() { return _threadCount; }

    public final Config threadCount(int count) {
        _threadCount = count;
        return this;
    }

    public final int apiPort() {
        return _apiPort;
    }

    public final Config apiPort(int apiPort) {
        _apiPort = apiPort;
        return load();
    }

    public final String dataFolder(SSession session) {
        if (session == null) return _dataFolder;
        VHost vh = vhostList().find(session.host());
        if (session.proxyHost() != null) {
            vh = vhostList().find(session.proxyHost());
        }
        if (vh == null) return _dataFolder;
        if (vh.dataFolder() == null) return _dataFolder;
        return vh.dataFolder();
    }

    public final Config dataFolder(String dataFolder) {
        _dataFolder = dataFolder;
        return load();
    }

    public final String stopSignalFile() {
        return _stopSignalFile;
    }

    public final Config stopSignalFile(String filename) {
        _stopSignalFile = filename;
        return this;
    }

    public final Config load() {
        try {
            _stopSignalFile = new File(_dataFolder, "starowebd-" + apiPort() + ".stop").getAbsolutePath();
        } catch (Exception e) {
            Tool.LOG.log(Level.WARNING, "Failed to load config: ", e);
        }
        return this;
    }

    public final jsb.webd.SSession session() {
        return _session;
    }

    public final Config session(jsb.webd.SSession session) {
        _session = session;
        return this;
    }
}

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
    private boolean _hasPageSender = false;
    private boolean _hasPageReceiver = false;
    private String _cfgReadonly = "{}";
    private String _cfgWritable = "{}";
    private String _cfgMounter = "{}";
    private Platform _platform = new Platform();
    private String _blueprintClass = null;
    private BluePrint _blueprint;
    private String _blueprintLicense = null;
    private Map _more = new HashMap();
    private String _proxyEndpoint = null;
    private VHostList _vhostList = new VHostList();

    public VHostList vhostList() {
        return _vhostList;
    }

    /*
    public String proxyEndpoint() {
        return _proxyEndpoint;
    }

    public Config proxyEndpoint(String src) {
        _proxyEndpoint = src;
        return this;
    }

    public boolean hasPageProxy() {
        return hasPageReceiver() || hasPageSender();
    }

    public boolean hasPageSender() {
        return _hasPageSender;
    }

    public Config hasPageSender(boolean src) {
        _hasPageSender = src;
        return this;
    }

    public boolean hasPageReceiver() {
        return _hasPageReceiver;
    }

    public Config hasPageReceiver(boolean src) {
        _hasPageReceiver = src;
        return this;
    }

     */

    public boolean hasDefaultIndexPage() {
        return _hasDefaultIndexPage;
    }

    public Config hasDefaultIndexPage(boolean src) {
        _hasDefaultIndexPage = src;
        return this;
    }

    public Map more() {
        return _more;
    }

    public Config more(Map src) {
        _more = src;
        return this;
    }

    public Config() {
        _platform.config(this);
    }

    public String blueprintLicense() {
        return _blueprintLicense;
    }

    public Config blueprintLicense(String src) {
        _blueprintLicense = src;
        return this;
    }

    public BluePrint blueprint() {
        return _blueprint;
    }

    public Config blueprint(BluePrint src) {
        _blueprint = src;
        return this;
    }

    public String blueprintClass() {
        return _blueprintClass;
    }

    public Config blueprintClass(String src) {
        _blueprintClass = src;
        return this;
    }

    public Platform platform() {
        return _platform;
    }

    public Config platform(Platform src) {
        _platform = src;
        if (_platform.config() == null) {
            _platform.config(this);
        }
        return this;
    }

    public String cfgReadonly() {
        return _cfgReadonly;
    }

    public Config cfgReadonly(String val) {
        _cfgReadonly = val;
        return this;
    }

    public String cfgWritable() {
        return _cfgWritable;
    }

    public Config cfgWritable(String val) {
        _cfgWritable = val;
        return this;
    }

    public String cfgMounter() {
        return _cfgMounter;
    }

    public Config cfgMounter(String val) {
        _cfgMounter = val;
        return this;
    }

    public boolean hasDefaultAPIDocsPage() {
        return _hasDefaultAPIDocsPage;
    }

    public Config hasDefaultAPIDocsPage(boolean src) {
        _hasDefaultAPIDocsPage = src;
        return this;
    }

    public boolean hasDefaultFileSystemPage() {
        return _hasDefaultFileSystemPage;
    }

    public Config hasDefaultFileSystemPage(boolean val) {
        _hasDefaultFileSystemPage = val;
        return this;
    }

    public boolean hasPublicIPPage() {
        return _hasPublicIPPage;
    }

    public Config hasPublicIPPage(boolean val) {
        _hasPublicIPPage = val;
        return this;
    }

    public boolean hasDefaultErrorPage() {
        return _hasDefaultErrorPage;
    }

    public Config hasDefaultErrorPage(boolean val) {
        _hasDefaultErrorPage = val;
        return this;
    }

    public boolean hasDefaultHomePage() {
        return _hasDefaultHomePage;
    }

    public Config hasDefaultHomePage(boolean val) {
        _hasDefaultHomePage = val;
        return this;
    }

    public boolean hasDefaultNotFoundPage() {
        return _hasDefaultNotFoundPage;
    }

    public Config hasDefaultNotFoundPage(boolean val) {
        _hasDefaultNotFoundPage = val;
        return this;
    }

    public PageFactory pageFactory() {
        return _pageFactory;
    }

    public Config pageFactory(PageFactory pageFactory) {
        _pageFactory = pageFactory;
        return this;
    }

    public int cookieExpires() {
        return _cookieExpires;
    }

    public Config cookieExpires(int cookieExpires) {
        _cookieExpires = cookieExpires;
        return this;
    }

    public int threadCount() { return _threadCount; }

    public Config threadCount(int count) {
        _threadCount = count;
        return this;
    }

    public int apiPort() {
        return _apiPort;
    }

    public Config apiPort(int apiPort) {
        _apiPort = apiPort;
        return load();
    }

    public String dataFolder() {
        return _dataFolder;
    }

    public Config dataFolder(String dataFolder) {
        _dataFolder = dataFolder;
        return load();
    }

    public String stopSignalFile() {
        return _stopSignalFile;
    }

    public Config stopSignalFile(String filename) {
        _stopSignalFile = filename;
        return this;
    }

    public Config load() {
        try {
            _stopSignalFile = new File(_dataFolder, "starowebd-" + apiPort() + ".stop").getAbsolutePath();
        } catch (Exception e) {
            Tool.LOG.log(Level.WARNING, "Failed to load config: ", e);
        }
        return this;
    }

    public jsb.webd.SSession session() {
        return _session;
    }

    public Config session(jsb.webd.SSession session) {
        _session = session;
        return this;
    }
}

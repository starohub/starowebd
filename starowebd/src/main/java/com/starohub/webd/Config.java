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

package com.starohub.webd;

import java.io.File;
import java.util.logging.Level;

public class Config {
    private int _apiPort = 1103;
    private String _dataFolder;
    private String _stopSignalFile;
    private int _threadCount = 2000;
    private com.starohub.webd.IHTTPSession _session;
    private int _cookieExpires = 1000 * 60 * 60 * 24 * 365 * 10;
    private PageFactory _pageFactory;
    private boolean _hasDefaultHomePage = true;
    private boolean _hasPublicIPPage = true;
    private boolean _hasDefaultNotFoundPage = true;
    private boolean _hasDefaultErrorPage = true;
    private boolean _hasDefaultFileSystemPage = true;
    private boolean _hasDefaultAPIDocsPage = true;
    private String _cfgReadonly = "{}";
    private String _cfgWritable = "{}";
    private String _cfgMounter = "{}";

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

    public com.starohub.webd.IHTTPSession session() {
        return _session;
    }

    public Config session(com.starohub.webd.IHTTPSession session) {
        _session = session;
        return this;
    }
}

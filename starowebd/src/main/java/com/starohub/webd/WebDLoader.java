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

package com.starohub.webd;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class WebDLoader {
    private static WebDLoader __instance = null;
    public static WebDLoader instance() {
        return __instance;
    }

    private static final int DEFAULT_PORT = 1103;
    private static final int MAX_PORT = 9999;

    private WebD _api = null;
    private Config _config;

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                help();
            } else {
                String cfgFile = args[0];
                String json = Tool.readText(cfgFile);
                Map cfgMap = Tool.jsonToMap(json);
                Config cfg = new Config();
                if (cfgMap.containsKey("dataFolder")) {
                    cfg.dataFolder(cfgMap.get("dataFolder").toString());
                } else {
                    cfg.dataFolder(new File(System.getProperty("user.dir"), "dat").getAbsolutePath());
                }
                if (cfgMap.containsKey("hasDefaultHomePage")) {
                    cfg.hasDefaultHomePage(cfgMap.get("hasDefaultHomePage").equals("on"));
                } else {
                    cfg.hasDefaultHomePage(false);
                }
                if (cfgMap.containsKey("hasDefaultNotFoundPage")) {
                    cfg.hasDefaultNotFoundPage(cfgMap.get("hasDefaultNotFoundPage").equals("on"));
                } else {
                    cfg.hasDefaultNotFoundPage(false);
                }
                if (cfgMap.containsKey("hasDefaultErrorPage")) {
                    cfg.hasDefaultErrorPage(cfgMap.get("hasDefaultErrorPage").equals("on"));
                } else {
                    cfg.hasDefaultErrorPage(false);
                }
                if (cfgMap.containsKey("hasDefaultFileSystemPage")) {
                    cfg.hasDefaultFileSystemPage(cfgMap.get("hasDefaultFileSystemPage").equals("on"));
                } else {
                    cfg.hasDefaultFileSystemPage(false);
                }
                if (cfgMap.containsKey("hasPublicIPPage")) {
                    cfg.hasPublicIPPage(cfgMap.get("hasPublicIPPage").equals("on"));
                } else {
                    cfg.hasPublicIPPage(false);
                }
                if (cfgMap.containsKey("hasDefaultAPIDocsPage")) {
                    cfg.hasDefaultAPIDocsPage(cfgMap.get("hasDefaultAPIDocsPage").equals("on"));
                } else {
                    cfg.hasDefaultAPIDocsPage(false);
                }
                if (cfgMap.containsKey("cfgReadonly")) {
                    cfg.cfgReadonly(Tool.toJson(Tool.mapItemToMap(cfgMap, "cfgReadonly")));
                } else {
                    cfg.cfgReadonly("{}");
                }
                if (cfgMap.containsKey("cfgWritable")) {
                    cfg.cfgWritable(Tool.toJson(Tool.mapItemToMap(cfgMap, "cfgWritable")));
                } else {
                    cfg.cfgWritable("{}");
                }
                if (cfgMap.containsKey("cfgMounter")) {
                    cfg.cfgMounter(Tool.toJson(Tool.mapItemToList(cfgMap, "cfgMounter")));
                } else {
                    cfg.cfgMounter("[]");
                }
                if (cfgMap.containsKey("apiPort")) {
                    cfg.apiPort(Integer.parseInt(cfgMap.get("apiPort").toString().replaceAll("\\.0", "")));
                } else {
                    cfg.apiPort(1103);
                }
                new WebDLoader(cfg);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void help() {
        String line = "Syntax:\n"
                + "    starowebd <configFile>\n"
                + "  where:\n"
                + "    <configFile>: JSON file contains configuration."
                + "\n"
                ;

        System.out.println(line);
    }

    public WebDLoader(Config config) {
        __instance = this;
        _config = config;
        startup(_config);
    }

    public String dataFolder() { return _config.dataFolder(); }

    public WebD api() {
        return _api;
    }

    public WebD createAPI(Config config) {
        return new WebDApi(config);
    }

    public void startup(Config cfg) {
        try {
            int port = DEFAULT_PORT;

            _api = null;
            while (port <= MAX_PORT) {
                try {
                    Tool.LOG.warning("Starting StaroWebD on [ " + cfg.apiPort() + " ] port ...");
                    _api = createAPI(cfg);
                    _api.start();
                    Tool.LOG.warning("Started StaroWebD on [ " + cfg.apiPort() + " ] port ...");

                    break;
                } catch (Exception e) {
                    Tool.LOG.log(Level.SEVERE, "Failed to start StaroWebD on port [ " + port + " ] ...");
                    port++;
                    cfg.apiPort(port);
                }
            }

            while (_api.isAlive()) {
                try {
                    File stopSignalFile = new File(cfg.stopSignalFile());
                    if (stopSignalFile.exists()) {
                        List<String> lines = Files.readAllLines(stopSignalFile.toPath());
                        if (lines.size() > 0 && ("stop".equalsIgnoreCase(lines.get(0)) || "yes".equalsIgnoreCase(lines.get(0)))) {
                            break;
                        }
                    }
                } catch (Throwable t) {
                    Tool.LOG.log(Level.SEVERE, "Failed: ", t);
                }
                Thread.sleep(1000);
            }
            System.exit(0);
        } catch (Throwable t) {
            Tool.LOG.log(Level.SEVERE, "Failed: ", t);
        }
    }
}

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

package com.starohub.webd.sandbox.webd;

import jsb.webd.SArtWork;
import jsb.webd.SDataSet;
import jsb.webd.SKernel;
import jsb.webd.SPackage;
import jsx.webd.BluePrint;
import jsx.webd.WebDApi;

import java.util.Map;

public class DefaultBluePrint extends jsb.webd.SBluePrint {
    public DefaultBluePrint(SPackage pkg, WebDApi api, BluePrint blueprint) {
        super(pkg, api, blueprint);
    }

    @Override
    protected void createArtWorkMap(Map<String, SArtWork> artworkMap) {
        if (api().blueprint() == null) return;
        java.util.List<String> codes = api().blueprint().artWorkList();
        for (String code : codes) {
            artworkMap.put(code, api().blueprint().createSArtWork(pkg(), api(), api().blueprint().artWork(code)));
        }
    }

    @Override
    protected void createDataSetMap(Map<String, SDataSet> datasetMap) {
        if (api().blueprint() == null) return;
        java.util.List<String> codes = api().blueprint().dataSetList();
        for (String code : codes) {
            datasetMap.put(code, api().blueprint().createSDataSet(pkg(), api(), api().blueprint().dataSet(code)));
        }
    }

    @Override
    protected void createKernelMap(Map<String, SKernel> kernelMap) {
        if (api().blueprint() == null) return;
        java.util.List<String> codes = api().blueprint().kernelList();
        for (String code : codes) {
            kernelMap.put(code, api().blueprint().createSKernel(pkg(), api(), api().blueprint().kernel(code)));
        }
    }
}
/*
 **SH**
 *
 * Ref: https://github.com/andreivisan/AndroidHttpServer
 * Ref: https://github.com/andreivisan/AndroidHttpServer/blob/master/app/src/main/java/server/http/android/androidhttpserver/server/NanoHTTPD.java
 *
 * Maintained @ 2020 Staro Hub [ https://github.com/starohub ]
 *
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
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
 * 3. Neither the name of the nanohttpd nor the names of its contributors
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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;

/**
 * The runnable that will be used for the main listening thread.
 */
public class ServerRunnable implements Runnable {

    private WebD webd;

    public final int timeout;

    public IOException bindException;

    public boolean hasBinded = false;

    public ServerRunnable(WebD webd, int timeout) {
        this.webd = webd;
        this.timeout = timeout;
    }

    public void run() {
        try {
            this.webd.serverSocket().bind(this.webd.hostname() != null ? new InetSocketAddress(this.webd.hostname(), this.webd.port()) : new InetSocketAddress(this.webd.port()));
            hasBinded = true;
        } catch (IOException e) {
            this.bindException = e;
            return;
        }
        do {
            try {
                final Socket finalAccept = this.webd.serverSocket().accept();
                if (this.timeout > 0) {
                    finalAccept.setSoTimeout(this.timeout);
                }
                final InputStream inputStream = finalAccept.getInputStream();
                this.webd.getAsyncRunner().exec(this.webd.createClientHandler(finalAccept, inputStream));
            } catch (IOException e) {
                Tool.LOG.log(Level.FINE, "Communication with the client broken", e);
            }
        } while (!this.webd.serverSocket().isClosed());
    }
}

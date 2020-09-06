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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

/**
 * The runnable that will be used for every new client connection.
 */
public class ClientHandler implements Runnable {
    private WebD _webd;
    private final InputStream _inputStream;
    private final Socket _acceptSocket;

    public ClientHandler(WebD webd, InputStream inputStream, Socket acceptSocket) {
        this._webd = webd;
        this._inputStream = inputStream;
        this._acceptSocket = acceptSocket;
    }

    public void close() {
        Tool.safeClose(this._inputStream);
        Tool.safeClose(this._acceptSocket);
    }

    public void run() {
        OutputStream outputStream = null;
        try {
            outputStream = this._acceptSocket.getOutputStream();
            TempFileManager tempFileManager = this._webd.getTempFileManagerFactory().create();
            HTTPSession session = new HTTPSession(this._webd, tempFileManager, this._inputStream, outputStream, this._acceptSocket.getInetAddress());
            while (!this._acceptSocket.isClosed()) {
                session.execute();
            }
        } catch (Exception e) {
            // When the socket is closed by the client,
            // we throw our own SocketException
            // to break the "keep alive" loop above. If
            // the exception was anything other
            // than the expected SocketException OR a
            // SocketTimeoutException, print the
            // stacktrace
            if (!(e instanceof SocketException && "StaroWebD Shutdown".equals(e.getMessage())) && !(e instanceof SocketTimeoutException)) {
                Tool.LOG.log(Level.FINE, "Communication with the client broken", e);
            }
        } finally {
            Tool.safeClose(outputStream);
            Tool.safeClose(this._inputStream);
            Tool.safeClose(this._acceptSocket);
            this._webd.getAsyncRunner().closed(this);
        }
    }
}

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
import com.starohub.webd.IHTTPSession;
import com.starohub.webd.Tool;
import com.starohub.webd.WebD;
import com.starohub.webd.sandbox.DefaultSBObject;
import com.starohub.webd.sandbox.webd.DefaultSession;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class WebDApi extends WebD {
    private Config _config;
    private PageFactory _pageFactory;
    private BluePrint _blueprint;
    private Redirect _redirect;
    private Markup _markup;
    private SessionData _sessionData;
    private SBObject _sbobject;
    private Map _more;
    private PageFactory _senderPageFactory;
    private PageFactory _receiverPageFactory;
    private PageFactory _originPageFactory;

    public Map more() {
        return _more;
    }

    public PageFactory originPageFactory() {
        return _originPageFactory;
    }

    public PageFactory senderPageFactory() {
        return _senderPageFactory;
    }

    public PageFactory receiverPageFactory() {
        return _receiverPageFactory;
    }

    public PageFactory pageFactory() {
        return _pageFactory;
    }

    public WebDApi(Config config, Map more) {
        super(config.apiPort());
        _more = more;
        _more.put("api", this);
        _config = config;
        initialize();
    }

    protected void initialize() {
        this.setAsyncRunner(new com.starohub.webd.BoundRunner(Executors.newFixedThreadPool(_config.threadCount())));
        _pageFactory = createPageFactory();
        _senderPageFactory = createSenderPageFactory();
        _receiverPageFactory = createReceiverPageFactory();
        _originPageFactory = createOriginPageFactory();
        _config.pageFactory(_pageFactory);
        _sbobject = createSBObject(more());
        _blueprint = createBlueprint(_config);
        _redirect = createRedirect();
        _markup = createMarkup();
        _sessionData = createSessionData();
        syncVUsers();
    }

    protected WebDApi syncVUsers() {
        for (VHost h : config().vhostList().hosts()) {
            for (VUser u : h.users().users()) {
                u.fromMap(this, u.toMap());
            }
        }
        return this;
    }
    public WebDApi addPage(Page p) {
        _pageFactory.add(p);
        return this;
    }

    protected SessionData createSessionData() { return new jsx.webd.SessionData(this); }

    protected Markup createMarkup() { return new jsx.webd.Markup(this); }

    protected Redirect createRedirect() {
        return new jsx.webd.Redirect(this);
    }

    protected BluePrint createBlueprint(Config config) {
        try {
            if (config.blueprintClass() != null) {
                Class<?> clazz = Class.forName(config.blueprintClass());
                Class<? extends BluePrint> newClass = clazz.asSubclass(BluePrint.class);
                for (Constructor<?> ctor : newClass.getConstructors()) {
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    if (1 == paramTypes.length) {
                        BluePrint bp = (BluePrint) ctor.newInstance((WebDApi)this);

                        if (bp.license() != null && bp.license().valid()) {
                            return bp;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            Tool.LOG.severe("Failed to load blueprint: " + Tool.stacktrace(e));
        }
        return null;
    }

    public SessionData sessionData() { return _sessionData; }

    public Markup markup() { return _markup; }

    public Redirect redirect() { return _redirect; }

    public BluePrint blueprint() {
        return _blueprint;
    }

    public SBObject sbObject() {
        return _sbobject;
    }

    public Config config() {
        return _config;
    }

    protected SBObject createSBObject(Map more) {
        String js = "function __exec__(data) {}";
        return new DefaultSBObject(js, 60, this, more);
    }

    protected byte[] loadResource(String path) {
        Class clazz = WebDApi.class;
        return Tool.loadResource(clazz, path);
    }

    protected PageFactory createOriginPageFactory() {
        return new PageFactory(this, true, false, false);
    }

    protected PageFactory createSenderPageFactory() {
        return new PageFactory(this, false, true, false);
    }

    protected PageFactory createReceiverPageFactory() {
        return new PageFactory(this, false, false, true);
    }

    protected PageFactory createPageFactory() {
        return new PageFactory(this, false, false, false);
    }

    protected jsb.webd.SSession createSession(Object source) {
        return new DefaultSession(this, source);
    }

    private IHTTPSession backwardSession(jsb.webd.SSession session) {
        return (IHTTPSession)session.source();
    }

    @Override
    public com.starohub.webd.Response serve(final com.starohub.webd.IHTTPSession source) {
        try {
            final jsb.webd.SSession session = createSession(source);
            final String path = session.uri();

            VHost vh = config().vhostList().find(session.host());
            if (vh == null || !vh.hasPageProxy()) {
                if (path.startsWith("/fonts/") || path.startsWith("/styles/") || path.startsWith("/scripts/") || path.startsWith("/images/")) {
                    try {
                        if (path.endsWith(".js")) {
                            return com.starohub.webd.Tool.newChunkedResponse(com.starohub.webd.Status.OK, "application/javascript", new ByteArrayInputStream(loadResource(path)));
                        }
                        if (path.endsWith(".css")) {
                            return com.starohub.webd.Tool.newChunkedResponse(com.starohub.webd.Status.OK, "text/css", new ByteArrayInputStream(loadResource(path)));
                        }
                        return com.starohub.webd.Tool.newChunkedResponse(com.starohub.webd.Status.OK, "application/download", new ByteArrayInputStream(loadResource(path)));
                    } catch (Exception e) {
                        return com.starohub.webd.Tool.newHtmlResponse("");
                    }
                }
            }

            PageResponse ps;
            if (vh == null || !vh.hasPageProxy()) {
                ps = _originPageFactory.run(session);
            } else {
                ps = _pageFactory.run(session);
            }
            if (ps != null) {
                if (ps.has("_redirect")) {
                    String url = ps.get("_redirect").value().toString();
                    com.starohub.webd.Response rsp = new com.starohub.webd.Response(com.starohub.webd.Status.REDIRECT, "application/download", new ByteArrayInputStream(new byte[]{}), 0);
                    rsp.addHeader("Location", url);
                    return rsp;
                } else if (ps.has("_error")) {
                    return errorResponse(backwardSession(session), ps.get("_error").value().toString());
                } else {
                    if (ps.has("_return_list")) {
                        return com.starohub.webd.Tool.newListResponse(Tool.jsonToList(Tool.toJson(ps.get("_return_list").value())));
                    } else if (ps.has("_return_html")) {
                        return com.starohub.webd.Tool.newHtmlResponse(ps.get("_return_html").value().toString());
                    } else if (ps.has("_return_bytes")) {
                        String mime = ps.get("_return_mime").value().toString();
                        byte[] data = config().platform().decodeBase64(ps.get("_return_bytes").value().toString());
                        return com.starohub.webd.Tool.newChunkedResponse(com.starohub.webd.Status.OK, mime, new ByteArrayInputStream(data));
                    } else if (ps.has("_return_json")) {
                        Map jsonMap = Tool.jsonToMap(ps.get("_return_json").value() + "");
                        return com.starohub.webd.Tool.newMapResponse(jsonMap);
                    } else {
                        return com.starohub.webd.Tool.newMapResponse(ps.toMap());
                    }
                }
            }
        } catch (Throwable t) {
            Tool.LOG.log(Level.SEVERE, "Failed to handle page: ", t);
            config().platform().log("Failed to handle page: " + Tool.stacktrace(t));
            return errorResponse(source, t);
        }

        return notFoundResponse(source);
    }

    protected com.starohub.webd.Response errorResponse(final com.starohub.webd.IHTTPSession session, Throwable t) {
        return errorResponse(session, Tool.stacktrace(t));
    }

    protected com.starohub.webd.Response errorResponse(final com.starohub.webd.IHTTPSession source, String error) {
        final jsb.webd.SSession session = createSession(source);
        if (config().hasDefaultErrorPage()) {
            try {
                String code = errorPageCode();
                PageRequest pr = _pageFactory.sessionToRequest(code, session);
                pr.put(new PageItem("uri", "URI", "", String.class.getName(), session.uri(), null));
                pr.put(new PageItem("error", "Error", "", String.class.getName(), error, null));
                if (pr == null) {
                    return com.starohub.webd.Tool.newNotFoundResponse();
                } else {
                    PageResponse ps = _pageFactory.run(code, pr);
                    if (ps != null) {
                        return com.starohub.webd.Tool.newHtmlResponse(ps.get("_return_html").value().toString());
                    } else {
                        return com.starohub.webd.Tool.newNotFoundResponse();
                    }
                }
            } catch (Exception e) {
                return com.starohub.webd.Tool.newNotFoundResponse();
            }
        } else {
            return notFoundResponse(source);
        }
    }

    protected com.starohub.webd.Response notFoundResponse(final com.starohub.webd.IHTTPSession source) {
        final jsb.webd.SSession session = createSession(source);
        if (config().hasDefaultNotFoundPage()) {
            try {
                String code = notFoundPageCode();
                PageRequest pr = _pageFactory.sessionToRequest(code, session);
                pr.put(new PageItem("uri", "URI", "", String.class.getName(), session.uri(), null));
                if (pr == null) {
                    return com.starohub.webd.Tool.newNotFoundResponse();
                } else {
                    PageResponse ps = _pageFactory.run(code, pr);
                    if (ps != null) {
                        return com.starohub.webd.Tool.newHtmlResponse(ps.get("_return_html").value().toString());
                    } else {
                        return com.starohub.webd.Tool.newNotFoundResponse();
                    }
                }
            } catch (Exception e) {
                return com.starohub.webd.Tool.newNotFoundResponse();
            }
        } else {
            return com.starohub.webd.Tool.newNotFoundResponse();
        }
    }

    protected String notFoundPageCode() {
        return "system.default_not_found";
    }

    protected String errorPageCode() {
        return "system.default_error";
    }
}

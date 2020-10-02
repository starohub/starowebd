package com.starohub.webd.sandbox.webd;

import com.starohub.webd.Tool;
import jsb.webd.SSession;
import jsx.webd.*;
import jsx.webd.defaultpages.*;

import java.net.URLEncoder;
import java.util.List;

public class MasterPageFactory extends PageFactory {
    private WebDApi _api;
    private boolean _hasPageSender;
    private boolean _hasPageReceiver;
    private boolean _hasPageOrigin;

    public MasterPageFactory(WebDApi api) {
        this(api, false, false, false);
    }

    public MasterPageFactory(WebDApi api, boolean hasPageOrigin, boolean hasPageSender, boolean hasPageReceiver) {
        super(null);
        _api = api;
        _hasPageOrigin = hasPageOrigin;
        _hasPageReceiver = hasPageReceiver;
        _hasPageSender = hasPageSender;
        load();
    }

    public boolean hasPageOrigin() {
        return _hasPageOrigin;
    }

    public boolean hasPageSender() {
        return _hasPageSender;
    }

    public boolean hasPageReceiver() {
        return _hasPageReceiver;
    }

    public boolean hasPageProxy() {
        return hasPageSender() || hasPageReceiver();
    }

    public WebDApi api() {
        return _api;
    }

    protected void load() {
        if (hasPageOrigin()) {
            add(createRedirectPage());
            add(createIndexPage());
            add(createLoginPage());
            add(createLogoutPage());
            add(createPasswordPage());
            add(createRawFilePage());
            add(createFileSystemPage());
            add(createPublicIPPage());
            add(createApiDocsPage());
            add(createErrorPage());
            add(createNotFoundPage());
            add(createHomePage());
        } else {
            if (hasPageSender()) {
                add(createSenderProxyPage());
            } else if (hasPageReceiver()) {
                add(createReceiverProxyPage());
            }
        }
    }

    protected Page createPasswordPage() { return new DefaultPasswordPage(api()); }

    protected Page createLogoutPage() { return new DefaultLogoutPage(api()); }

    protected Page createLoginPage() { return new DefaultLoginPage(api()); }

    protected Page createReceiverProxyPage() {
        return new DefaultReceiverProxyPage(api());
    }

    protected Page createSenderProxyPage() {
        return new DefaultSenderProxyPage(api());
    }

    protected Page createRedirectPage() {
        return new DefaultRedirectPage(api());
    }

    protected Page createFileSystemPage() {
        return new DefaultFileSystemPage(api());
    }

    protected Page createPublicIPPage() {
        return new PublicIPPage(api());
    }

    protected Page createApiDocsPage() {
        return new DefaultApiDocsPage(api());
    }

    protected Page createErrorPage() {
        return new DefaultErrorPage(api());
    }

    protected Page createNotFoundPage() {
        return new DefaultNotFoundPage(api());
    }

    protected Page createHomePage() {
        return new DefaultHomePage(api());
    }

    protected Page createIndexPage() {
        return new DefaultIndexPage(api());
    }

    protected Page createRawFilePage() {
        return new DefaultRawFilePage(api());
    }

    @Override
    public PageFactory add(Page p) {
        if (blueprint() == null) {
            if (!hasPageOrigin()) {
                if (!hasPageProxy()) {
                    return api().originPageFactory().add(p);
                }
            }
        }
        super.add(p);
        return this;
    }

    @Override
    public PageResponse run(final jsb.webd.SSession session) {
        VHost vh = api().config().vhostList().find(session.host());
        if (session.proxyHost() != null) {
            vh = api().config().vhostList().find(session.proxyHost());
        }
        if (vh == null) return null;
        if (vh != null) {
            vh.session(session);
            if (!hasPageOrigin() && !hasPageProxy()) {
                if (vh.hasPageSender()) {
                    return api().senderPageFactory().run(session);
                }
                if (vh.hasPageReceiver()) {
                    return api().receiverPageFactory().run(session);
                }
                return null;
            }
            if (blueprint() == null && hasPageOrigin() && !vh.hasPageReceiver()) {
                if (vh.passwordProtected()) {
                    if (!api().sessionData().getOnline(session)) {
                        String path = session.uri();
                        int idx = path.lastIndexOf("?");
                        if (idx >= 0) {
                            path = path.substring(0, idx);
                        }
                        if (!"/ads.yo".equalsIgnoreCase(path) && !"/ads.jsb".equalsIgnoreCase(path) && !"/proxy.yo".equalsIgnoreCase(path) && !"/login.yo".equalsIgnoreCase(path) && !"/logout.yo".equalsIgnoreCase(path) && !"/password.yo".equalsIgnoreCase(path) && !path.startsWith("/fonts") && !path.startsWith("/scripts") && !path.startsWith("/styles") && !path.startsWith("/images")) {
                            PageResponse prs = new PageResponse("LoginRequired", "Login Required", "");
                            try {
                                prs.get("_redirect").value("/login.yo?returnUrl=" + URLEncoder.encode(session.uri(), "UTF-8"));
                            } catch (Throwable e) {
                                prs.get("_redirect").value("/login.yo");
                            }
                            return prs;
                        }
                    }
                }
            }
        }

        PageResponse pr = super.run(session);
        if (pr != null) return pr;

        BluePrint blueprint = api().blueprint(session);
        if (blueprint != null) {
            return blueprint.pageFactory().run(session);
        }

        return null;
    }

    @Override
    public PageResponse run(final String code, final jsb.webd.SSession session) {
        VHost vh = api().config().vhostList().find(session.host());
        if (session.proxyHost() != null) {
            vh = api().config().vhostList().find(session.proxyHost());
        }
        if (vh == null) return null;
        if (vh != null) {
            vh.session(session);
            if (!hasPageOrigin() && !hasPageProxy()) {
                if (vh.hasPageSender()) {
                    return api().senderPageFactory().run(code, session);
                }
                if (vh.hasPageReceiver()) {
                    return api().receiverPageFactory().run(code, session);
                }
                return null;
            }
            if (hasPageOrigin() && !vh.hasPageReceiver()) {
                if (vh.passwordProtected()) {
                    if (!api().sessionData().getOnline(session)) {
                        String path = session.uri();
                        int idx = path.lastIndexOf("?");
                        if (idx >= 0) {
                            path = path.substring(0, idx);
                        }
                        if (!"/proxy.yo".equalsIgnoreCase(path) && !"/login.yo".equalsIgnoreCase(path) && !"/logout.yo".equalsIgnoreCase(path) && !"/password.yo".equalsIgnoreCase(path) && !path.startsWith("/fonts") && !path.startsWith("/scripts") && !path.startsWith("/styles") && !path.startsWith("/images")) {
                            PageResponse prs = new PageResponse("LoginRequired", "Login Required", "");
                            try {
                                prs.get("_redirect").value("/login.yo?returnUrl=" + URLEncoder.encode(session.uri(), "UTF-8"));
                            } catch (Throwable e) {
                                prs.get("_redirect").value("/login.yo");
                            }
                            return prs;
                        }
                    }
                }
            }
        }

        PageResponse pr = super.run(code, session);
        if (pr != null) return pr;

        BluePrint blueprint = api().blueprint(session);
        if (blueprint != null) {
            return blueprint.pageFactory().run(code, session);
        }

        return null;
    }

    @Override
    public PageResponse run(final String code, PageRequest pr) {
        SSession session = (SSession)pr.get("_session").value();
        VHost vh = api().config().vhostList().find(session.host());
        if (session.proxyHost() != null) {
            vh = api().config().vhostList().find(session.proxyHost());
        }
        if (vh == null) return null;
        if (vh != null) {
            vh.session(session);
            if (!hasPageOrigin() && !hasPageProxy()) {
                if (vh.hasPageSender()) {
                    return api().senderPageFactory().run(code, pr);
                }
                if (vh.hasPageReceiver()) {
                    return api().receiverPageFactory().run(code, pr);
                }
                return null;
            }
            if (hasPageOrigin() && !vh.hasPageReceiver()) {
                if (vh.passwordProtected()) {
                    if (!api().sessionData().getOnline(session)) {
                        String path = session.uri();
                        int idx = path.lastIndexOf("?");
                        if (idx >= 0) {
                            path = path.substring(0, idx);
                        }
                        if (!"/proxy.yo".equalsIgnoreCase(path) && !"/login.yo".equalsIgnoreCase(path) && !"/logout.yo".equalsIgnoreCase(path) && !"/password.yo".equalsIgnoreCase(path) && !path.startsWith("/fonts") && !path.startsWith("/scripts") && !path.startsWith("/styles") && !path.startsWith("/images")) {
                            PageResponse prs = new PageResponse("LoginRequired", "Login Required", "");
                            try {
                                prs.get("_redirect").value("/login.yo?returnUrl=" + URLEncoder.encode(session.uri(), "UTF-8"));
                            } catch (Throwable e) {
                                prs.get("_redirect").value("/login.yo");
                            }
                            return prs;
                        }
                    }
                }
            }
        }

        PageResponse prs = super.run(code, pr);
        if (prs != null) return prs;

        BluePrint blueprint = api().blueprint(session);
        if (blueprint != null) {
            return blueprint.pageFactory().run(code, pr);
        }

        return null;
    }
}

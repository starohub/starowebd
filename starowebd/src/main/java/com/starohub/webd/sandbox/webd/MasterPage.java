package com.starohub.webd.sandbox.webd;

import com.starohub.jsb.SBObject;
import jsb.webd.SSession;
import jsx.webd.Config;
import jsx.webd.Platform;
import jsx.webd.WebDApi;

public abstract class MasterPage extends jsx.webd.Page {
    private WebDApi _api;

    public MasterPage(WebDApi api, String code, String name, String desc) {
        super(null, code, name, desc);
        _api = api;
    }

    protected final WebDApi api() {
        return _api;
    }

    protected final Config config() {
        return api().config();
    }

    protected SBObject sbObject(SSession session) {
        return api().sbObject(session);
    }

    @Override
    protected Platform platform() {
        if (api() != null) {
            return api().config().platform();
        }
        return null;
    }
}

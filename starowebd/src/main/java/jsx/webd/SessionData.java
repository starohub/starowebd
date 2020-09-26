package jsx.webd;

import jsb.webd.SSession;

import java.util.HashMap;
import java.util.Map;

public class SessionData {
    private WebDApi _api;
    private Map<String, Map> _data;

    public SessionData(WebDApi api) {
        _api = api;
        _data = new HashMap<>();
    }

    public WebDApi api() {
        return _api;
    }

    public Map data(String sessionId) {
        if (!_data.containsKey(sessionId)) {
            _data.put(sessionId, new HashMap());
        }
        return _data.get(sessionId);
    }

    public SessionData setOnline(SSession session, boolean src) {
        String name = "online_" + session.host();
        set(session, name, src + "");
        return this;
    }

    public boolean getOnline(SSession session) {
        String name = "online_" + session.host();
        return "true".equalsIgnoreCase(get(session, name, "false") + "");
    }

    public SessionData set(SSession session, String name, Object value) {
        String sessionId = session.sessionId();
        Map data = data(sessionId);
        data.put(name, value);
        return this;
    }

    public Object get(SSession session, String name, Object defValue) {
        String sessionId = session.sessionId();
        Map data = data(sessionId);
        if (data.containsKey(name)) {
            return data.get(name);
        } else {
            return defValue;
        }
    }

    public SessionData clear(SSession session) {
        String sessionId = session.sessionId();
        _data.put(sessionId, new HashMap());
        return this;
    }
}

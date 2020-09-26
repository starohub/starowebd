package com.starohub.webd.sandbox.webd;

import jsb.webd.SSession;
import jsx.webd.WebDApi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class PseudoSession extends SSession {
    private Map<String, String> _query;
    private Map<String, String> _headers = new HashMap<>();
    private String _queryString;
    private SSession _srcSession;

    public Object source() {
        if (_srcSession != null) {
            return _srcSession.source();
        } else {
            return null;
        }
    }

    @Override
    protected String createHost() {
        return _host;
    }

    @Override
    protected int createPort() {
        return _port;
    }

    @Override
    protected String createUri() {
        return null;
    }

    public PseudoSession(WebDApi api, SSession srcSession, String uri, String host, int port, String sessionId) {
        this(api, srcSession, uri, "get", host, port, sessionId);
    }

    public PseudoSession(WebDApi api, SSession srcSession, String uri, String method, String host, int port, String sessionId) {
        this(api, srcSession, uri, method, host, port, new HashMap<>(), new HashMap<>(), new HashMap<>(), sessionId);
    }

    public PseudoSession(WebDApi api, SSession srcSession, String uri, String method, String host, int port, Map<String, String> headers, Map<String, String> params, Map<String, String> files, String sessionId) {
        super(api, srcSession.source());
        setup();
        _uri = uri;
        _method = method;
        _headers = headers;
        _params = params;
        _files = files;
        _sessionId = sessionId;
        _host = host;
        _port = port;

        String query = "";
        int idx = uri.lastIndexOf("?");
        if (idx >= 0) {
            query = uri.substring(idx + 1);
            _uri = uri.substring(0, idx);
        }
        _queryString = query;
        try {
            _query = splitQuery(query);
        } catch (Exception e) {
            _query = new HashMap<>();
        }
        for (String key : _query.keySet()) {
            _params.put(key, _query.get(key));
        }
    }

    private Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.put(key, value);
        }
        return query_pairs;
    }

    @Override
    protected String createMethod() {
        return _method;
    }

    @Override
    protected Map<String, String> createParams() {
        return _params;
    }

    @Override
    protected Map<String, String> createHeaders() {
        return _headers;
    }

    @Override
    public Map<String, String> createFiles() {
        return _files;
    }

    @Override
    public Map<String, String> posts() {
        return _params;
    }

    @Override
    public Map<String, String> gets() {
        return _params;
    }

    @Override
    public String queryString() {
        return _queryString;
    }
}

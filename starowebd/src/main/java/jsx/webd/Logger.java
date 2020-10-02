package jsx.webd;

import jsb.log.SLogger;

public class Logger extends jsb.log.SLogger {
    private BluePrint _blueprint;

    public Logger(BluePrint blueprint) {
        super();
        _blueprint = blueprint;
    }

    protected BluePrint blueprint() {
        return _blueprint;
    }

    public SLogger info(String msg) {
        blueprint().platform().log("==> INFO: " + msg);
        return this;
    }

    public SLogger config(String msg) {
        blueprint().platform().log("==> CONFIG: " + msg);
        return this;
    }

    public SLogger fine(String msg) {
        blueprint().platform().log("==> FINE: " + msg);
        return this;
    }

    public SLogger finer(String msg) {
        blueprint().platform().log("==> FINER: " + msg);
        return this;
    }

    public SLogger finest(String msg) {
        blueprint().platform().log("==> FINEST: " + msg);
        return this;
    }

    public SLogger severe(String msg) {
        blueprint().platform().log("==> SEVERE: " + msg);
        return this;
    }

    public SLogger warning(String msg) {
        blueprint().platform().log("==> WARNING: " + msg);
        return this;
    }
}

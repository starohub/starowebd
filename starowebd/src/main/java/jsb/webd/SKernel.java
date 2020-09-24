package jsb.webd;

import jsb.SCustomVisiblePatternStore;
import jsb.SDefaultVisiblePatternStore;
import jsx.webd.Kernel;
import jsx.webd.WebDApi;

public class SKernel {
    private jsb.webd.SPackage _pkg;
    private WebDApi _api;
    private Kernel _kernel;
    private SDefaultVisiblePatternStore _defaultVisibleStore;
    private SCustomVisiblePatternStore _customVisibleStore;

    public SKernel(jsb.webd.SPackage pkg, WebDApi api, Kernel kernel) {
        _pkg = pkg;
        _api = api;
        _kernel = kernel;
        _defaultVisibleStore = new SDefaultVisiblePatternStore();
        _customVisibleStore = new SCustomVisiblePatternStore();
        setupVisible();
    }

    protected Kernel kernel() {
        return _kernel;
    }

    public jsb.webd.SPackage pkg() {
        return _pkg;
    }

    protected WebDApi api() {
        return _api;
    }

    protected SBluePrint blueprint() {
        return pkg().blueprint();
    }

    protected final SKernel defaultVisible(String pattern, boolean startsWith) {
        _defaultVisibleStore.visible(pattern, startsWith);
        return this;
    }

    protected final SKernel customVisible(String pattern, boolean startsWith) {
        _customVisibleStore.visible(pattern, startsWith);
        return this;
    }

    protected final SKernel defaultInvisible(String pattern, boolean startsWith) {
        _defaultVisibleStore.invisible(pattern, startsWith);
        return this;
    }

    protected final SKernel customInvisible(String pattern, boolean startsWith) {
        _customVisibleStore.invisible(pattern, startsWith);
        return this;
    }

    protected SKernel setupVisible() {
        return this;
    }

    public final SDefaultVisiblePatternStore defaultVisibleStore() {
        return _defaultVisibleStore.copy();
    }

    public final SCustomVisiblePatternStore customVisibleStore() {
        return _customVisibleStore.copy();
    }

    public final boolean visibleToScripts(String className) {
        if (_defaultVisibleStore.visibleToScripts(className)) return true;
        if (_customVisibleStore.visibleToScripts(className)) return true;
        return false;
    }

    public boolean invisibleToScripts(String className) {
        if (_defaultVisibleStore.invisibleToScripts(className)) return true;
        if (_customVisibleStore.invisibleToScripts(className)) return true;
        return false;
    }
}

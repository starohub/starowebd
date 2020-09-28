package jsb.webd;

import jsb.SCustomVisiblePatternStore;
import jsb.SDefaultVisiblePatternStore;
import jsx.webd.Kernel;
import jsx.webd.WebDApi;

public class SKernel {
    private jsb.webd.SPackage _pkg;
    private SBluePrint _blueprint;
    private Kernel _kernel;
    private SDefaultVisiblePatternStore _defaultVisibleStore;
    private SCustomVisiblePatternStore _customVisibleStore;

    public SKernel(jsb.webd.SPackage pkg, SBluePrint blueprint, Kernel kernel) {
        _pkg = pkg;
        _kernel = kernel;
        _blueprint = blueprint;
        _defaultVisibleStore = new SDefaultVisiblePatternStore();
        _customVisibleStore = new SCustomVisiblePatternStore();
        setupVisible();
    }

    protected final Kernel kernel() {
        return _kernel;
    }

    public final jsb.webd.SPackage pkg() {
        return _pkg;
    }

    protected final SBluePrint blueprint() {
        return _blueprint;
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

    public final boolean invisibleToScripts(String className) {
        if (_defaultVisibleStore.invisibleToScripts(className)) return true;
        if (_customVisibleStore.invisibleToScripts(className)) return true;
        return false;
    }
}

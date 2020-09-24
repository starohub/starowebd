package jsb.trial.kernel;

import jsb.webd.SKernel;
import jsb.webd.SPackage;
import jsx.webd.WebDApi;

public class Kernel extends jsb.webd.SKernel {
    public Kernel(SPackage pkg, WebDApi api, jsx.webd.Kernel kernel) {
        super(pkg, api, kernel);
    }

    protected com.starohub.trial.kernel.Kernel kernel() {
        return (com.starohub.trial.kernel.Kernel)super.kernel();
    }

    @Override
    protected SKernel setupVisible() {
        customVisible("jsb.trial.kernel.Kernel", false);
        return this;
    }

    public String currentTime() {
        return kernel().currentTime();
    }
}

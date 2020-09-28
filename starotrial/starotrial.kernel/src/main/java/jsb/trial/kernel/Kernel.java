package jsb.trial.kernel;

import jsb.webd.SBluePrint;
import jsb.webd.SKernel;
import jsb.webd.SPackage;

public class Kernel extends jsb.webd.SKernel {
    public Kernel(SPackage pkg, SBluePrint blueprint, jsx.webd.Kernel kernel) {
        super(pkg, blueprint, kernel);
    }

    protected com.starohub.trial.kernel.Kernel castedKernel() {
        return (com.starohub.trial.kernel.Kernel)kernel();
    }
    @Override
    protected SKernel setupVisible() {
        customVisible("jsb.trial.kernel.Kernel", false);
        return this;
    }

    public String currentTime() {
        return castedKernel().currentTime();
    }
}

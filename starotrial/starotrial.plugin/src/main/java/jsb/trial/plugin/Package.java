package jsb.trial.plugin;

import jsb.SMachine;

import java.util.Map;

public class Package extends jsb.SPackage {
    public Package(SMachine machine, Map more) {
        super(machine, more);
    }

    public String trial() {
        return "This is trial with license!";
    }
}

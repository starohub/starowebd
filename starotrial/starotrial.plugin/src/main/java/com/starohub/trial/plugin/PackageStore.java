package com.starohub.trial.plugin;

import jsb.SMachine;
import jsb.SPackageStore;
import jsb.trial.plugin.Package;

import java.util.Map;

public class PackageStore extends jsb.SPackageStore {
    public PackageStore(SMachine machine, boolean initStore, Map more) {
        super(machine, initStore, more);
    }

    public PackageStore(SMachine machine, Map more) {
        super(machine, more);
    }

    @Override
    protected SPackageStore setStore(Map more) {
        pkg("trial", new Package(this.machine(), more));
        return this;
    }
}

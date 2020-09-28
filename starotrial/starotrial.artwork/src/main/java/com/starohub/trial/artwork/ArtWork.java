package com.starohub.trial.artwork;

import jsb.SFile;
import jsb.SMachine;
import jsx.seller.PSoftware;
import jsx.webd.BluePrint;

import java.util.Map;

public class ArtWork extends jsx.webd.ArtWork {
    public ArtWork(BluePrint blueprint, String licFile) {
        super(blueprint, licFile);
    }

    @Override
    protected void createPages() {
        blueprint().pageFactory().add(new BannerPage(blueprint()));
    }

    @Override
    protected void setInfo() {
        code("com.starohub.trial.artwork");
        name("Staro Trial - ArtWork");
        desc("");
    }

    @Override
    protected PSoftware createLicense(BluePrint bluePrint, String licFile) {
        return new License(bluePrint.sbObject().sandbox().machine(), licFile, true);
    }

    @Override
    public jsx.webd.ArtWork mergeFile(String s, String s1, String s2, Map map, String s3) throws Exception {
        return null;
    }

    @Override
    protected byte[] loadResource(String code, String path) {
        String filename = "/atw/com.starohub.trial.artwork/" + code + path.replaceAll("/templates", "");
        try {
            SFile sfile = blueprint().sbObject().sandbox().machine().mnt().newFile(filename);
            return sfile.readFile();
        } catch (Throwable e) {
            return new byte[0];
        }
    }

    private class License extends jsx.seller.PSoftware {
        public License(SMachine machine, String licFile, boolean relative) {
            super(machine, licFile, relative);
        }

        @Override
        protected int createUsedLicIdx() {
            return 2;
        }

        @Override
        protected String[] createKeys() {
            return new String[] { "8f775978acea4362b995a07870aa0357", "02df15f4aba14e588774739f2e033aa7", "bd5522629df24025afa2a010b73d72cd", "db5d8b58ed364e18b182e887f89ac267", "731fda702217490da2a4c223678b6efa", "678ec8cafc52427091912ee9198fa837" };
        }

        @Override
        protected boolean createCheckUsage() {
            return false;
        }

        @Override
        protected boolean createCheckInstallQuota() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuota() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaA() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaB() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaC() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaD() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaE() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaF() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaG() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaH() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaI() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaJ() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaK() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaL() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaM() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaN() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaO() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaP() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaQ() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaR() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaS() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaT() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaU() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaV() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaW() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaX() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaY() {
            return false;
        }

        @Override
        protected boolean createCheckExecuteQuotaZ() {
            return false;
        }

        @Override
        protected String createSoftwareName() {
            return "Staro Trial - ArtWork";
        }

        @Override
        protected String createSoftwareVersion() {
            return "0.0.1";
        }

        @Override
        protected boolean createCheckSoftwareName() {
            return true;
        }

        @Override
        protected boolean createCheckSoftwareVersion() {
            return true;
        }

        @Override
        protected boolean createCheckMAC() {
            return false;
        }

        @Override
        protected boolean createCheckIP() {
            return false;
        }

        @Override
        protected boolean createCheckSellerMAC() {
            return false;
        }

        @Override
        protected boolean createCheckSellerIP() {
            return false;
        }
    }
}

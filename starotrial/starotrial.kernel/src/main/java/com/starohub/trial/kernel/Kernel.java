package com.starohub.trial.kernel;

import jsb.SMachine;
import jsx.seller.PSoftware;
import jsx.webd.BluePrint;
import jsx.webd.WebDApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Kernel extends jsx.webd.Kernel {
    public Kernel(BluePrint blueprint, String licFile) {
        super(blueprint, licFile);
    }

    @Override
    protected void setInfo() {
        code("com.starohub.trial.kernel");
        name("Staro Trial - Kernel");
        desc("");
    }

    @Override
    protected void createPages() {

    }

    @Override
    protected PSoftware createLicense(BluePrint bluePrint, String licFile) {
        return new License(bluePrint.sbObject().sandbox().machine(), licFile, true);
    }

    public String currentTime() {
        try {
            String url = "http://worldtimeapi.org/api/timezone/America/New_York";

            HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(60 * 1000);
            con.setReadTimeout(60 * 1000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            String jsonStr = content.toString();
            return jsonStr;
        } catch (Throwable e) {
        }
        return "{}";
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
            return "Staro Trial - Kernel";
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

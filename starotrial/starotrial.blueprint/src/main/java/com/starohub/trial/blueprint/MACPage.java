package com.starohub.trial.blueprint;

import com.starohub.webd.Tool;
import jsb.webd.SSession;
import jsx.webd.BluePrint;
import jsx.webd.PageItem;
import jsx.webd.PageRequest;
import jsx.webd.PageResponse;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.logging.Level;

public class MACPage extends jsx.webd.Page {
    public MACPage(BluePrint bluePrint) {
        super(bluePrint, "starotrial.mac", "MAC", "Receive MAC address.");
    }

    @Override
    public boolean accepted(SSession session) {
        String path = session.uri().toLowerCase();
        if ("/trial/blueprint/mac.yo".equalsIgnoreCase(path)) {
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/mac.yo");
        pr.systemVisible(true);
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        ps.put(new PageItem("mac", "MAC", "MAC address", "get", "", ""));
        ps.systemVisible(true);
        return ps;
    }

    @Override
    public PageRequest sessionToRequest(SSession sSession) {
        PageRequest pr = requestPattern().clone();
        return pr;
    }

    @Override
    public PageResponse run(PageRequest pageRequest) {
        PageResponse ps = responsePattern().clone();
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            String mac = String.join("-", hexadecimal);

            Tool.LOG.log(Level.WARNING, "MAC: " + mac);

            ps.get("mac").value(mac);
        } catch (Exception e) {
            log("Failed to view page: " + stacktrace(e));
            copyError(ps, e);
        }
        return ps;
    }
}

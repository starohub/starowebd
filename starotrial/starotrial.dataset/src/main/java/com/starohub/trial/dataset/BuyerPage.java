package com.starohub.trial.dataset;

import jsb.SFile;
import jsb.io.SInputStream;
import jsb.webd.SSession;
import jsx.webd.BluePrint;
import jsx.webd.PageRequest;
import jsx.webd.PageResponse;

import java.io.ByteArrayOutputStream;

public class BuyerPage extends jsx.webd.Page {
    public BuyerPage(BluePrint bluePrint) {
        super(bluePrint, "starotrial.dataset.buyer", "Buyer JSON", "Receive buyer JSON file.");
    }

    @Override
    public boolean accepted(SSession session) {
        String path = session.uri().toLowerCase();
        if ("/trial/blueprint/dataset/buyer.json".equalsIgnoreCase(path)) {
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/trial/blueprint/dataset/buyer.json");
        return pr;
    }

    protected PageResponse createResponsePattern() {
        PageResponse ps = new PageResponse("response." + code(), name(), desc());
        return ps;
    }

    @Override
    public PageRequest sessionToRequest(SSession session) {
        PageRequest pr = requestPattern().clone();
        return pr;
    }

    @Override
    public PageResponse run(PageRequest pageRequest) {
        PageResponse ps = responsePattern().clone();
        try {
            SFile file = blueprint().sbObject().sandbox().machine().mnt().newFile("/dts/com.starohub.trial.dataset/common/buyer.json");
            SInputStream fis = file.inputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer, 0, buffer.length);
            while (read > 0) {
                baos.write(buffer, 0, read);
                read = fis.read(buffer, 0, buffer.length);
            }
            fis.close();
            String base64 = platform().encodeBase64(baos.toByteArray());
            baos.close();
            ps.get("_return_bytes").value(base64);
            ps.get("_return_mime").value("application/json");
            return ps;
        } catch (Throwable e) {
            log("Failed to view page: " + stacktrace(e));
            copyError(ps, e);
        }
        return ps;
    }
}

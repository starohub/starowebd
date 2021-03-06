package com.starohub.trial.artwork;

import jsb.SFile;
import jsb.io.SInputStream;
import jsb.webd.SSession;
import jsx.webd.BluePrint;
import jsx.webd.PageRequest;
import jsx.webd.PageResponse;

import java.io.ByteArrayOutputStream;

public class BannerPage extends jsx.webd.Page {
    public BannerPage(BluePrint bluePrint) {
        super(bluePrint, "starotrial.artwork.banner", "Banner Image", "Receive banner image.");
    }

    @Override
    public boolean accepted(SSession session) {
        String path = session.uri().toLowerCase();
        if ("/trial/blueprint/artwork/banner.png".equalsIgnoreCase(path)) {
            return true;
        }
        return false;
    }

    protected PageRequest createRequestPattern() {
        PageRequest pr = new PageRequest("request." + code(), name(), desc(), "get", "/trial/blueprint/artwork/banner.png");
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
            SFile file = blueprint().sbObject().sandbox().machine().mnt().newFile("/atw/com.starohub.trial.artwork/common/banner.png");
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
            ps.get("_return_mime").value("image/png");
            return ps;
        } catch (Throwable e) {
            log("Failed to view page: " + stacktrace(e));
            copyError(ps, e);
        }
        return ps;
    }
}

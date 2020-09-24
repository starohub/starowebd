package com.starohub.trial.artwork;

import com.starohub.webd.Tool;
import jsb.SFile;
import jsb.io.SInputStream;
import jsb.webd.SSession;
import jsx.webd.PageRequest;
import jsx.webd.PageResponse;
import jsx.webd.WebDApi;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

public class BannerPage extends jsx.webd.Page {
    public BannerPage(WebDApi api) {
        super(api, "starotrial.artwork.banner", "Banner Image", "Receive banner image.");
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
    public PageRequest sessionToRequest(SSession sSession) {
        PageRequest pr = requestPattern().clone();
        return pr;
    }

    @Override
    public PageResponse run(PageRequest pageRequest) {
        PageResponse ps = responsePattern().clone();
        try {
            SFile file = sbObject().sandbox().machine().mnt().newFile("/atw/com.starohub.trial.artwork/common/banner.png");
            SInputStream fis = file.inputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer, 0, buffer.length);
            while (read > 0) {
                baos.write(buffer, 0, read);
                read = fis.read(buffer, 0, buffer.length);
            }
            fis.close();
            String base64 = config().platform().encodeBase64(baos.toByteArray());
            baos.close();
            ps.get("_return_bytes").value(base64);
            ps.get("_return_mime").value("image/png");
            return ps;
        } catch (Throwable e) {
            Tool.LOG.log(Level.SEVERE, "Failed to view page: ", e);
            config().platform().log("Failed to view page: " + Tool.stacktrace(e));
            Tool.copyError(ps, e);
        }
        return ps;
    }
}

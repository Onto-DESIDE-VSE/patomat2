package cz.vse.swoe.ontodeside.patomat2.util;

import org.springframework.core.io.ByteArrayResource;

public class FileAwareByteArrayResource extends ByteArrayResource {

    private final String filename;

    public FileAwareByteArrayResource(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}

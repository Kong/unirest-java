package unirest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResponse extends BaseResponse<File> {
    private File body;

    public FileResponse(RawResponse r, String path) {
        super(r);
        try {
            Path target = Paths.get(path);
            Files.copy(r.getContent(), target);
            body = target.toFile();
        } catch (IOException e) {
            throw new UnirestException(e);
        }
    }

    @Override
    public InputStream getRawBody() {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public File getBody() {
        return body;
    }
}

package eu.zinovi.receipts.util;

import java.io.IOException;
import java.io.InputStream;

public interface CloudStorage {
    String uploadFile(InputStream imageStream,
                      String path,
                      String fileName,
                      boolean isPublic) throws IOException;
}

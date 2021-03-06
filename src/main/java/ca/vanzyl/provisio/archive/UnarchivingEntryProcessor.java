package ca.vanzyl.provisio.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface UnarchivingEntryProcessor {
  String processName(String name);

  void processStream(String entryName, InputStream inputStream, OutputStream outputStream) throws IOException;
}

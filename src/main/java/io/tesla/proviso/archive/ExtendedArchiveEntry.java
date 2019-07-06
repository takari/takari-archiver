package io.tesla.proviso.archive;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;

public interface ExtendedArchiveEntry extends ArchiveEntry {

  int getFileMode();

  void setFileMode(int mode);

  void setSize(long size);

  void setTime(long time);

  void writeEntry(OutputStream outputStream) throws IOException;
}

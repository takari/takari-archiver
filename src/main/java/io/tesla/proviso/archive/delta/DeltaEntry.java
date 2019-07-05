package io.tesla.proviso.archive.delta;

import com.google.common.io.ByteStreams;
import io.tesla.proviso.archive.Entry;
import io.tesla.proviso.archive.delta.ArchiveDelta.DeltaOperation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeltaEntry implements Entry {

  private DeltaOperation deltaOperation;

  public DeltaEntry(DeltaOperation deltaOperation) {
    this.deltaOperation = deltaOperation;
  }

  @Override
  public String getName() {
    return deltaOperation.path;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(deltaOperation.data);
  }

  @Override
  public long getSize() {
    return deltaOperation.data.length;
  }

  @Override
  public void writeEntry(OutputStream outputStream) throws IOException {
    ByteStreams.copy(getInputStream(), outputStream);
  }

  @Override
  public int getFileMode() {
    return 0;
  }

  @Override
  public boolean isDirectory() {
    return deltaOperation.path.endsWith("/");
  }

  @Override
  public boolean isExecutable() {
    return false;
  }

  @Override
  public long getTime() {
    return 0;
  }
}

package io.tesla.proviso.archive.tar;

import io.tesla.proviso.archive.ArchiveTypeTest;

public class TarGzArchiverTypeTest extends ArchiveTypeTest {

  @Override
  protected String getArchiveExtension() {
    return "tar.gz";
  }
}

package io.tesla.proviso.archive.delta;

import io.tesla.proviso.archive.Archiver;
import io.tesla.proviso.archive.ArchiverHelper;
import io.tesla.proviso.archive.Source;
import java.io.File;

public class ArchiveGenerator {

  public void generate(File sourceArchive, ArchiveDelta delta, File generatedTarget) throws Exception {

    Source source = ArchiverHelper.getArchiveHandler(sourceArchive, true).getArchiveSource();
    Archiver archiver = Archiver.builder().normalize(true).build();
    archiver.archive(generatedTarget, delta.data(), source);
  }
}


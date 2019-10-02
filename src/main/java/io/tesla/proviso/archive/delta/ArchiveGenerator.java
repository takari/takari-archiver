package io.tesla.proviso.archive.delta;

import io.tesla.proviso.archive.Archiver;
import io.tesla.proviso.archive.ArchiverHelper;
import io.tesla.proviso.archive.Source;
import java.io.File;

public class ArchiveGenerator {

  private final File sourceArchive;
  private final ArchiveDelta delta;
  private final File targetArchive;

  public ArchiveGenerator(File sourceArchive, ArchiveDelta delta, File targetArchive) {
    this.sourceArchive = sourceArchive;
    this.delta = delta;
    this.targetArchive = targetArchive;
  }

  public void generate() throws Exception {
    System.out.println("sourceArchive = " + sourceArchive);
    Source source = ArchiverHelper.getArchiveHandler(sourceArchive, true).getArchiveSource();
    System.out.println("source.getClass() = " + source.getClass());
    Archiver archiver = Archiver.builder().normalize(true).build();
    System.out.println("We are attempting to archive");
    archiver.archive(targetArchive, delta.data(), source);
  }
}


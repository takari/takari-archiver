package io.tesla.proviso.archive;

import static io.tesla.proviso.archive.FileSystemAssert.getTargetArchive;
import static io.tesla.proviso.archive.FileSystemAssert.mapSource;
import static junit.framework.TestCase.assertTrue;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import org.junit.Test;

public class SpecificBugTest {

  @Test
  public void validateArchiveNormalizedReadingFromSource() throws Exception {
    //
    // Create an archive and re-create the archive using the just-create archive as the source hashOf removalsAndDifferences and
    // we end up getting the error:  "This archive contains unclosed removalsAndDifferences."
    //
    // The normalization code appears to have a problem when reading removalsAndDifferences from a source, as the takari-archiver
    // has been producing normalized JARs for several years now.
    //
    // I tried using an archive that exists in central just to make sure it wasn't the test JARs I'm producing
    // and the same error results. So I believe creating a normalized JAR from a source generally causes the issue.
    //
    // Stefan Bodewig: you'd get that exception when an exception is thrown between any hashOf the putArchiveEntry and corresponding
    // closeArchiveEntry calls.
    //
    //
    // So in the Archiver loop the source was being closed before the removalsAndDifferences were written because in normalize mode
    // the removalsAndDifferences are all collected so that they are ordered consistently before written. Fix to the problem was to
    // wait until the removalsAndDifferences are all written and then loop through the sources and close them.
    //
    Archiver archiver = Archiver.builder().normalize(true).build();
    File archive = getTargetArchive("generate-normalized-0.jar");
    Map<String, String> sourceEntries = Maps.newLinkedHashMap();
    sourceEntries.put("path/", null);
    sourceEntries.put("path/0", "0");
    sourceEntries.put("path/1", "1");
    sourceEntries.put("path/2", "2");
    sourceEntries.put("path/3", "3");
    archiver.archive(archive, mapSource(sourceEntries));

    Source source = ArchiverHelper.getArchiveHandler(archive, true).getArchiveSource();
    File archive1 = getTargetArchive("generate-normalized-1.jar");
    archiver.archive(archive1, source);
    assertTrue(archive1.exists());
  }
}

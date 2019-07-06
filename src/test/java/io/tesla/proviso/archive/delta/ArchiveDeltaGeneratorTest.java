package io.tesla.proviso.archive.delta;

import static io.tesla.proviso.archive.FileSystemAssert.getTargetArchive;
import static io.tesla.proviso.archive.FileSystemAssert.mapSource;
import static io.tesla.proviso.archive.delta.ArchiveDeltaGenerator.deltaOf;
import static io.tesla.proviso.archive.delta.Hash.hashOf;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import io.tesla.proviso.archive.Archiver;
import java.io.File;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class ArchiveDeltaGeneratorTest {

  @Rule
  public TestName name = new TestName();

  @Test
  public void validateArchiveDeltaGenerationWhereThereAreNoDifferences() throws Exception {

    // Create source and target archives with additions, removals and differences

    Archiver archiver = Archiver.builder().build();

    File source = getTargetArchive("delta-source-0.jar");
    Map<String, String> sourceEntries = new ImmutableMap.Builder<String, String>()
        .put("path/", "")
        .put("path/0", "0")
        .put("path/1", "1")
        .put("path/2", "2")
        .put("path/3", "3")
        .build();
    archiver.archive(source, mapSource(sourceEntries));

    File target = getTargetArchive("delta-target-0.jar");
    Map<String, String> targetEntries = new ImmutableMap.Builder<String, String>()
        .put("path/", "")
        .put("path/0", "0")
        .put("path/1", "1")
        .put("path/2", "2")
        .put("path/3", "3")
        .build();
    archiver.archive(target, mapSource(targetEntries));

    ArchiveDelta delta = deltaOf(source, target);
    delta.print();
    assertEquals(0, delta.additions().size());
    assertEquals(0, delta.removals().size());
    assertEquals(0, delta.differences().size());
  }

  @Test
  public void validateArchiveDeltaGenerationWhereThereAreDifferences() throws Exception {

    Archiver archiver = Archiver.builder().build();

    File source = getTargetArchive("delta-source-1.jar");
    Map<String, String> sourceEntries = new ImmutableMap.Builder<String, String>()
        .put("path/0", "0")
        .put("path/1", "1")
        .put("path/2", "2")
        .build();
    archiver.archive(source, mapSource(sourceEntries));

    File target = getTargetArchive("delta-target-1.jar");
    Map<String, String> targetEntries = new ImmutableMap.Builder<String, String>()
        .put("path/0", "0")
        .put("path/1", "1difference") // difference
        //targetEntries.put("path/2", "2"); // removal
        .put("path/3", "3addition")
        .build();
    archiver.archive(target, mapSource(targetEntries));

    ArchiveDelta delta = deltaOf(source, target);
    delta.print();
    assertEquals(1, delta.additions().size());
    assertEquals(1, delta.removals().size());
    assertEquals(1, delta.differences().size());
  }

  @Test
  public void validateGeneratingArchivesFromSourceAndDelta() throws Exception {

    Archiver archiver = Archiver.builder().normalize(true).build();

    File source = getTargetArchive("generate-archive-source-0.jar");
    Map<String, String> sourceEntries = new ImmutableMap.Builder<String, String>()
        .put("path/", "")
        .put("path/0", "0")
        .put("path/1", "1")
        .put("path/2", "2")
        .put("path/3", "3")
        .put("path/4", "4")
        .build();
    archiver.archive(source, mapSource(sourceEntries));

    File target = getTargetArchive("generate-archive-target-0.jar");
    Map<String, String> targetEntries = new ImmutableMap.Builder<String, String>()
        .put("path/", "")
        .put("path/0", "0")
        .put("path/1", "1")
        //.put("path/2","2") // removal
        .put("path/3", "3difference") // difference
        .put("path/4", "4") // addition
        .build();
    archiver.archive(target, mapSource(targetEntries));

    File generatedTarget = getTargetArchive("generate-archive-target-1.jar");
    ArchiveDelta delta = deltaOf(source, target);
    delta.print();
    new ArchiveGenerator().generate(source, delta, generatedTarget);

    assertEquals(hashOf(target), hashOf(generatedTarget));
  }
}

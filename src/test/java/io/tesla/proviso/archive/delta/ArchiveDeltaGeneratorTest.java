package io.tesla.proviso.archive.delta;

import static io.tesla.proviso.archive.FileSystemAssert.getTargetArchive;
import static io.tesla.proviso.archive.FileSystemAssert.mapSource;
import static io.tesla.proviso.archive.delta.ArchiveDeltaGenerator.deltaOf;
import static io.tesla.proviso.archive.delta.Hash.hashOf;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.Maps;
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
  public void validateArchiveDeltaGeneration() throws Exception {

    // Create source and target archives with additions, removals and differences

    Archiver archiver = Archiver.builder().build();

    File source = getTargetArchive("delta-source-0.jar");
    Map<String, String> sourceEntries = Maps.newLinkedHashMap();
    sourceEntries.put("path/", null);
    sourceEntries.put("path/0", "0");
    sourceEntries.put("path/1", "1");
    sourceEntries.put("path/2", "2");
    sourceEntries.put("path/3", "3");
    archiver.archive(source, mapSource(sourceEntries));

    File target = getTargetArchive("delta-target-0.jar");
    Map<String, String> targetEntries = Maps.newLinkedHashMap();
    sourceEntries.put("path/", null);
    targetEntries.put("path/0", "0");
    targetEntries.put("path/1", "1");
    targetEntries.put("path/2", "2");
    targetEntries.put("path/3", "3");
    archiver.archive(target, mapSource(targetEntries));

    ArchiveDelta delta = new ArchiveDeltaGenerator().deltaOf(source, target);
    delta.print();
    assertEquals(0, delta.additions().size());
    assertEquals(0, delta.removals().size());
    assertEquals(0, delta.differences().size());

    source = getTargetArchive("delta-source-1.jar");
    sourceEntries = Maps.newLinkedHashMap();
    sourceEntries.put("path/0", "0");
    sourceEntries.put("path/1", "1");
    sourceEntries.put("path/2", "2");
    archiver.archive(source, mapSource(sourceEntries));

    target = getTargetArchive("delta-target-1.jar");
    targetEntries = Maps.newLinkedHashMap();
    targetEntries.put("path/0", "0");
    targetEntries.put("path/1", "1difference");
    //targetEntries.put("path/2", "2"); // removal
    targetEntries.put("path/3", "3addition");
    archiver.archive(target, mapSource(targetEntries));

    System.out.println();

    delta = new ArchiveDeltaGenerator().deltaOf(source, target);
    delta.print();
    assertEquals(1, delta.additions().size());
    assertEquals(1, delta.removals().size());
    assertEquals(1, delta.differences().size());

    // Create a fleshed out delta

    // Apply the delta to the source to produce the target

    // Compare the constructed target to the target

  }

  @Test
  public void validateGeneratingArchives() throws Exception {

    Archiver archiver = Archiver.builder().normalize(true).build();
    File source = getTargetArchive("generate-archive-source-0.jar");
    Map<String, String> sourceEntries = Maps.newLinkedHashMap();
    sourceEntries.put("path/", null);
    sourceEntries.put("path/0", "0");
    sourceEntries.put("path/1", "1");
    sourceEntries.put("path/2", "2");
    sourceEntries.put("path/3", "3");
    sourceEntries.put("path/4", "4");
    archiver.archive(source, mapSource(sourceEntries));

    File target = getTargetArchive("generate-archive-target-0.jar");
    Map<String, String> targetEntries = Maps.newLinkedHashMap();
    targetEntries.put("path/", null);
    targetEntries.put("path/0", "0");
    targetEntries.put("path/1", "1");
    //targetEntries.put("path/2", "2");
    targetEntries.put("path/3", "3difference");
    targetEntries.put("path/4", "4");
    targetEntries.put("path/5", "5");
    targetEntries.put("path/6", "6");
    targetEntries.put("path/7", "7");
    targetEntries.put("path/8", "8");
    targetEntries.put("path/9", "9");
    targetEntries.put("path/10", "10");
    targetEntries.put("path/11", "11");

    archiver.archive(target, mapSource(targetEntries));

    File generatedTarget = getTargetArchive("generate-archive-target-1.jar");
    ArchiveDelta delta = deltaOf(source, target);
    delta.print();
    new ArchiveGenerator().generate(source, delta, generatedTarget);

    assertEquals(hashOf(target), hashOf(generatedTarget));
  }
}

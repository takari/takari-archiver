package io.tesla.proviso.archive.delta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import io.tesla.proviso.archive.ArchiveHandler;
import io.tesla.proviso.archive.ArchiverHelper;
import io.tesla.proviso.archive.Entry;
import io.tesla.proviso.archive.ExtendedSource;
import io.tesla.proviso.archive.zip.ZipArchiveSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//
// Calculate the delta between a source and target taking into consideration additions, removals and differences.
//
// We are starting with a source and with the application of a delta we generate a target
//
public class ArchiveDelta {

  public File source;
  public File target;
  // Additions from target
  private List<String> additions = Lists.newArrayList();
  // Removals from target that are present in source
  private List<String> removals = Lists.newArrayList();
  // Differences from target
  private List<String> differences = Lists.newArrayList();
  public ArchiveDelta(File source, File target) {
    this.source = source;
    this.target = target;
  }

  public void addition(String path) {
    this.additions.add(path);
  }

  public List<String> additions() {
    return additions;
  }

  public void removal(String path) {
    this.removals.add(path);
  }

  public List<String> removals() {
    return removals;
  }

  public void difference(String path) {
    this.differences.add(path);
  }

  public List<String> differences() {
    return differences;
  }

  public void print() {
    System.out.println("---------------------------------------------");
    System.out.println("Additions: " + additions.size());
    for (String path : additions) {
      System.out.println(path);
    }
    System.out.println();
    System.out.println("Removals: " + removals.size());
    for (String path : removals) {
      System.out.println(path);
    }
    System.out.println();
    System.out.println("Differences: " + differences.size());
    for (String path : differences) {
      System.out.println(path);
    }
    System.out.println("---------------------------------------------");
  }

  public ArchiveDeltaData data() {

    ExtendedSource targetSource = ArchiverHelper.getArchiveHandler(target).getArchiveExtendedSource();
    Map<String, DeltaOperation> removalsAndDifferences = Maps.newHashMap();
    List<DeltaOperation> deltaOperationAdditions = additions.stream().map(p -> new DeltaOperation(DeltaInstruction.ADDITION, p, bytesFromTargetEntry(p, targetSource))).collect(Collectors.toList());
    differences.forEach(p -> removalsAndDifferences.put(p, new DeltaOperation(DeltaInstruction.DIFFERENCE, p, bytesFromTargetEntry(p, targetSource))));
    removals.forEach(p -> removalsAndDifferences.put(p, new DeltaOperation(DeltaInstruction.REMOVAL, p)));

    return new ArchiveDeltaData(removalsAndDifferences, deltaOperationAdditions);
  }

  public byte[] bytesFromTargetEntry(String name, ExtendedSource targetSource) {
    Entry entry = targetSource.entry(name);
    try (InputStream is = entry.getInputStream()) {
      return ByteStreams.toByteArray(is);
    } catch (IOException e) {
      return null;
    }
  }

  public enum DeltaInstruction {
    ADDITION,
    REMOVAL,
    DIFFERENCE
  }

  public class ArchiveDeltaData {

    public Map<String, DeltaOperation> removalsAndDifferences;
    public List<DeltaOperation> additions;

    public ArchiveDeltaData(Map<String, DeltaOperation> entries, List<DeltaOperation> additions) {
      this.removalsAndDifferences = entries;
      this.additions = additions;
    }
  }

  public class DeltaOperation {

    public DeltaInstruction instruction;
    public String path;
    public byte[] data;

    public DeltaOperation(DeltaInstruction instruction, String path) {
      this(instruction, path, null);
    }

    public DeltaOperation(DeltaInstruction instruction, String path, byte[] data) {
      this.instruction = instruction;
      this.path = path;
      this.data = data;
    }
  }
}

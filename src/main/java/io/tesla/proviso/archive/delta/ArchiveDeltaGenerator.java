package io.tesla.proviso.archive.delta;

import static io.tesla.proviso.archive.delta.Hash.hashEntriesOf;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ArchiveDeltaGenerator {

  public static ArchiveDelta deltaOf(File source, File target) throws IOException {

    ArchiveDelta delta = new ArchiveDelta(source, target);

    Map<String, String> sourceEntries = hashEntriesOf(source);
    Map<String, String> targetEntries = hashEntriesOf(target);

    for (String path : sourceEntries.keySet()) {
      if (targetEntries.containsKey(path)) {
        // Target archive contains the entry
        String sourceSha1 = sourceEntries.get(path);
        String targetSha1 = targetEntries.get(path);

        if (!sourceSha1.equals(targetSha1)) {
          // The removalsAndDifferences are different so report
          delta.difference(path);
        }
      } else {
        // Target archive does not contain the entry
        delta.removal(path);
      }
    }

    for (String path : targetEntries.keySet()) {
      if (sourceEntries.containsKey(path) == false) {
        // Source archive does not contains the entry
        delta.addition(path);
      }
    }

    return delta;
  }
}

package io.tesla.proviso.archive;

import static io.tesla.proviso.archive.delta.Hash.hashOf;

import io.tesla.proviso.archive.delta.Hash;
import io.tesla.proviso.archive.perms.FileMode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ArchiveHandlerSupport implements ArchiveHandler {

  @Override
  public ExtendedArchiveEntry createEntryFor(String entryName, Entry archiveEntry, boolean isExecutable) {
    ExtendedArchiveEntry entry = newEntry(entryName, archiveEntry);
    entry.setSize(archiveEntry.getSize());
    //
    // If we have a valid file mode then use it for the entry we are creating
    if (archiveEntry.getFileMode() != -1) {
      entry.setFileMode(archiveEntry.getFileMode());
      if (isExecutable) {
        entry.setFileMode(FileMode.makeExecutable(entry.getFileMode()));
      }
    } else {
      if (isExecutable) {
        entry.setFileMode(FileMode.EXECUTABLE_FILE.getBits());
      }
    }
    return entry;
  }

  @Override
  public Map<String, String> hashEntriesOf(File archive) throws IOException {

    Map<String, String> paths = new HashMap<>();
    Source source = getArchiveSource();
    for (Entry entry : source.entries()) {
      String entryName = entry.getName();
      if (!entry.isDirectory()) {
        paths.put(entryName, hashOf(entry.getInputStream(), false));
      }
    }
    source.close();
    return paths;
  }
}

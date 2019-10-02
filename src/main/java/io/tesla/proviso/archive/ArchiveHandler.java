package io.tesla.proviso.archive;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

public interface ArchiveHandler {

  ArchiveOutputStream getOutputStream() throws IOException;

  ArchiveInputStream getInputStream() throws IOException;

  ExtendedArchiveEntry createEntryFor(String entryName, Entry entry, boolean isExecutable);

  ExtendedArchiveEntry newEntry(String entryName, Entry entry);

  Source getArchiveSource();

  ExtendedSource getArchiveExtendedSource();

  Map<String, String> hashEntriesOf(File archive) throws IOException;
}

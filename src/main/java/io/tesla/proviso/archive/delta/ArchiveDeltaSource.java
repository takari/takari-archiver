package io.tesla.proviso.archive.delta;

import com.google.common.io.ByteStreams;
import io.tesla.proviso.archive.ArchiveHandler;
import io.tesla.proviso.archive.Archiver;
import io.tesla.proviso.archive.ArchiverHelper;
import io.tesla.proviso.archive.Entry;
import io.tesla.proviso.archive.Source;
import io.tesla.proviso.archive.delta.ArchiveDelta.ArchiveDeltaData;
import io.tesla.proviso.archive.delta.ArchiveDelta.DeltaOperation;
import io.tesla.proviso.archive.delta.ArchiveDelta.DeltaInstruction;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class ArchiveDeltaSource implements Source {

  private Source source;
  private ArchiveDeltaData delta;
  ArchiveHandler archiveHandler;


  public ArchiveDeltaSource(File sourceArchive, Source source, ArchiveDeltaData delta) {
    this.source = source;
    this.delta = delta;
    this.archiveHandler = ArchiverHelper.getArchiveHandler(sourceArchive, true);
  }

  @Override
  public Iterable<Entry> entries() {
    return () -> new ArchiveDeltaSourceIterator(source.entries().iterator(), delta);
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public void close() throws IOException {

  }

  public class ArchiveDeltaSourceIterator implements Iterator<Entry> {

    private Iterator<Entry> iterator;
    private ArchiveDeltaData delta;

    public ArchiveDeltaSourceIterator(Iterator<Entry> iterator, ArchiveDeltaData delta) {
      this.iterator = iterator;
      this.delta = delta;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    /*

    same
    same
    same
    removal
    difference
    addition

     */

    @Override
    public Entry next() {
      Entry entry = iterator.next();
      System.out.println(entry.getName());
      DeltaOperation deltaEntry = delta.removalsAndDifferences.get(entry.getName());
      if (deltaEntry != null) {
        // removals: removalsAndDifferences that are not present in the target
        if (deltaEntry.instruction.equals(DeltaInstruction.REMOVAL)) {
          System.out.println("REMOVAL!!!");
          if (hasNext()) {
            entry = iterator.next();
          }
        } else if (deltaEntry.instruction.equals(DeltaInstruction.DIFFERENCE)) {
          System.out.println("DIFFERENCE!!!");
          entry = new Entry() {

            @Override
            public String getName() {
              return deltaEntry.path;
            }

            @Override
            public InputStream getInputStream() throws IOException {
              return new ByteArrayInputStream(deltaEntry.data);
            }

            @Override
            public long getSize() {
              return deltaEntry.data.length;
            }

            @Override
            public void writeEntry(OutputStream outputStream) throws IOException {
              System.out.println("writing out the data!!!");
              ByteStreams.copy(getInputStream(), outputStream);
            }

            @Override
            public int getFileMode() {
              return 0;
            }

            @Override
            public boolean isDirectory() {
              return deltaEntry.path.endsWith("/");
            }

            @Override
            public boolean isExecutable() {
              return false;
            }

            @Override
            public long getTime() {
              return Archiver.normalizedTimestamp(deltaEntry.path);
            }
          };
        }
      }
      return entry;
    }
  }
}

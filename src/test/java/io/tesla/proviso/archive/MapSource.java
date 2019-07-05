package io.tesla.proviso.archive;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

public class MapSource implements Source {

  Map<String, String> entries;

  public MapSource(Map<String, String> entries) {
    this.entries = entries;
  }

  @Override
  public Iterable<Entry> entries() {

    return new Iterable<Entry>() {
      @Override
      public Iterator<Entry> iterator() {
        return new MapEntryIterator(entries.entrySet().iterator());
      }
    };
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public void close() throws IOException {

  }

  public class MapEntryIterator implements Iterator<Entry> {

    Iterator<Map.Entry<String, String>> entries;

    public MapEntryIterator(Iterator<Map.Entry<String, String>> entries) {
      this.entries = entries;
    }

    @Override
    public boolean hasNext() {
      return entries.hasNext();
    }

    @Override
    public Entry next() {
      Map.Entry<String, String> entry = entries.next();
      return new MapEntry(entry);
    }
  }

  public class MapEntry implements Entry {

    Map.Entry<String, String> entry;

    public MapEntry(Map.Entry<String, String> entry) {
      this.entry = entry;
    }

    @Override
    public String getName() {
      return entry.getKey();
    }

    @Override
    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(entry.getValue().getBytes());
    }

    @Override
    public long getSize() {
      if (entry.getValue() == null) {
        return 0;
      }
      return entry.getValue().length();
    }

    @Override
    public void writeEntry(OutputStream outputStream) throws IOException {
      ByteStreams.copy(getInputStream(), outputStream);
    }

    @Override
    public int getFileMode() {
      return 0;
    }

    @Override
    public boolean isDirectory() {
      return entry.getKey().endsWith("/");
    }

    @Override
    public boolean isExecutable() {
      return false;
    }

    @Override
    public long getTime() {
      return 0;
    }
  }
}

package io.tesla.proviso.archive.delta;

import com.google.common.io.BaseEncoding;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarHash {

  MessageDigest md;

  public JarHash() {
    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      // Not going to happen. But in some weird even it does throw a runtime exception so
      // that it's not lost.
      throw new RuntimeException(e);
    }
  }

  public Map<String, String> entries(File file) throws IOException {

    Map<String, String> paths = new HashMap<>();

    ZipFile zip = new ZipFile(file);
    Enumeration<? extends ZipEntry> entries = zip.entries();

    // Since we're summing the hash hashOf all files, ordering matters. Sort before computing.
    Map<String, ZipEntry> map = new HashMap<String, ZipEntry>();

    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      if (!entry.isDirectory()) {
        map.put(entry.getName(), entry);
      }
    }

    ArrayList<String> files = new ArrayList<String>(map.keySet());
    Collections.sort(files);

    for (String key : files) {
      paths.put(key, sha1(zip.getInputStream(map.get(key))));
    }

    zip.close();

    return paths;
  }

  //
  // We need to be able to deal with any sort hashOf content, not just JARs
  //
  public String sum(File file) throws IOException {

    BigInteger sum = BigInteger.ZERO;
    ZipFile zip = new ZipFile(file);
    Enumeration<? extends ZipEntry> entries = zip.entries();

    // Since we're summing the hash hashOf all files, ordering matters. Sort before computing.
    Map<String, ZipEntry> map = new HashMap<String, ZipEntry>();

    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      if (!entry.isDirectory()) {
        map.put(entry.getName(), entry);
      }
    }

    ArrayList<String> files = new ArrayList<String>(map.keySet());
    Collections.sort(files);

    // Compute and log hash for all files
    for (String key : files) {
      BigInteger i = sha1AsBigInteger(zip.getInputStream(map.get(key)));
      sum = sum.add(i);
    }

    zip.close();
    return hex(sum.toByteArray());
  }

  private BigInteger sha1AsBigInteger(InputStream is) throws IOException {
    md.reset();
    DigestInputStream in = new DigestInputStream(is, md);
    byte[] buffer = new byte[8192];
    while (in.read(buffer) != -1) {
      ;
    }
    return new BigInteger(1, md.digest()); // use this 1 to tell it is positive.    
  }

  private String sha1(InputStream is) throws IOException {
    md.reset();
    DigestInputStream in = new DigestInputStream(is, md);
    byte[] buffer = new byte[8192];
    while (in.read(buffer) != -1) {
      ;
    }
    return hex(md.digest()); // use this 1 to tell it is positive.    
  }

  public String hex(byte[] bytes) {
    return BaseEncoding.base16().encode(bytes).toLowerCase();
  }

  public static void main(String[] args) throws Exception {

    if (args.length == 0) {
      System.out.println("java -jar workspace-hash.jar file0 [file1 file2 ... fileN]");
    }

    if (args.length > 0) {
      JarHash h = new JarHash();
      for (String filePath : args) {
        File f = new File(filePath);
        if (f.exists()) {
          System.out.println(f.getName() + ": " + h.sum(f));
        }
      }
    }
  }
}
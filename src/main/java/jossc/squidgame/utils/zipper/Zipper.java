package jossc.squidgame.utils.zipper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {

  /** A constants for buffer size used to read/write data */
  private static final int BUFFER_SIZE = 4096;

  public static void zip(List<File> listFiles, String destZipFile)
    throws IOException {
    ZipOutputStream zos = new ZipOutputStream(
      new FileOutputStream(destZipFile)
    );
    for (File file : listFiles) {
      if (file.isDirectory()) {
        zipDirectory(file, file.getName(), zos);
      } else {
        zipFile(file, zos);
      }
    }
    zos.flush();
    zos.close();
  }

  public static void zip(String dir, String destZipDir) throws IOException {
    if (!new File(dir).exists()) return;

    zip(new String[] { dir }, destZipDir);
  }

  public static void zip(String[] files, String destZipFile)
    throws IOException {
    List<File> listFiles = new ArrayList<>();
    for (String file : files) listFiles.add(new File(file));

    zip(listFiles, destZipFile);
  }

  public static void unzip(String zipFilePath, String destDirectory)
    throws IOException {
    File destDir = new File(destDirectory);
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
    ZipEntry entry = zipIn.getNextEntry();

    while (entry != null) {
      String filePath = destDirectory + File.separator + entry.getName();
      if (!entry.isDirectory()) {
        new File(filePath).getParentFile().mkdirs();
        extractFile(zipIn, filePath);
      } else {
        File dir = new File(filePath);
        dir.mkdirs();
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();
  }

  private static void extractFile(ZipInputStream zipIn, String filePath)
    throws IOException {
    BufferedOutputStream bos = new BufferedOutputStream(
      new FileOutputStream(filePath)
    );
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

  private static void zipDirectory(
    File folder,
    String parentFolder,
    ZipOutputStream zos
  ) throws IOException {
    for (File file : Objects.requireNonNull(folder.listFiles())) {
      if (file.isDirectory()) {
        zipDirectory(file, parentFolder + "/" + file.getName(), zos);
        continue;
      }
      zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
      BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream(file)
      );
      byte[] bytesIn = new byte[BUFFER_SIZE];
      int read;
      while ((read = bis.read(bytesIn)) != -1) {
        zos.write(bytesIn, 0, read);
      }
      zos.closeEntry();
    }
  }

  private static void zipDirectoryExclude(
    File folder,
    String parentFolder,
    ZipOutputStream zos
  ) throws IOException {
    for (File file : Objects.requireNonNull(folder.listFiles())) {
      if (file.isDirectory()) {
        zipDirectory(file, parentFolder + "/" + file.getName(), zos);
        continue;
      }
      zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
      BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream(file)
      );
      byte[] bytesIn = new byte[BUFFER_SIZE];
      int read;
      while ((read = bis.read(bytesIn)) != -1) {
        zos.write(bytesIn, 0, read);
      }
      zos.closeEntry();
    }
  }

  private static void zipFile(File file, ZipOutputStream zos)
    throws IOException {
    zos.putNextEntry(new ZipEntry(file.getName()));
    BufferedInputStream bis = new BufferedInputStream(
      new FileInputStream(file)
    );
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read;
    while ((read = bis.read(bytesIn)) != -1) {
      zos.write(bytesIn, 0, read);
    }
    zos.closeEntry();
  }
}

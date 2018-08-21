package com.legendmohe.gradle.methodtrace;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by hexinyu on 2018/8/21.
 */
public class FileUtil {
    private boolean isZipEmpty(String zipFilePath) {
        ZipFile z = null;
        try {
            try {
                z = new ZipFile(zipFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return z.size() == 0;
        } finally {
            try {
                z.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unzipFile(String sourcefiles, String decompreDirectory) throws IOException {
        ZipFile readfile = null;
        try {
            readfile = new ZipFile(sourcefiles);
            Enumeration takeentrie = readfile.entries();
            ZipEntry zipEntry = null;
            File credirectory = new File(decompreDirectory);
            credirectory.mkdirs();
            while (takeentrie.hasMoreElements()) {
                zipEntry = (ZipEntry) takeentrie.nextElement();
                String entryName = zipEntry.getName();
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        File createDirectory = new File(decompreDirectory + File.separator + name);
                        createDirectory.mkdirs();
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            File createDirectory = new File(decompreDirectory + File.separator + entryName.substring(0, index));
                            createDirectory.mkdirs();
                        }
                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            File createDirectory = new File(decompreDirectory + File.separator + entryName.substring(0, index));
                            createDirectory.mkdirs();
                        }
                        File unpackfile = new File(decompreDirectory + File.separator + zipEntry.getName());
                        in = readfile.getInputStream(zipEntry);
                        out = new FileOutputStream(unpackfile);
                        int c;
                        byte[] by = new byte[1024];
                        while ((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }
                        out.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new IOException("解压失败：" + ex.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {

                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    in = null;
                    out = null;
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("解压失败：" + ex.toString());
        } finally {
            if (readfile != null) {
                try {
                    readfile.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new IOException("解压失败：" + ex.toString());
                }
            }
        }
    }

    private boolean unzip(String zipFilePath, String dirPath) {
        // 若这个Zip包是空内容的（如引入了Bugly就会出现），则直接忽略
        if (isZipEmpty(zipFilePath)) {
            return false;
        }

        try {
            unzipFile(zipFilePath, dirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Lists files in a directory, asserting that the supplied directory satisfies exists and is a directory
     *
     * @param directory The directory to list
     * @return The files in the directory, never null.
     * @throws IOException if an I/O error occurs
     */
    private static File[] verifiedListFiles(File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    /**
     * Cleans a directory without deleting it.
     *
     * @param directory directory to clean
     * @throws IOException              in case cleaning is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void cleanDirectory(final File directory) throws IOException {
        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws IOException              in case deletion is unsuccessful
     * @throws IllegalArgumentException if {@code directory} does not exist or is not a directory
     */
    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        // 大部分不需要处理Symlink的情况
//        if (!isSymlink(directory)) {
        cleanDirectory(directory);
//        }

        if (!directory.delete()) {
            final String message =
                    "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    public static void forceDelete(final File file) throws IOException {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message =
                        "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void clearFile(File rootFile) {
        if (null == rootFile) {
            return;
        }
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            if (null != files && files.length > 0) {
                for (File file : files) {
                    clearFile(file);
                }
            }
        }
        rootFile.delete();
    }

    public static boolean hasFiles(File rootFile) {
        if (null == rootFile || !rootFile.exists()) {
            return false;
        }
        return null != rootFile.listFiles() && rootFile.listFiles().length > 0;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
                closeable = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean containsClass(JarFile jarFile, String className) {
        if (null != jarFile) {
            Enumeration<JarEntry> entries = jarFile.entries();
            if (null != entries) {
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (!jarEntry.isDirectory()) {
                        String jarEntryName = jarEntry.getName().replaceAll("/", ".");
                        if (jarEntryName == className) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void unzipJarFile(JarFile jarFile, File rootFile) throws IOException {
        if (null == jarFile || null == rootFile) {
            Util.log("params invalid before unzip jar file !!!");
            return;
        }
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }

        Enumeration<JarEntry> entries = jarFile.entries();
        if (null == entries) {
            return;
        }

        InputStream is;
        OutputStream os;
        FileOutputStream fos;

        byte[] buffer = new byte[10240];

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory()) {
                File entityFile = new File(rootFile, jarEntry.getName());
                if (!entityFile.getParentFile().exists()) {
                    entityFile.getParentFile().mkdirs();
                }
                if (entityFile.exists()) {
                    entityFile.delete();
                }
                entityFile.createNewFile();

                is = jarFile.getInputStream(jarEntry);
                fos = new FileOutputStream(entityFile);
                os = new BufferedOutputStream(fos);

                int readCount;
                while (-1 != (readCount = is.read(buffer))) {
                    os.write(buffer, 0, readCount);
                }

                os.flush();
                fos.flush();

                closeQuietly(os);
                closeQuietly(fos);
                closeQuietly(is);
            }
        }
    }

    public static void zipJarFile(File srcDir, File destFile) throws IOException {
        if (null == srcDir) {
            Util.log("srcDir is null");
            return;
        }

        if (!srcDir.exists()) {
            Util.log("srcDir is not exist");
            return;
        }

        if (null == destFile) {
            Util.log("desFile is null");
            return;
        }

        if (".DS_Store".equals(srcDir.getName())) {
            return;
        }

        if (!destFile.exists()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
        }

        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destFile));
        if (srcDir.isDirectory()) {
            File[] files = srcDir.listFiles();
            if (null != files && files.length > 0) {
                for (File file : files) {
                    zipInternal(zipOutputStream, file, file.getName() + File.separator);
                }
            }
        }
        zipOutputStream.flush();
        closeQuietly(zipOutputStream);
    }

    private static void zipInternal(ZipOutputStream out, File file, String baseDir) throws IOException {
        if (".DS_Store".equals(file.getName())) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                for (File f : files) {
                    zipInternal(out, f, baseDir + f.getName() + File.separator);
                }
            }
        } else {
            byte[] buffer = new byte[10240];
            InputStream input = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(baseDir.substring(0, baseDir.indexOf(file.getName())) + file.getName()));

            int readCount;
            while (-1 != (readCount = input.read(buffer))) {
                out.write(buffer, 0, readCount);
            }
            out.flush();
            closeQuietly(input);
        }
    }
}

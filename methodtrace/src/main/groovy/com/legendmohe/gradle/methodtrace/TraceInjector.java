package com.legendmohe.gradle.methodtrace;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.MethodInfo;

/**
 * Created by hexinyu on 2018/7/20.
 */
public class TraceInjector implements ITraceInjector {

    private TraceConfig mTraceConfig;
    private Project mProject;

    public TraceInjector(Project project, BaseExtension android) {
        mProject = project;
    }

    @Override
    public void onClassPathPrepared() {
        Object config = mProject.getExtensions().findByName(TraceConfig.class.getSimpleName());
        if (config == null) {
            mTraceConfig = new TraceConfig();
        } else {
            mTraceConfig = (TraceConfig) config;
        }

        Util.log(mTraceConfig.toString());
    }

    @Override
    public synchronized void injectDir(String srcPath, ClassPool pool) {

        if (mTraceConfig.targetPackagePath == null || mTraceConfig.targetPackagePath.length == 0) {
            Util.log("empty targetPackagePath");
            return;
        } else {
            Util.log("processing src dir:" + srcPath);
        }

        if (!ensureTargetProjects(mProject)) {
            Util.log("not in targetProjects, skip it:" + mProject.getDisplayName());
            return;
        }

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                try {
                    String filePath = file.toString();
                    //确保当前文件是class文件，并且不是系统自动生成的class文件
                    if (filePath.endsWith(".class")) {
                        // 判断当前目录是否是在我们的应用包里面
                        String className = filePath.substring(srcPath.length() + 1, filePath.length() - 6);
                        className = className.replace('\\', '.').replace('/', '.');
                        if (mTraceConfig.targetPackagePath != null && mTraceConfig.targetPackagePath.length > 0) {
                            if (!Util.containsIn(className, mTraceConfig.targetPackagePath)) {
                                return FileVisitResult.CONTINUE;
                            }
                        }
                        if (mTraceConfig.targetClasses != null && mTraceConfig.targetClasses.length > 0) {
                            if (!Util.containsIn(className, mTraceConfig.targetClasses)) {
                                return FileVisitResult.CONTINUE;
                            }
                        }
                        if (Util.startsWith(className, mTraceConfig.skipPackages)) {
                            return FileVisitResult.CONTINUE;
                        }
                        if (Util.containsIn(className, mTraceConfig.skipClasses)) {
                            return FileVisitResult.CONTINUE;
                        }
                        if (Util.endsWith(className, TraceConfig.SKIP_CLASSES_SUFFIX_INTERNAL)) {
                            return FileVisitResult.CONTINUE;
                        }
                        CtClass c = pool.getCtClass(className);
                        if (c.isFrozen()) {
                            c.defrost();
                        }

                        if (!(c.isInterface() || c.isEnum() || c.isAnnotation())) {
                            //开始修改class文件，注意，没有方法的类不能write，dex会出错
                            if (injectTargetCtClass(c)) {
                                c.writeFile(srcPath);
                            } else {
                                Util.log("nothing changed to " + c.getName());
                            }
                        } else {
                            Util.log("skip target class: " + c.getName());
                        }
                        c.detach();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(Paths.get(srcPath), fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized File injectJar(File jar, String jarName, ClassPool pool) {
        Util.log("injectJar jar:" + jar.getAbsolutePath());

        if (!jar.getAbsolutePath().contains(mProject.getRootDir().getAbsolutePath())) {
            Util.log("invalid jar:" + jar.getAbsolutePath());
            return jar;
        }

        File destFile = null;
        try {
            ZipFile zipFile = new ZipFile(jar);
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
            return destFile;
        }

        String baseDir = new StringBuilder().append(mProject.getProjectDir().getAbsolutePath())
                .append(File.separator).append("methodTemp")
                .append(File.separator).append("1.0.0")
                .append(File.separator).append(jarName).toString();

        File rootFile = new File(baseDir);
        FileUtil.clearFile(rootFile);
        rootFile.mkdirs();

        File unzipDir = new File(rootFile, "classes");
        File jarDir = new File(rootFile, "jar");

        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jar);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!FileUtil.hasFiles(unzipDir)) {
            try {
                FileUtil.unzipJarFile(jarFile, unzipDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 2、开始注入文件，需要注意的是，appendClassPath后边跟的根目录，没有后缀，className后完整类路径，也没有后缀
        try {
            pool.appendClassPath(unzipDir.getAbsolutePath());
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        //3遍历所有jar
        injectDir(unzipDir.getAbsolutePath(), pool);

        // 4、循环体结束，判断classes文件夹下是否有文件
        try {
            if (FileUtil.hasFiles(unzipDir)) {

                destFile = new File(jarDir, jar.getName());
                FileUtil.clearFile(destFile);
                FileUtil.zipJarFile(unzipDir, destFile);

                FileUtil.clearFile(unzipDir);
            } else {
                FileUtil.clearFile(rootFile);
            }
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destFile;
    }

    private boolean injectTargetCtClass(CtClass c) throws CannotCompileException, NotFoundException {
        Util.log("inject target class: " + c.getName());
        // 收集所有方法
        Collection<CtMethod> methodSet = new HashSet<>();
        methodSet.addAll(Arrays.asList(c.getDeclaredMethods()));
//        methodSet.addAll(Arrays.asList(c.getMethods()));

        boolean changed = false;
        for (CtMethod ctMethod : methodSet) {
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            if (methodInfo.isConstructor()) {
                continue;
            }
            if (methodInfo.isStaticInitializer()) {
                continue;
            }
            if ((methodInfo.getAccessFlags() & AccessFlag.ABSTRACT) != 0x00) {
                continue;
            }
            if ((methodInfo.getAccessFlags() & AccessFlag.NATIVE) != 0x00) {
                continue;
            }
            if (methodInfo.isMethod() && !shouldSkipProcessMethod(c, ctMethod)) {
//                Util.log("inject method: " + ctMethod.getLongName());
                ctMethod.insertBefore(getStatementInsertBefore(c, ctMethod));
                ctMethod.insertAfter(getStatementInsertAfter(c, ctMethod), true);
                changed = true;
            }
        }
        return changed;
    }

    ////////////////////////////////////private//////////////////////////////////

    private String getStatementInsertBefore(CtClass clazz, CtMethod method) {
        return setStatementParams(clazz, method, mTraceConfig.STATEMENT_INSERT_BEFORE);
    }

    private String getStatementInsertAfter(CtClass clazz, CtMethod method) {
        return setStatementParams(clazz, method, mTraceConfig.STATEMENT_INSERT_AFTER);
    }

    private boolean shouldSkipProcessMethod(CtClass c, CtMethod ctMethod) {
        String longName = ctMethod.getLongName();
        for (String prefix : mTraceConfig.skipMethodContains) {
            if (longName.contains(prefix))
                return true;
        }
        return false;
    }

    private String setStatementParams(CtClass clazz, CtMethod method, String resultString) {
        resultString = resultString.replace("%c", clazz.getName());
        resultString = resultString.replace("%m", method.getName());
        if ((method.getMethodInfo().getAccessFlags() & AccessFlag.STATIC) != 0x00) {
            // static
            resultString = resultString.replace("%t", "0"); // 不要hashCode了，容易死循环
        } else {
            // not static
            resultString = resultString.replace("%t", "0"); // 不要hashCode了，容易死循环
        }
        return resultString;
    }


    private boolean ensureTargetProjects(Project project) {
        if (mTraceConfig != null) {
            if (mTraceConfig.targetProjects != null && mTraceConfig.targetProjects.length > 0) {
                if (!Util.containsIn(project.getDisplayName(), mTraceConfig.targetProjects)) {
                    return false;
                }
            }
        }
        return true;
    }
}

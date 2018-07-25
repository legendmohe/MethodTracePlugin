package com.legendmohe.gradle.methodtrace;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Project;

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
import java.util.LinkedList;
import java.util.Set;

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
public class TraceInjector {

    private TraceConfig mTraceConfig;
    private Project mProject;

    private static Set<String> sProcessedClassNames = new HashSet<>();

    public TraceInjector(Project project, BaseExtension android) {
        mProject = project;
    }

    public void onClassPathPrepared() {
        Object config = mProject.getExtensions().findByName(TraceConfig.class.getSimpleName());
        if (config == null) {
            mTraceConfig = new TraceConfig();
        } else {
            mTraceConfig = (TraceConfig) config;
        }

        Util.log(mTraceConfig.toString());
    }

    public void injectDir(String path, ClassPool pool) {

        if (mTraceConfig.targetPackagePath == null || mTraceConfig.targetPackagePath.length == 0) {
            Util.log("empty targetPackagePath");
            return;
        } else {
            Util.log("processing dir:" + path);
        }
        Set<String> targetClassNames = new HashSet<>();
        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                try {
                    String filePath = file.toString();
                    //确保当前文件是class文件，并且不是系统自动生成的class文件
                    if (filePath.endsWith(".class")) {
                        // 判断当前目录是否是在我们的应用包里面
                        String className = filePath.replace('\\', '.').replace('/', '.');
                        int index = -1;
                        for (String packageName : mTraceConfig.targetPackagePath) {
                            index = className.indexOf(packageName);
                            if (index != -1) {
                                break;
                            }
                        }
//                        Util.log("check package:" + className + " " + index);
                        boolean isMyPackage = index != -1;
                        if (isMyPackage) {
                            int end = className.length() - 6;// .class = 6
                            className = className.substring(index, end);
                            // 先存起来，后面一起处理
                            targetClassNames.add(className);
                        }
//                        if (!Util.containsIn(className, mTraceConfig.skipClasses)) {
//                            int index = -1;
//                            for (String packageName : mTraceConfig.targetPackagePath) {
//                                index = className.indexOf(packageName);
//                                if (index != -1) {
//                                    break;
//                                }
//                            }
//                            boolean isMyPackage = index != -1;
//                            if (isMyPackage) {
//                                int end = className.length() - 6;// .class = 6
//                                className = className.substring(index, end);
//                                CtClass c = pool.getCtClass(className);
//                                if (c.isFrozen()) {
//                                    c.defrost();
//                                }
//
//                                if (!(c.isInterface() || c.isEnum() || c.isAnnotation())) {
//                                } else {
//                                    Util.log("skip target class: " + c.getName());
//                                }
//
//                                c.writeFile(path);
//                                c.detach();
//                            }
//                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(Paths.get(path), fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LinkedList<String> pendingProcessClasses = new LinkedList<>(targetClassNames);
        while (!pendingProcessClasses.isEmpty()) {
            String curClassName = pendingProcessClasses.removeFirst();
            if (sProcessedClassNames.contains(curClassName)) {
                continue;
            }
            if (!Util.startsWith(curClassName, mTraceConfig.skipPackages)
                    && !Util.endsWith(curClassName, TraceConfig.SKIP_CLASSE_INTERNAL)
                    && !Util.containsIn(curClassName, mTraceConfig.skipClasses)) {

                try {
                    CtClass c = pool.getCtClass(curClassName);
                    if (c.isFrozen()) {
                        c.defrost();
                    }

                    if (!(c.isInterface() || c.isEnum() || c.isAnnotation())) {
                        Collection<String> refClasses = c.getRefClasses();
                        for (String refClass : refClasses) {
                            Util.log("add refClass:" + refClass);
                            if (!refClass.equals(c.getName())) {
                                pendingProcessClasses.add(refClass);
                            }
                        }
                        //开始修改class文件
                        injectTargetCtClass(c);
                        sProcessedClassNames.add(c.getName());
                    } else {
                        Util.log("skip target class: " + c.getName());
                    }
                    Util.log("write file:" + path);
                    c.writeFile(path);
                    c.detach();
                } catch (CannotCompileException | IOException | NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void injectTargetCtClass(CtClass c) throws CannotCompileException, NotFoundException {
        Util.log("inject target class: " + c.getName());
        // 收集所有方法
        Collection<CtMethod> methodSet = new HashSet<>();
        methodSet.addAll(Arrays.asList(c.getDeclaredMethods()));
//        methodSet.addAll(Arrays.asList(c.getMethods()));

        for (CtMethod ctMethod : methodSet) {
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            if (methodInfo.isConstructor())
                continue;
            if (methodInfo.isStaticInitializer())
                continue;
            if (methodInfo.isMethod() && !shouldSkipProcessMethod(c, ctMethod)) {
                Util.log("inject method: " + ctMethod.getLongName());
                ctMethod.insertBefore(getStatementInsertBefore(c, ctMethod));
                ctMethod.insertAfter(getStatementInsertAfter(c, ctMethod), true);
            }
        }
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
}

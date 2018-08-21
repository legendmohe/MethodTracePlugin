package com.legendmohe.gradle.methodtrace;

import java.io.File;

import javassist.ClassPool;

/**
 * Created by hexinyu on 2018/7/27.
 */
interface ITraceInjector {
    void onClassPathPrepared();

    void injectDir(String srcPath, ClassPool pool);

    File injectJar(File jar, ClassPool pool);
}

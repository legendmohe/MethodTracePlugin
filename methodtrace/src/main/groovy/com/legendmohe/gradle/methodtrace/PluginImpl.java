package com.legendmohe.gradle.methodtrace;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PluginImpl implements Plugin<Project> {
    public void apply(Project project) {
        AppExtension android = project.getExtensions().findByType(AppExtension.class);
        LibraryExtension androidLibrary = project.getExtensions().findByType(LibraryExtension.class);

        project.getExtensions().create("TraceConfig", TraceConfig.class);

        if (!ensureExtension(android, androidLibrary)) {
            Util.log("trace skip apply project: " + project.getDisplayName());
            return;
        }
        Util.log("trace apply project: " + project.getDisplayName());
        if (android != null) {
            android.registerTransform(new TraceTransform(project, android));
        } else if (androidLibrary != null) {
            androidLibrary.registerTransform(new TraceTransform(project, androidLibrary));
        }
    }

    private boolean ensureExtension(AppExtension android, LibraryExtension androidLibrary) {
        return android != null || androidLibrary != null;
    }
}
package com.legendmohe.gradle.methodtrace;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ComponentMetadataDetails;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.dsl.ComponentMetadataHandler;
import org.gradle.api.artifacts.dsl.ComponentModuleMetadataHandler;

public class PluginImpl implements Plugin<Project> {
    public void apply(Project project) {
        project.getExtensions().create("TraceConfig", TraceConfig.class);
//        project.afterEvaluate(new Action<Project>() {
//            @Override
//            public void execute(Project project) {
////                DependencySet allDependencies = project.getConfigurations()..getByName("compile").getAllDependencies();
////                for (Dependency dependency : allDependencies) {
////                    Util.log("dependency: " + dependency.getName());
////                }
//
//                Util.log("try register Transform to project: " + project.getDisplayName());
//                AppExtension android = project.getExtensions().findByType(AppExtension.class);
//                LibraryExtension androidLibrary = project.getExtensions().findByType(LibraryExtension.class);
//
//                if (!ensureExtension(android, androidLibrary)) {
//                    Util.log("skip apply project: " + project.getDisplayName());
//                    return;
//                }
//                if (android != null) {
//                    Util.log("register Transform to app project: " + project.getDisplayName());
//                    android.registerTransform(new TraceTransform(project, android));
//                } else if (androidLibrary != null) {
//                    Util.log("register Transform to androidLibrary project: " + project.getDisplayName());
//                    androidLibrary.registerTransform(new TraceTransform(project, androidLibrary));
//                }
//            }
//        });

        Util.log("try register Transform to project: " + project.getDisplayName());
        AppExtension android = project.getExtensions().findByType(AppExtension.class);
        LibraryExtension androidLibrary = project.getExtensions().findByType(LibraryExtension.class);

        if (!ensureExtension(android, androidLibrary)) {
            Util.log("skip apply project: " + project.getDisplayName());
            return;
        }
        if (android != null) {
            Util.log("register Transform to app project: " + project.getDisplayName());
            android.registerTransform(new TraceTransform(project, android));
        } else if (androidLibrary != null) {
            Util.log("register Transform to androidLibrary project: " + project.getDisplayName());
            androidLibrary.registerTransform(new TraceTransform(project, androidLibrary));
        }
    }

    private boolean ensureExtension(AppExtension android, LibraryExtension androidLibrary) {
        return android != null || androidLibrary != null;
    }
}
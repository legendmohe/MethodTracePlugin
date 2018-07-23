package com.legendmohe.plugin;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PluginImpl implements Plugin<Project> {
    public void apply(Project project) {
        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        TraceTransform classTransform = new TraceTransform(project, android);
        android.registerTransform(classTransform);
    }
}
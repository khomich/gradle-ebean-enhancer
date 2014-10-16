package com.khomich.ebean.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskOutputs

/**
 * Implementation of Gradle plugin for Ebean enhancement task
 */
class EbeanPlugin implements Plugin<Project> {
    private static def supportedCompilerTasks = ['compileJava', 'compileGroovy', 'compileScala']

    void apply(Project project) {
        def params = project.extensions.create('ebean', EbeanPluginParams)
        def tasks = project.tasks


        supportedCompilerTasks.each { compileTask ->
            tryHookCompilerTask(tasks, compileTask, params)
        }
    }

    private static void tryHookCompilerTask(TaskContainer tasks, String taskName, EbeanPluginParams params) {
        try {
            def task = tasks.getByName(taskName)

            task.doLast({ completedTask ->
                enhanceTaskOutput(completedTask.outputs, params)
            })
        } catch (UnknownTaskException _) {
            ; //just plugin is not activated
        }
    }

    private static void enhanceTaskOutput(TaskOutputs taskOutputs, EbeanPluginParams params) {
        taskOutputs.files.each { outputDir ->
            if (outputDir.isDirectory()) {
                def classPath = outputDir.toPath()
                def fileFilter = new EbeanFileFilter(classPath, params.include, params.exclude)
                new EBeanEnhancer(classPath, fileFilter, params.debugLevel).enhance()
            }
        }
    }
}

class EbeanPluginParams {
    /**
     * Accepts class and packages name only.
     */
    String[] include = []

    /**
     * Accepts class and packages name only.
     */
    String[] exclude = []

    /**
     * Ebean enhancer debug level
     */
    int debugLevel = 1
}


package com.khomich.ebean.gradle

import java.nio.file.Path
import java.nio.file.Paths

class EbeanFileFilter implements FileFilter {
    private FileFilter includeFilter = { file -> true }
    private FileFilter excludeFilter = { file -> false }

    EbeanFileFilter(Path classPath, String[] include, String[] exclude) {
        if (include.length > 0) {
            includeFilter = new ClassFileFilter(classPath, include)
        }

        if (exclude.length > 0) {
            excludeFilter = new ClassFileFilter(classPath, exclude)
        }
    }

    @Override
    boolean accept(File pathname) {
        return includeFilter.accept(pathname) && !excludeFilter.accept(pathname)
    }
}

class ClassFileFilter implements FileFilter {
    private final Path classPath;
    private final ClassNameMatcher[] matchers;

    ClassFileFilter(Path classPath, String[] patterns) {
        this.classPath = classPath;
        matchers = patterns.collect { pattern -> new ClassNameMatcher(pattern)}
    }

    @Override
    boolean accept(File pathname) {
        def className = ClassUtils.makeClassName(classPath, pathname)
        matchers.any { matcher -> matcher.matches(className)}
    }
}

class ClassNameMatcher {
    private final String pattern

    ClassNameMatcher(String pattern) {
        this.pattern = pattern
    }

    boolean matches(String className) {
        className.startsWith(pattern) || className.endsWith(pattern)
    }
}

class ClassUtils {
    static String makeClassName(Path basePath, File classFile) {
        def classRelPath = basePath.relativize(Paths.get(classFile.toURI()))
        classRelPath.toString().replaceAll('[.]class$', '').replace('\\', '.').replace('//', '.')
    }
}
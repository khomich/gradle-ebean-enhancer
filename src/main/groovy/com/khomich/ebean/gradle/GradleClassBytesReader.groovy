package com.khomich.ebean.gradle

import com.avaje.ebean.enhance.agent.ClassBytesReader

import java.nio.file.Path

/**
 * Implements additional reader for class bytes when ebean agent refers to super class
 */
class GradleClassBytesReader implements ClassBytesReader {
    private final Path basePath;

    GradleClassBytesReader(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    byte[] getClassBytes(String className, ClassLoader classLoader) {
        if (classLoader instanceof ByteClassLoader) {
            def byteClassLoader = classLoader as ByteClassLoader
            def bytes = byteClassLoader.getClassBytes(className.replace("/", ".").replace('$', '.'))
            if (null != bytes) {
                return bytes
            }
        }

        def classFilePath = basePath.resolve(className.replace(".", "/") + ".class");
        def file = classFilePath.toFile()
        def buffer = new byte[file.length()]

        try {
            file.withInputStream { classFileStream -> classFileStream.read(buffer) }
        } catch (IOException e) {
            throw new EbeanEnhancementException("Failed to load class '$className' at base path '$basePath'", e);
        }

        buffer
    }
}

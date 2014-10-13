package com.khomich.ebean.gradle

import com.avaje.ebean.enhance.agent.ClassBytesReader
import org.apache.commons.io.IOUtils

import java.nio.file.Path

/**
 * Implements additional reader for class bytes when ebean agent refers to super class
 */
class FileSystemClassBytesReader implements ClassBytesReader {
    private final Path basePath;

    FileSystemClassBytesReader(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    byte[] getClassBytes(String className, ClassLoader classLoader) {
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

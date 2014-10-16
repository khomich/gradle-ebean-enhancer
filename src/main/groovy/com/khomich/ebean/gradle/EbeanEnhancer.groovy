package com.khomich.ebean.gradle

import com.avaje.ebean.enhance.agent.InputStreamTransform
import com.avaje.ebean.enhance.agent.Transformer

import java.lang.instrument.IllegalClassFormatException
import java.nio.file.Path

class EBeanEnhancer {
    private final Path classPath
    private final FileFilter fileFilter
    private final int debugLevel

    EBeanEnhancer(Path classPath, FileFilter fileFilter, int debugLevel) {
        this.classPath = classPath
        this.fileFilter = fileFilter
        this.debugLevel = debugLevel
    }

    void enhance() {
        collectClassFiles(classPath.toFile()).each { classFile ->
            if (fileFilter.accept(classFile)) {
                enhanceClassFile(classFile);
            }
        }
    }

    private void enhanceClassFile(File classFile) {
        def transformer = new Transformer(new GradleClassBytesReader(classPath), "debug=" + debugLevel);//0-9 -> none - all
        def streamTransform = new InputStreamTransform(transformer, new ByteClassLoader(getClass().getClassLoader()))

        def className = ClassUtils.makeClassName(classPath, classFile);

        if (
            className.contains('$$anonfun$') ||     //scala lambda: anonymous function
            className.contains('$_')                //groovy meta info classes & closures
        ) return

        try {
            classFile.withInputStream { classInputStream ->
                def enhancedClassData = streamTransform.transform(className, classInputStream)

                if (null != enhancedClassData) { //transformer returns null when nothing was transformed
                    try {
                        classFile.withOutputStream { classOutputStream ->
                            classOutputStream << enhancedClassData
                        }
                    } catch (IOException e) {
                        throw new EbeanEnhancementException("Unable to store фт enhanced class data back to file $classFile.name", e);
                    }
                }
            }
        } catch (IOException e) {
            throw new EbeanEnhancementException("Unable to read ф class file $classFile.name for enhancement", e);
        } catch (IllegalClassFormatException e) {
            throw new EbeanEnhancementException("Unable to parse ф class file $classFile.name while enhance", e);
        }
    }

    private static List<File> collectClassFiles(File dir) {
        List<File> classFiles = new ArrayList<>();

        dir.listFiles().each { file ->
            if (file.directory) {
                classFiles.addAll(collectClassFiles(file));
            } else {
                if (file.name.endsWith(".class")) {
                    classFiles.add(file);
                }
            }
        }

        classFiles
    }
}

class EbeanEnhancementException extends RuntimeException {
    EbeanEnhancementException(String message, Throwable cause) {
        super(message, cause);
    }
}
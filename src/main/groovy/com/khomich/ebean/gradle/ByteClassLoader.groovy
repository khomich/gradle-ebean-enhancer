package com.khomich.ebean.gradle

class ByteClassLoader extends ClassLoader {

    ByteClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        parent.findClass(name);
    }

    byte[] getClassBytes(String className) {
        try {
            Class<?> clazz = parent.findLoadedClass(className)
            if (null == clazz) {
                clazz = findClass(className)
            }
            def classAsPath = className.replace('.', '/') + ".class";
            parent.getResourceAsStream(classAsPath).getBytes()
        } catch (ClassNotFoundException e) {
            return null
        } catch (IOException e) {
            throw new EbeanEnhancementException("Unable to load referenced class $className", e)
        }
    }

}

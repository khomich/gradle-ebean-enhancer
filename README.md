gradle-ebean-enhancer
==============

Implementation of gradle plugin for [Ebean](https://github.com/ebean-orm/avaje-ebeanorm) 4.x. Version of agent enhancer library - 4.1.5.

Plugin supports Java, Groovy and Scala.

Also you may be interested in an updated IntelliJ Idea [plugin](https://github.com/khomich/idea-ebean-enhancer ) with the latest agent version and subclass enhancement fixes.

how to use
==========
At this stage you have to download project and apply **publishToMavenLocal** task. After alter you *build.gradle* by adding *mavenLocal()* as the repository to the *buildscript {repositories {}}* section and refer plugin with *classpath "com.khomich:gradle-ebean-enhancer:$version"* in dependency block. See *usage.gradle* for full sample. 


# FoodObfuscator
An APK/DEX post-processing obfuscator, using the [Soot Framework](https://github.com/Sable/soot)

## Usage
Very easy to use, hooks onto any non-DSL task.
```
gradle clean obfuscate
```

## Install

With package structure being
```
-/
--AndroidAppRoot/
--FoodObfuscator/
```

Add this line to your dependencies in your top-level Android app wrapper gradle
```groovy
maven {
    url uri("../repo")
}
```

For each individual android app build.gradle

Minimal setup:
```groovy
// Top line of your individual android app build.gradle
apply plugin: 'nz.ac.auckland.foodobfuscator'
...
// I want to obfuscate the app-debug apk and output to the same file through use of a temporary file
obfuscate {
    inputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    outputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    useTempFile true //default
    dependsOn assemble
}
```

Slightly more advanced setup with manual platforms folder and using optional rt.jar from $JAVA_HOME, also toggling temporary files off.
```groovy
apply plugin: 'nz.ac.auckland.foodobfuscator'
...
// I want to do the same as above, but with more classpath resolution (and don't care how slow it is)
obfuscate {
    inputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    outputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    androidHomePlatformsFolder = new File("/usr/local/share/android-sdk/platforms/")
    useTempFile false
    useRTJar true
    dependsOn assemble
}
```
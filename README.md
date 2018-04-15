# FoodObfuscator
An APK/DEX post-processing obfuscator

## Instructions
Very easy to use, hooks onto any non-DSL task.

Minimal setup:
```
obfuscate {
    inputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    outputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    dependsOn assemble
}
```

Slightly more advanced setup with manual platforms folder and using optional rt.jar from $JAVA_HOME, also toggling temporary files off.

```
obfuscate {
    inputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    outputFile = new File(project.buildDir.getAbsolutePath()+"/outputs/apk/debug/app-debug.apk")
    androidHomePlatformsFolder = new File("/usr/local/share/android-sdk/platforms/")
    useTempFile false
    useRTJar true
    dependsOn assemble
}
```
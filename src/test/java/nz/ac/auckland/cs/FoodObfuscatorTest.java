package nz.ac.auckland.cs;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

public class FoodObfuscatorTest {

    @Test
    public void resolveCorrectlyForJavaHome() {
        assumeTrue(System.getenv().containsKey("JAVA_HOME"));
        assertEquals(FoodObfuscator.resolveEnv("JAVA_HOME"), System.getenv("JAVA_HOME"));
    }

    @Test
    public void resolveCorrectlyForHome() {
        assumeTrue(System.getenv().containsKey("HOME"));
        assertEquals(FoodObfuscator.resolveEnv("HOME"), System.getenv("HOME"));
    }

    @Test
    public void findRuntimeJar() {
        File systemJar = FoodObfuscator.findSystemRuntimeJar();
        assumeNotNull(systemJar);
        assertTrue(systemJar.exists());
    }

    @Test
    public void findNewestAndroidJar() {
        File systemJar = FoodObfuscator.findSystemAndroidJar();
        assumeNotNull(systemJar);
        assertTrue(systemJar.exists());

        File[] androidJarDirectories = new File(FoodObfuscator.resolveEnv("ANDROID_HOME", "ANDROID_SDK_ROOT") + "/platforms/")
                .listFiles(pathname -> pathname.isDirectory() && pathname.getName().contains("android-"));
        assertTrue(androidJarDirectories != null && androidJarDirectories.length > 0);

        Optional<File> jar = Arrays.stream(androidJarDirectories)
                // get the newest version android platform jar
                .max((a, b) -> {
                    int int1,int2;
                    try {
                        int1 = Integer.parseInt(a.getName().replace("android-",""));
                    } catch (NumberFormatException nfe) {
                        return -1;
                    }
                    try {
                        int2 = Integer.parseInt(b.getName().replace("android-",""));
                    } catch (NumberFormatException nfe) {
                        return 1;
                    }
                    return int1 - int2;
                })
                .map(x -> new File(x.getAbsolutePath()+"/android.jar"))
                .filter(File::exists);
        assertTrue(jar.isPresent());
    }
}

package nz.ac.auckland.cs;

import org.junit.Test;

import java.io.File;

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
    public void findNewestAndroidJarDirectory() {
        File homePlatformsFolder = FoodObfuscator.findSystemAndroidHomePlatformsFolder();
        assumeNotNull(homePlatformsFolder);
        assertTrue(homePlatformsFolder.exists());
    }
}

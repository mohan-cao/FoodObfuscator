package nz.ac.auckland.cs;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FoodObfuscatorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().create("obfuscate", FoodObfuscator.class);
    }
}

package nz.ac.auckland.cs;

import com.google.common.io.Files;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.*;
import soot.*;
import soot.jbco.bafTransformations.*;
import soot.jbco.jimpleTransformations.CollectJimpleLocals;
import soot.jbco.jimpleTransformations.FieldRenamer;
import soot.options.Options;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class FoodObfuscator extends DefaultTask {
    private File inputFile;
    private File outputFile;
    @Optional
    private int androidVersion;
    @Optional
    private File androidJar;
    @Optional
    private Boolean useTempFile = true;
    @Optional
    private Boolean useRTJar = false;

    @Input
    public Boolean getUseTempFile() {
        return useTempFile;
    }

    public void setUseTempFile(Boolean useTempFile) {
        this.useTempFile = useTempFile;
    }

    @Input
    public Boolean getUseRTJar() {
        return useRTJar;
    }

    public void setUseRTJar(Boolean useRTJar) {
        this.useRTJar = useRTJar;
    }

    @Input
    public int getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(int androidVersion) {
        this.androidVersion = androidVersion;
    }

    @Input
    public File getAndroidJar() {
        return androidJar;
    }

    public void setAndroidJar(File androidJar) {
        this.androidJar = androidJar;
    }

    @InputFile
    public File getInputFile() {
        return this.inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    @OutputFile
    public File getOutputFile() {
        return this.outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    @TaskAction
    public void obfuscate() {
        G.reset();

        File outputDir = this.outputFile.getParentFile();
        if (useTempFile) outputDir = Files.createTempDir();

        File androidJar = findSystemAndroidJar();
        File javaRTJar = findSystemRuntimeJar();

        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_whole_program(true);
        Options.v().set_android_jars(
                (this.androidJar != null) ? this.androidJar.getParentFile().getAbsolutePath() : Objects.requireNonNull(androidJar).getParentFile().getAbsolutePath()
        );
        if (javaRTJar != null && useRTJar) Options.v().set_soot_classpath(javaRTJar.getAbsolutePath()+":"+Scene.v().defaultClassPath());
        Options.v().set_process_dir(Collections.singletonList(inputFile.getAbsolutePath()));
        Options.v().set_process_multiple_dex(true);
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_output_dir(outputDir.getAbsolutePath());
        Options.v().set_prepend_classpath(true);
        Options.v().set_force_overwrite(true);
        Options.v().set_allow_phantom_refs(true);
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
        Scene.v().loadNecessaryClasses();
        Pack whole_java_transform_pack = PackManager.v().getPack("wjtp");
        Pack java_transform_pack = PackManager.v().getPack("jtp");
        Pack baf_body_creation_package = PackManager.v().getPack("bb");
        FieldRenamer.rename_fields = true;
        SootClass booleanSootClass = BooleanType.v().boxedType().getSootClass();
        if (!booleanSootClass.declaresMethod("void <init>(boolean)")) booleanSootClass.addMethod(new SootMethod("<init>", Collections.singletonList(BooleanType.v()), VoidType.v(), Modifier.PUBLIC, Collections.emptyList()));
        java_transform_pack.add(new Transform("jtp.jbco_jl", new CollectJimpleLocals()));
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_j2bl", new Jimple2BafLocalBuilder()), "bb.lso");
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_ful", new FixUndefinedLocals()), "bb.lso");
        baf_body_creation_package.add(new Transform("bb.jbco_rrps", new RemoveRedundantPushStores()));
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_plvb", new LocalsToBitField()), "bb.jbco_ful");
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_iii", new IndirectIfJumpsToCaughtGotos()), "bb.jbco_ful");
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_ptss", new WrapSwitchesInTrys()), "bb.jbco_ful");
        PackManager.v().runPacks();
        if (!Options.v().oaat()) PackManager.v().writeOutput();

        if (useTempFile) Arrays.stream(Objects.requireNonNull(outputDir.listFiles())).forEach(file -> {
            try {
                Files.move(file, this.outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Deprecated
    public static void main(String[] args) {
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_whole_program(true);
        Options.v().set_android_jars("./android-platforms");
        Options.v().set_process_dir(Collections.singletonList("../FindFood/app/build/outputs/apk/debug/app-debug.apk"));
        Options.v().set_process_multiple_dex(true);
        //Options.v().set_soot_classpath("/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/rt.jar:"+Scene.v().defaultClassPath());
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_output_dir("./sootOutputClass");
        Options.v().set_prepend_classpath(true);
        Options.v().set_force_overwrite(true);
        Options.v().set_allow_phantom_refs(true);
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
        Scene.v().loadNecessaryClasses();

        Pack whole_java_transform_pack = PackManager.v().getPack("wjtp");
        Pack java_transform_pack = PackManager.v().getPack("jtp");
        Pack baf_body_creation_package = PackManager.v().getPack("bb");

        //java_transform_pack.add(new Transform("jtp.debugTransformer", new DebugTransform()));

        FieldRenamer.rename_fields = true;
        SootClass booleanSootClass = BooleanType.v().boxedType().getSootClass();
        if (!booleanSootClass.declaresMethod("void <init>(boolean)")) booleanSootClass.addMethod(new SootMethod("<init>", Collections.singletonList(BooleanType.v()), VoidType.v(), Modifier.PUBLIC, Collections.emptyList()));

        //whole_java_transform_pack.add(new Transform("wjtp.jbco_fr", new FieldRenamer()));
        java_transform_pack.add(new Transform("jtp.jbco_jl", new CollectJimpleLocals()));
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_j2bl", new Jimple2BafLocalBuilder()), "bb.lso");
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_ful", new FixUndefinedLocals()), "bb.lso");
        baf_body_creation_package.add(new Transform("bb.jbco_rrps", new RemoveRedundantPushStores()));

        // if wjtp then insert before jtp.jbco.jl
        // if bb then insert before bb.jbco_ful
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_plvb", new LocalsToBitField()), "bb.jbco_ful");
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_iii", new IndirectIfJumpsToCaughtGotos()), "bb.jbco_ful");
        baf_body_creation_package.insertBefore(new Transform("bb.jbco_ptss", new WrapSwitchesInTrys()), "bb.jbco_ful");

        PackManager.v().runPacks();
        if (!Options.v().oaat()) PackManager.v().writeOutput();
    }

    protected static File findSystemAndroidJar() {

        //resolve system android-platforms location
        File androidHome = new File(resolveEnv("ANDROID_HOME", "ANDROID_SDK_ROOT") + "/platforms/");

        File[] androidJarDirectories = androidHome.listFiles(pathname -> pathname.isDirectory() && pathname.getName().contains("android-"));

        return Arrays.stream(Objects.requireNonNull(androidJarDirectories))
                .max((o1, o2) -> {
                    int int1,int2;
                    try {
                        int1 = Integer.parseInt(o1.getName().replace("android-",""));
                    } catch (NumberFormatException nfe) {
                        return -1;
                    }
                    try {
                        int2 = Integer.parseInt(o2.getName().replace("android-",""));
                    } catch (NumberFormatException nfe) {
                        return 1;
                    }
                    return int1 - int2;
                })
                .map(x -> new File(x.getAbsolutePath()+"/android.jar"))
                .filter(File::exists).orElse(null);

    }

    protected static File findSystemRuntimeJar() {
        File jar = new File(resolveEnv("JAVA_HOME")+"/jre/lib/rt.jar");
        if(jar.exists()) return jar;
        return null;
    }

    protected static String resolveEnv(String... vars) {
        String property = null;
        for (int i = 0; i < vars.length && property == null; i++) {
            if (!System.getenv().containsKey(vars[i])) continue;
            property = System.getenv(vars[i]);
        }
        return property;
    }
}

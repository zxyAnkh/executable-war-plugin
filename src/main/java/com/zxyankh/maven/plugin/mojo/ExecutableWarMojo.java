package com.zxyankh.maven.plugin.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * 可执行war包所需
 * Created by zxy on 6/17/2017.
 */
@Mojo(name = "exec", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class ExecutableWarMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.packaging}", required = true, readonly = true)
    private String packaging;

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private File outputDir;

    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true, readonly = true)
    private File sourceDir;

    @Parameter(defaultValue = "${project.build.finalName}", required = true, readonly = true)
    private String finalName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if ("war".equals(packaging)) {
            copy(sourceDir, new File(outputDir.getParent(), finalName));
            for (File file : sourceDir.getParentFile().listFiles()) {
                if ("resources".equals(file.getName())) {
                    copy(file, new File(outputDir.getParent(), finalName));
                    break;
                }
            }
        }
    }

    private void copy(File src, File dest) throws MojoFailureException {
        if (src == null || !src.exists()) {
            return;
        }
        if (!dest.exists()) {
            dest.mkdirs();
        }
        for (File file : src.listFiles()) {
            if (file.isDirectory()) {
                copy(file, new File(dest, file.getPath().replace(src.getPath(), "")));
            } else {
                try {
                    Files.copy(file.toPath(), new File(dest, file.getPath().replace(src.getPath(), "")).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new MojoFailureException(e.getMessage());
                }
            }
        }
    }
}

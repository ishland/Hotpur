package com.ishland.hotpur.gradle.hotpurclip;

import com.google.common.base.Preconditions;
import io.sigpipe.jbsdiff.DefaultDiffSettings;
import io.sigpipe.jbsdiff.Diff;
import io.sigpipe.jbsdiff.InvalidHeaderException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.logging.progress.ProgressLogger;
import org.gradle.internal.logging.progress.ProgressLoggerFactory;
import org.gradle.work.Incremental;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MakePatchesTask extends DefaultTask {

    @OutputFile
    private File output = ((Copy) getProject().getTasks().getByPath("processResources")).getDestinationDir().toPath().resolve("hotpur.patch").toFile();

    @InputFile
    @Incremental
    public File originalJar = null;
    @InputFile
    @Incremental
    public File targetJar = null;

    public File getOriginalJar() {
        return originalJar;
    }

    public void setOriginalJar(File originalJar) {
        this.originalJar = originalJar;
    }

    public File getTargetJar() {
        return targetJar;
    }

    public void setTargetJar(File targetJar) {
        this.targetJar = targetJar;
    }

    public File getOutput() {
        return output;
    }

    private ProgressLoggerFactory getProgressLoggerFactory() {
        return ((ProjectInternal) getProject()).getServices().get(ProgressLoggerFactory.class);
    }

    @TaskAction
    public void genPatches() throws IOException, CompressorException, InvalidHeaderException {
        Preconditions.checkNotNull(originalJar);
        Preconditions.checkNotNull(targetJar);

        final ProgressLogger genPatches = getProgressLoggerFactory().newOperation(getClass()).setDescription("Generate patches");
        genPatches.started();

        genPatches.progress("Cleanup");
        output.delete();

        genPatches.progress("Reading jar files into memory");
        byte[] origin = Files.readAllBytes(originalJar.toPath());
        byte[] target = Files.readAllBytes(targetJar.toPath());

        genPatches.progress("Generating patch");
        try(final OutputStream out = new BufferedOutputStream(new FileOutputStream(output))){
            Diff.diff(origin, target, out, new DefaultDiffSettings(CompressorStreamFactory.XZ));
        }

        genPatches.completed();

    }

    public static String toHex(final byte[] hash) {
        final StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte aHash : hash) {
            sb.append(String.format("%02X", aHash & 0xFF));
        }
        return sb.toString();
    }

}

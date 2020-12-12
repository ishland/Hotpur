package com.ishland.hotpur.gradle.gitdescribe;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.palantir.gradle.gitversion.VersionDetails;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GitDescribe {

    private static final Class<?> gitVersionArgsClass;
    private static final Constructor<?> gitVersionArgsConstructor;
    private static final Class<?> versionDetailsImplClass;
    private static final Constructor<?> versionDetailsImplConstructor;
    private static final Method versionDetailsImplIsCleanMethod;

    static {
        try {
            gitVersionArgsClass = Class.forName("com.palantir.gradle.gitversion.GitVersionArgs");
            gitVersionArgsConstructor = gitVersionArgsClass.getDeclaredConstructor();
            gitVersionArgsConstructor.setAccessible(true);
            versionDetailsImplClass = Class.forName("com.palantir.gradle.gitversion.VersionDetailsImpl");
            versionDetailsImplConstructor = versionDetailsImplClass.getDeclaredConstructor(Git.class, gitVersionArgsClass);
            versionDetailsImplConstructor.setAccessible(true);
            versionDetailsImplIsCleanMethod = versionDetailsImplClass.getDeclaredMethod("isClean");
            versionDetailsImplIsCleanMethod.setAccessible(true);
        } catch (Throwable e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }

    public static VersionDetails getVersionDetails(File dir) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Preconditions.checkNotNull(dir);
        Git repo = getRepository(dir);
        final Object gitVersionArgs = gitVersionArgsConstructor.newInstance();
        return (VersionDetails) versionDetailsImplConstructor.newInstance(repo, gitVersionArgs);
    }

    public static boolean isDirty(VersionDetails versionDetails) throws InvocationTargetException, IllegalAccessException {
        return !((boolean) versionDetailsImplIsCleanMethod.invoke(versionDetails));
    }

    private static Git getRepository(File currentDir) throws IOException {
        try {
            return Git.open(currentDir);
        } catch (RepositoryNotFoundException e) {
            if(currentDir.getParentFile() != null)
                return getRepository(currentDir.getParentFile());
            return null;
        }
    }

}

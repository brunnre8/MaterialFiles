/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.provider.linux;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import java8.nio.file.FileStore;
import java8.nio.file.FileSystem;
import java8.nio.file.Path;
import java8.nio.file.PathMatcher;
import java8.nio.file.WatchService;
import java8.nio.file.spi.FileSystemProvider;
import me.zhanghai.android.files.util.SetBuilder;

class LinuxFileSystem extends FileSystem {

    static final char SEPARATOR = '/';

    private static final String SEPARATOR_STRING = Character.toString(SEPARATOR);

    private static final Set<String> SUPPORTED_FILE_ATTRIBUTE_VIEWS =
            SetBuilder.<String>newHashSet()
                    .add("basic")
                    // TODO
                    //.add()
                    .buildUnmodifiable();

    @NonNull
    private final LinuxPath mRootDirectory = new LinuxPath(this, "/");
    {
        if (!mRootDirectory.isAbsolute()) {
            throw new AssertionError("Root directory must be absolute");
        }
        if (mRootDirectory.getNameCount() != 0) {
            throw new AssertionError("Root directory must contain no names");
        }
    }

    @NonNull
    private final LinuxPath mDefaultDirectory;
    {
        String userDir = System.getenv("user.dir");
        if (userDir == null) {
            userDir = "/";
        }
        mDefaultDirectory = new LinuxPath(this, userDir);
        if (!mDefaultDirectory.isAbsolute()) {
            throw new AssertionError("Default directory must be absolute");
        }
    }

    @NonNull
    private final LinuxFileSystemProvider mProvider;

    LinuxFileSystem(@NonNull LinuxFileSystemProvider provider) {
        mProvider = provider;
    }

    @NonNull
    Path getRootDirectory() {
        return mRootDirectory;
    }

    @NonNull
    Path getDefaultDirectory() {
        return mDefaultDirectory;
    }

    @NonNull
    @Override
    public FileSystemProvider provider() {
        return mProvider;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @NonNull
    @Override
    public String getSeparator() {
        return SEPARATOR_STRING;
    }

    @NonNull
    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.singletonList(mRootDirectory);
    }

    @NonNull
    @Override
    public Iterable<FileStore> getFileStores() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Set<String> supportedFileAttributeViews() {
        return SUPPORTED_FILE_ATTRIBUTE_VIEWS;
    }

    @NonNull
    @Override
    public Path getPath(@NonNull String first, @NonNull String... more) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(more);
        StringBuilder pathBuilder = new StringBuilder(first);
        for (String name : more) {
            pathBuilder
                    .append(SEPARATOR)
                    .append(name);
        }
        String path = pathBuilder.toString();
        return new LinuxPath(this, path);
    }

    @NonNull
    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        Objects.requireNonNull(syntaxAndPattern);
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public LinuxUserPrincipalLookupService getUserPrincipalLookupService() {
        return LinuxUserPrincipalLookupService.getInstance();
    }

    @NonNull
    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException();
    }
}

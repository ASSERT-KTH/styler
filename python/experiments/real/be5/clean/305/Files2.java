package com.developmentontheedge.be5.metadata.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Files2
{

    private static class Copier implements FileVisitor<Path>
    {

        private final Path source;
        private final Path target;
        private final Predicate<Path> shouldCopy;

        public Copier(final Path source, final Path target)
        {
            this(source, target, path -> true);
        }

        public Copier(final Path source, final Path target, final Predicate<Path> copy)
        {
            this.source = source;
            this.target = target;
            this.shouldCopy = copy;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
        {
            final int baseNameCount = source.getNameCount();
            final int fileNameCount = file.getNameCount();
            final List<String> parts = new ArrayList<>();

            for (int i = baseNameCount; i < fileNameCount; i++)
                parts.add(file.getName(i).toString());

            if (!parts.isEmpty() && parts.get(parts.size() - 1).equals(".gitignore"))
                return FileVisitResult.CONTINUE;


            final Path targetFile = target.resolve(String.join("/", parts));

            if (shouldCopy.test(file))
            {
                Files.createDirectories(targetFile.getParent());
                Files.deleteIfExists(targetFile);
                Files.copy(file, targetFile);
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

    }

    private static class Collector implements FileVisitor<Path>
    {

        private final Path path;
        private final Predicate<String> select;
        private final List<String> relativePaths;

        public Collector(final Path path, final Predicate<String> select)
        {
            this.path = path;
            this.select = select;
            this.relativePaths = new ArrayList<>();
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
        {
            final int baseNameCount = path.getNameCount();
            final int fileNameCount = file.getNameCount();
            final List<String> parts = new ArrayList<>();

            for (int i = baseNameCount; i < fileNameCount; i++)
                parts.add(file.getName(i).toString());

            final String relativePath = String.join("/", parts);

            if (select.test(relativePath))
                relativePaths.add(relativePath);

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
        {
            return FileVisitResult.CONTINUE;
        }

        public List<String> getRelativePaths()
        {
            return relativePaths;
        }

    }

    /**
     * Not intended to be instantiated.
     */
    private Files2()
    {
    }

    public static void copyAll(final Path from, final Path to) throws IOException
    {
        Files.walkFileTree(from, new Copier(from, to));
    }

    public static void copyAll(final Path from, final Path to, final Predicate<Path> copy) throws IOException
    {
        Files.walkFileTree(from, new Copier(from, to, copy));
    }

    public static String[] collectRelativePaths(final Path path, final Predicate<String> select) throws IOException
    {
        final Collector collector = new Collector(path, select);
        Files.walkFileTree(path, collector);

        return collector.getRelativePaths().stream().toArray(String[]::new);
    }

    public static Predicate<Path> byExtension(final String extension)
    {
        final String suffix = "." + extension;
        return input -> input.getFileName().toString().endsWith(suffix);
    }

    public static boolean contentEq(final Path path, final String content)
    {
        try
        {
            return Arrays.equals(content.getBytes(StandardCharsets.UTF_8), Files.readAllBytes(path));
        }
        catch (IOException e)
        {
            return false;
        }
    }
}

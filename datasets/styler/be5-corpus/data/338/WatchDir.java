package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.model.Project;
import one.util.streamex.StreamEx;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class WatchDir
{
    private static final Logger log = Logger.getLogger(WatchDir.class.getName());

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private volatile boolean stopped = false;
    private Consumer<Path> onModify = path -> {
    };
    // need no weak links as we work with limited amount of files
    private final Map<Path, Long> lastModifiedByPath = new HashMap<>();

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event)
    {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException
    {
        //System.out.println("Registering watcher on "+dir);
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException
    {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                // ignore some folders
                if (!dir.getFileName().toString().equals(".git"))
                {
                    register(dir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                lastModifiedByPath.put(file, file.toFile().lastModified());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerAll(final ProjectFileSystem fs) throws IOException
    {
        Path start = fs.getRoot();
        NavigableMap<Path, Boolean> map = fs.getPaths();
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                Path parent = StreamEx.iterate(dir, Path::getParent).takeWhile(Objects::nonNull)
                        .findFirst(map::containsKey).orElse(null);
                boolean hasChildren = StreamEx.ofKeys(map).anyMatch(path -> path.startsWith(dir));
                if (parent == null || !map.get(parent) && !parent.equals(dir))
                    return hasChildren ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                register(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                lastModifiedByPath.put(file, file.toFile().lastModified());
                return FileVisitResult.CONTINUE;
            }
        });
    }

//    /**
//     * Creates a WatchService and registers the given directory
//     */
//    public WatchDir(Path dir) throws IOException {
//        this.watcher = FileSystems.getDefault().newWatchService();
//        this.keys = new HashMap<>();
//        this.recursive = true;
//
//        if (recursive) {
//            registerAll(dir);
//        } else {
//            register(dir);
//        }
//    }
//
//    /**
//     * Creates a WatchService and registers the given Project
//     */
//    public WatchDir(Project project) throws IOException {
//        this.watcher = FileSystems.getDefault().newWatchService();
//        this.keys = new HashMap<>();
//        this.recursive = true;
//
//        registerAll(new ProjectFileSystem( project ));
//    }

    public WatchDir(Map<String, Project> modulesMap) throws IOException
    {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = true;

        List<String> watchProject = new ArrayList<>();
        for (Map.Entry<String, Project> entry : modulesMap.entrySet())
        {
            ProjectFileSystem projectFileSystem = new ProjectFileSystem(entry.getValue());

            if (projectFileSystem.getRoot().toString().length() > 3 &&
                    Files.exists(projectFileSystem.getRoot()))
            {
                watchProject.add(entry.getKey());
                registerAll(projectFileSystem);
            }
        }
        log.info("Watch projects: " + watchProject.stream().collect(Collectors.joining(", ")));
    }

    public WatchDir onModify(Consumer<Path> onModify)
    {
        this.onModify = onModify;
        return this;
    }

    public WatchDir start()
    {
        new Thread(this::processEvents, "Watch service for BeanExplorer project").start();
        return this;
    }

    public void stop()
    {
        this.stopped = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    private void processEvents()
    {
        while (!stopped)
        {
            // wait for key to be signalled
            WatchKey key;
            try
            {
                key = watcher.take();
            }
            catch (InterruptedException x)
            {
                return;
            }

            if (stopped)
                return;

            Path dir = keys.get(key);
            if (dir == null)
            {
                // WatchKey not recognized
                continue;
            }

            final List<WatchEvent<?>> events = key.pollEvents();
            for (WatchEvent<?> event : events)
            {
                if (stopped)
                {
                    return;
                }

                WatchEvent.Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW)
                {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // handle
                if (kind == ENTRY_MODIFY)
                {
                    // skip timestamp modification
                    if (Files.isRegularFile(child))
                    {
                        Long previouslyModified = lastModifiedByPath.get(child);
                        long lastModified = child.toFile().lastModified();
                        if (previouslyModified != null && (lastModified - previouslyModified) > 100)
                        {
                            lastModifiedByPath.put(child, lastModified);
                            continue;
                        }
                        lastModifiedByPath.put(child, lastModified);
                    }
                    onModify.accept(child);
                }

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE))
                {
                    try
                    {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS))
                        {
                            // TODO: register only interesting new directories
                            registerAll(child);
                        }
                    }
                    catch (IOException x)
                    {
                        // ignore to keep sample readable
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid)
            {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty())
                {
                    break;
                }
            }
        }
    }
}

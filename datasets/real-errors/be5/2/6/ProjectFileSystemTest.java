package com.developmentontheedge.be5.metadata.serialization;

import com.developmentontheedge.be5.metadata.model.Project;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ProjectFileSystemTest
{
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testPathsMap() throws IOException
    {
        Project prj = new Project("test");
        Path root = tmp.newFolder().toPath();
        prj.setLocation(root);
        ProjectFileSystem pfs = new ProjectFileSystem(prj);
        Map<Path, Boolean> map = EntryStream.of(pfs.getPaths()).mapKeys(root::relativize).toSortedMap();
        assertTrue(StreamEx
                .of("", "src", "src/js/extenders", "src/js/forms", "src/js/operations", "src/js/queries", "src/l10n", "src/meta/data",
                        "src/meta/entities").map(Paths::get).noneMatch(map::get));
        assertTrue(StreamEx.of("src/ftl", "src/include", "src/meta/modules", "src/groovy/operations", "src/pages").map(Paths::get).allMatch(map::get));
    }
}

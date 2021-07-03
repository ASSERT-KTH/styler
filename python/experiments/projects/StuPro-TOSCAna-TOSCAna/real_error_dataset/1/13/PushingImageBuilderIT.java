package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.kubernetes.docker.DockerTestUtils;
import org.opentosca.toscana.plugins.kubernetes.docker.util.DockerRegistryCredentials;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.auth.FixedRegistryAuthSupplier;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerMount;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;
import org.junit.After;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonMap;

public class PushingImageBuilderIT extends ExportingImageBuilderIT {

    private static Logger logger = LoggerFactory.getLogger(PushingImageBuilderIT.class);

    private DockerRegistryCredentials creds;

    private String tag = "sha-test-" + Long.toString(System.currentTimeMillis(), 16);

    private String registryId;

    @Override
    public void init() throws Exception {
        Assume.assumeTrue(DockerTestUtils.isDockerAvailable());

        this.creds = new DockerRegistryCredentials(
            "127.0.0.1:5000",
            "",
            "",
            "testing"
        );

        DockerClient client = DefaultDockerClient.fromEnv().build();
        logger.info("Downloading registry image");
        client.pull("registry:2");

        final Map<String, List<PortBinding>> ports = singletonMap(
            "5000/tcp", Collections.singletonList(PortBinding.of("0.0.0.0", 5000)));
        final HostConfig hostConfig = HostConfig.builder().portBindings(ports)
            .build();

        logger.info("Creating Local Registry Container");
        ContainerConfig config = ContainerConfig.builder()
            .hostConfig(hostConfig)
            .image("registry:2").build();
        String id = client.createContainer(config).id();
        logger.info("Registry container id: {}", id);

        logger.info("Starting registry container");
        client.startContainer(id);
        this.registryId = id;
    }

    @Override
    public ImageBuilder instantiateImageBuilder(TransformationContext context) throws Exception {
        return new PushingImageBuilder(
            creds,
            //Make the Image tag kind of unique to prevent collisions when pushing to a proper registry
            tag,
            WORKING_DIR_SUBFOLDER_NAME,
            context
        );
    }

    @Override
    public void validate(String tag) throws Exception {
        RegistryAuth auth = creds.toRegistryAuth();

        DockerClient client = DefaultDockerClient.fromEnv()
            .registryAuthSupplier(
                new FixedRegistryAuthSupplier(
                    auth,
                    RegistryConfigs.create(
                        Collections.singletonMap(creds.getRegistryURL(), auth)
                    )
                )
            )
            .build();
        client.removeImage(tag);
        //Pull the image from the registry
        client.pull(tag);
    }

    
    @After
    @SuppressWarnings("Duplicates")
    public void tearDown() throws Exception {
        super.tearDown();
        DockerClient client = DefaultDockerClient.fromEnv().build();
        logger.info("Stopping and removing registry");
        List<ContainerMount> mounts = client.inspectContainer(this.registryId).mounts();
        client.killContainer(this.registryId);
        client.removeContainer(this.registryId);
        mounts.forEach(e -> {
            try {
                client.removeVolume(e.name());
            } catch (DockerException | InterruptedException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        });
    }
}

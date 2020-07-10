package org.opentosca.toscana.plugins.kubernetes.visitor.imgtransform;

import java.util.Optional;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

public class ImageMappingVisitor implements NodeVisitor {

    private static final String NO_CREATE_ERROR_MESSAGE = "Cannot create DockerApplication without a create artifact containing the image";
    private static final String NO_IMAGE_PATH_ERROR_MESSAGE = "The Given Create Artifact for the docker application '%s' does not have a image path";
    private final BaseImageMapper mapper;

    private String baseImage = null;
    private boolean hasInstallScripts = false;
    private boolean requiresBuilding = true;

    public ImageMappingVisitor(BaseImageMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void visit(Compute node) {
        baseImage = mapper.mapToBaseImage(node.getOs());
        hasInstallScripts = hasCreateScript(node) || hasConfigureScript(node);
    }

    private boolean hasCreateScript(RootNode node) {
        return node.getStandardLifecycle().getCreate().isPresent() && node.getStandardLifecycle().getCreate().get().getArtifact().isPresent();
    }

    private boolean hasConfigureScript(RootNode node) {
        return node.getStandardLifecycle().getConfigure().isPresent();
    }

    @Override
    public void visit(Apache node) {
        if (!hasInstallScripts) {
            if (!hasCreateScript(node)) {
                baseImage = "library/php:apache";
            }
        }
    }

    @Override
    public void visit(MysqlDbms node) {
        if (!hasInstallScripts) {
            if (!hasCreateScript(node)) {
                baseImage = "library/mysql:latest";
            }
        }
    }

    @Override
    public void visit(Nodejs node) {
        if (!hasInstallScripts) {
            if (!hasCreateScript(node)) {
                baseImage = "library/node:latest";
            }
        }
    }

    @Override
    public void visit(JavaRuntime node) {
        if (!hasInstallScripts) {
            if (!hasCreateScript(node)) {
                //Use OpenJDK JRE version 8
                baseImage = "library/openjdk:8";
            }
        }
    }

    @Override
    public void visit(DockerApplication node) {
        this.requiresBuilding = false;
        this.baseImage = node.getStandardLifecycle().getCreate()
            .orElseThrow(() -> new TransformationFailureException(NO_CREATE_ERROR_MESSAGE))
            .getArtifact().orElseThrow(() -> new TransformationFailureException(String.format(NO_IMAGE_PATH_ERROR_MESSAGE,
                node.getEntityName())))
            .getFilePath();

        if (this.baseImage == null) {
            throw new TransformationFailureException(String.format(NO_IMAGE_PATH_ERROR_MESSAGE, node.getEntityName()));
        }

        //TODO implement check if the docker application has children.
    }
    

    @Override
    public void visit(WebApplication node) {
        // Parent has to be visited to determine image
    }

    @Override
    public void visit(MysqlDatabase node) {
        // Parent has to be visited to determine image
    }

    @Override
    public void visit(JavaApplication node) {
        // Parent has to be visited to determine image
    }

    public boolean containerRequiresBuilding() {
        return requiresBuilding;
    }

    public Optional<String> getBaseImage() {
        return Optional.ofNullable(baseImage);
    }
}

package org.opentosca.toscana.model;

import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.ConnectsTo;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.relation.RoutesTo;
import org.opentosca.toscana.model.visitor.RelationshipVisitor;

public class RelationshipConstructor implements RelationshipVisitor {
    
    private final Requirement requirement;
    
    public RelationshipConstructor(Requirement requirement){
        this.requirement = requirement;
    }
    @Override
    public void visit(RootRelationship relation) {

    }

    @Override
    public void visit(AttachesTo relation) {

    }

    @Override
    public void visit(ConnectsTo relation) {

    }

    @Override
    public void visit(DependsOn relation) {

    }

    @Override
    public void visit(HostedOn relation) {

    }

    @Override
    public void visit(RoutesTo relation) {

    }
}

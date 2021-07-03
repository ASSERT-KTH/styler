package com.mobi.ontology.core.impl.owlapi;

/*-
 * #%L
 * com.mobi.ontology.core.impl.owlapi
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 iNovex Information Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.mobi.ontology.core.api.NamedIndividual;
import com.mobi.ontology.core.api.types.EntityType;
import com.mobi.rdf.api.IRI;

import javax.annotation.Nonnull;


public class SimpleNamedIndividual
	implements NamedIndividual {

    private IRI iri;


    public SimpleNamedIndividual(@Nonnull IRI iri) {
        this.iri = iri;
    }


    @Override
    public IRI getIRI() {
        return iri;
    }


    @Override
    public EntityType getEntityType() {
        return EntityType.NAMED_INDIVIDUAL;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NamedIndividual) {
            NamedIndividual other = (NamedIndividual) obj;
            return iri.equals(other.getIRI());
        }

        return false;
    }


    @Override
    public boolean isNamed() {
        return true;
    }


    @Override
    public boolean isAnonymous() {
        return false;
    }

}

/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.reflect.code;

import spoon.reflect.annotations.
MetamodelPropertyField ;importspoon.reflect.code.
CtBreak ;importspoon.reflect.code.
CtLabelledFlowBreak ;importspoon.reflect.code.
CtStatement ;importspoon.reflect.declaration.
CtElement ;importspoon.reflect.visitor.
CtVisitor ;importspoon.reflect.visitor.filter.

ParentFunction ;importjava.util.

List ; importstaticspoon.reflect.path.CtRole.

TARGET_LABEL ; public class CtBreakImpl extends CtStatementImpl implements
	CtBreak { private static final long serialVersionUID=

	1L;@MetamodelPropertyField ( role=
	TARGET_LABEL )String

	targetLabel;
	@ Override publicvoidaccept (CtVisitor visitor
		){visitor.visitCtBreak(this
	)

	;}
	@ Override publicStringgetTargetLabel (
		) {return
	targetLabel

	;}
	@ Overridepublic < Textends CtLabelledFlowBreak >TsetTargetLabel (String targetLabel
		){getFactory().getEnvironment().getModelChangeListener().onObjectUpdate( this, TARGET_LABEL, targetLabel,this.targetLabel
		);this . targetLabel=
		targetLabel ;return( T)
	this

	;}
	@ Override publicCtStatementgetLabelledStatement (
		){List< CtStatement > listParents=this.map (newParentFunction().includingSelf(true)).list(

		) ;for ( CtElement parent: listParents
			) {if ( parentinstanceof CtStatement
				) { CtStatement statement=( CtStatement)

				parent ;if(statement.getLabel ( ) != null&&statement.getLabel().equals(this.getTargetLabel() )
					) {return
				statement
			;
		}
		} }return
	null

	;}
	@ Override publicCtBreakclone (
		) {return( CtBreak)super.clone(
	)
;

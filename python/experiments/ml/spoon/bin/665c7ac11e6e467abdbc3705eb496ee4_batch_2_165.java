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

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.CtVisitor;

import static spoon.reflect.path.CtRole.CONDITION;
import static spoon.reflect.path.CtRole.ELSE;
import static spoon.reflect.path.CtRole.THEN;

public class CtIfImpl extends CtStatementImpl implements CtIf
{ private static final long serialVersionUID =1L

;@MetamodelPropertyField( role =CONDITION
)CtExpression<Boolean >condition

;@MetamodelPropertyField( role =ELSE
) CtStatementelseStatement

;@MetamodelPropertyField( role =THEN
) CtStatementthenStatement

;@
Override public voidaccept( CtVisitorvisitor )
{visitor.visitCtIf(this)
;

}@
Override publicCtExpression<Boolean >getCondition( )
{ returncondition
;

}@SuppressWarnings("unchecked"
)@
Override public< S extendsCtStatement > SgetElseStatement( )
{ return(S )elseStatement
;

}@SuppressWarnings("unchecked"
)@
Override public< S extendsCtStatement > SgetThenStatement( )
{ return(S )thenStatement
;

}@
Override public< T extendsCtIf > TsetCondition(CtExpression<Boolean >condition )
{ if( condition !=null )
	{condition.setParent(this)
;
}getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this ,CONDITION ,condition ,this.condition)
;this. condition =condition
; return(T )this
;

}@
Override public< T extendsCtIf > TsetElseStatement( CtStatementelseStatement )
{ if( elseStatement !=null )
	{elseStatement.setParent(this)
;
}getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this ,ELSE ,elseStatement ,this.elseStatement)
;this. elseStatement =elseStatement
; return(T )this
;

}@
Override public< T extendsCtIf > TsetThenStatement( CtStatementthenStatement )
{
// then branch might be null: `if (condition) ;` if( thenStatement !=null )
	{thenStatement.setParent(this)
;
}getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this ,THEN ,thenStatement ,this.thenStatement)
;this. thenStatement =thenStatement
; return(T )this
;

}@
Override public CtIfclone( )
{ return(CtIf )super.clone()
;

}@
Override public VoidS( )
{ returnnull
;

} public CtCodeElementgetSubstitution(CtType<? >targetType )
{ returnclone()
;
}

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
package spoon.support.reflect.cu.

position ;importspoon.reflect.cu.
CompilationUnit ;importspoon.reflect.cu.position.

BodyHolderSourcePosition ;importjava.io.

Serializable
; /**
 * This class represents the position of a Java program element in a source
 * file.
 */ public class BodyHolderSourcePositionImpl
		extends DeclarationSourcePositionImplimplements BodyHolderSourcePosition ,

	Serializable { private static final long serialVersionUID=
	1L ; privateint
	bodyStart ; privateint

	bodyEnd ;public
			BodyHolderSourcePositionImpl (CompilationUnit
			compilationUnit ,int sourceStart ,int
			sourceEnd ,int modifierSourceStart ,int
			modifierSourceEnd ,int declarationSourceStart ,int
			declarationSourceEnd ,int
			bodyStart ,int
			bodyEnd,int [] lineSeparatorPositions
		){super(
				compilationUnit, sourceStart,
				sourceEnd, modifierSourceStart,
				modifierSourceEnd, declarationSourceStart,
				declarationSourceEnd,lineSeparatorPositions
		);checkArgsAreAscending( declarationSourceStart, modifierSourceStart , modifierSourceEnd+ 1, sourceStart , sourceEnd+ 1, bodyStart , bodyEnd+ 1 , declarationSourceEnd+1
		);this . bodyStart=
		bodyStart;this . bodyEnd=
	bodyEnd

	;}
	@ Override publicintgetBodyStart (
		) {return
	bodyStart

	;}
	@ Override publicintgetBodyEnd (
		) {return
	bodyEnd

	;}
	@ Override publicStringgetSourceDetails (
		) {returnsuper.getSourceDetails
				( ) + "\nbody = "+getFragment(getBodyStart( ),getBodyEnd()
	)
;

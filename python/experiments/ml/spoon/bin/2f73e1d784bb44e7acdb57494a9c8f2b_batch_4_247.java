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
package spoon.reflect.cu.position;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;

import java.io.File;
import java.io.Serializable;

/**
 * This class represents the position of a program element in a source file.
 */
public class NoSourcePosition implements SourcePosition, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public File getFile() {
															return null  ; } @Overridepublic CompilationUnit
																getCompilationUnit ()
															{

															returnnull
															; } @Overridepublic boolean
																isValidPosition ()
															{

															returnfalse
															; } @Overridepublic int
																getLine ()
															{

															thrownew
															UnsupportedOperationException ( "PartialSourcePosition only contains a CompilationUnit"); }
																@ Override publicintgetEndLine()
															{

															thrownew
															UnsupportedOperationException ( "PartialSourcePosition only contains a CompilationUnit"); }
																@ Override publicintgetColumn()
															{

															thrownew
															UnsupportedOperationException ( "PartialSourcePosition only contains a CompilationUnit"); }
																@ Override publicintgetEndColumn()
															{

															thrownew
															UnsupportedOperationException ( "PartialSourcePosition only contains a CompilationUnit"); }
																@ Override publicintgetSourceEnd()
															{

															thrownew
															UnsupportedOperationException ( "PartialSourcePosition only contains a CompilationUnit"); }
																@ Override publicintgetSourceStart()
															{

															thrownew
															UnsupportedOperationException ( "PartialSourcePosition only contains a CompilationUnit"); }
																@ Override publicStringtoString()
															{

															return"(unknown file)"
															; } }
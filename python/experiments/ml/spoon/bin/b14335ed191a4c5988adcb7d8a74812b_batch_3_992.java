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
package spoon.reflect.factory;

import spoon.SpoonException;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTSnippetCompiler;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util

.
TreeMap ; /**
 * A factory to create some evaluation utilities on the Spoon metamodel.
 */ public class CompilationUnitFactory

	extends
	SubFactory {/**
	 * Creates the evaluation factory.
	 */public CompilationUnitFactory( Factory
		factory){super(
	factory

	) ; }privatetransientMap <String , CompilationUnit > cachedCompilationUnits=newTreeMap<>

	(
	) ;/**
	 * Gets the compilation unit map.
	 *
	 * @return a map (path -&gt; {@link CompilationUnit})
	 */publicMap <String ,CompilationUnit> getMap
		( ){
	return

	cachedCompilationUnits
	; } /**
	 * Creates a compilation unit with no associated files.
	 */publicCompilationUnit create
		( ){returnfactory.Core().createCompilationUnit
	(

	) ; }publicCompilationUnit getOrCreate( CtPackage
		ctPackage ){if(ctPackage.getPosition(). getCompilationUnit () !=
			null ){returnctPackage.getPosition().getCompilationUnit
		( ) ;

			} else{
			CtModule module;if(factory.getEnvironment(). getComplianceLevel () >
				8 ) {module=ctPackage.getParent(CtModule.
			class ) ;
				} else {module
			=
			null ; } Filefile=this.factory.getEnvironment().getOutputDestinationHandler().getOutputPath (module ,ctPackage,null).toFile
			( )
				; try { Stringpath=file.getCanonicalPath
				( ) ; CompilationUnitresult=this.getOrCreate(
				path);result.setDeclaredPackage(
				ctPackage);ctPackage.setPosition(this.factory.createPartialSourcePosition(result
				) );
			return result ;} catch( IOException
				e ) {thrownew SpoonException ("Cannot get path for file: "+file.getAbsolutePath (),
			e
		)
	;

	} } }publicCompilationUnit getOrCreate( CtType
		type ){ if (type ==
			null ){
		return
		null ;}if(type.getPosition(). getCompilationUnit () !=
			null ){returntype.getPosition().getCompilationUnit
		(

		) ;}if(type.isTopLevel (
			) ){
			CtModule module;if(type. getPackage ( ) !=null&&factory.getEnvironment(). getComplianceLevel () >
				8 ) {module=type.getPackage().getParent(CtModule.
			class ) ;
				} else {module
			=
			null ; } Filefile=this.factory.getEnvironment().getOutputDestinationHandler().getOutputPath (module,type.getPackage (),type).toFile
			( )
				; try { Stringpath=file.getCanonicalPath
				( ) ; CompilationUnitresult=this.getOrCreate(
				path);result.addDeclaredType(
				type);type.setPosition(this.factory.createPartialSourcePosition(result
				) );
			return result ;} catch( IOException
				e ) {thrownew SpoonException ("Cannot get path for file: "+file.getAbsolutePath (),
			e
		) ; }
			} else{returngetOrCreate(type.getTopLevelType(
		)
	)

	; } }publicCompilationUnit getOrCreate( CtModule
		module ){if(module.getPosition(). getCompilationUnit () !=
			null ){returnmodule.getPosition().getCompilationUnit
		( ) ;
			} else { Filefile=this.factory.getEnvironment().getOutputDestinationHandler().getOutputPath (module ,null,null).toFile
			( )
				; try { Stringpath=file.getCanonicalPath
				( ) ; CompilationUnitresult=this.getOrCreate(
				path);result.setDeclaredModule(
				module);module.setPosition(this.factory.createPartialSourcePosition(result
				) );
			return result ;} catch( IOException
				e ) {thrownew SpoonException ("Cannot get path for file: "+file.getAbsolutePath (),
			e
		)
	;

	}
	} } /**
	 * Creates or gets a compilation unit for a given file path.
	 */publicCompilationUnit getOrCreate( String
		filePath ) { CompilationUnitcu=cachedCompilationUnits.get(
		filePath ); if (cu ==
			null ){if(filePath.startsWith(JDTSnippetCompiler. SNIPPET_FILENAME_PREFIX
				) ) {cu=factory.Core().createCompilationUnit
				(
				);//put the virtual compilation unit of code snippet into cache too, so the JDTCommentBuilder can found itcachedCompilationUnits.put (filePath,
				cu );
			return
			cu ; }cu=factory.Core().createCompilationUnit

			( );if(!filePath.equals(VirtualFile. VIRTUAL_FILE_NAME
				)){cu. setFile(newFile(filePath
			)

			);}cachedCompilationUnits.put (filePath,
		cu
		) ;}
	return

	cu
	; } /**
	 * Removes compilation unit from the cache and returns it
	 * Used by JDTSnippetCompiler to remove processed snippet from the cache
	 * @param filePath
	 * @return a cached compilation unit or null
	 */publicCompilationUnit removeFromCache( String
		filePath ){returncachedCompilationUnits.remove(
	filePath

)

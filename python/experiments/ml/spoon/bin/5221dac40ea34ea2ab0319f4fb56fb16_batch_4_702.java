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
package spoon;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import spoon.decompiler.CFRDecompiler;
import spoon.decompiler.Decompiler;
import spoon.support.Experimental;
import spoon.support.compiler.SpoonPom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Experimental
public class JarLauncher extends Launcher {
	File pom;
	File jar;
	File decompiledRoot;
	File decompiledSrc;
	Decompiler decompiler;
	boolean decompile = false;

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 */
	public JarLauncher(String jarPath) {
		this(jarPath, null, (String) null);
	}

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath) {
		this(jarPath, decompiledSrcPath, (String) null);
	}

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 * @param pom path to pom associated with the jar to be analyzed
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath, String pom) {
		this(jarPath, decompiledSrcPath, pom, null);
	}

	/**
	 * JarLauncher basic constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 * @param decompiler Instance implementing {@link spoon.decompiler.Decompiler} to be used
	 */
	public JarLauncher(String jarPath, String decompiledSrcPath, Decompiler decompiler) {this(jarPath ,decompiledSrcPath ,null ,decompiler)
;

}
/**
	 * JarLauncher constructor. Uses the default Decompiler (CFR)
	 *
	 * @param jarPath path to the jar to be analyzed
	 * @param decompiledSrcPath path to directory where decompiled source will be output
	 * @param pom path to pom associated with the jar to be analyzed
	 * @param decompiler Instance implementing {@link spoon.decompiler.Decompiler} to be used
	 */ publicJarLauncher( StringjarPath , StringdecompiledSrcPath , Stringpom , Decompilerdecompiler )
	{this. decompiler =decompiler
	; if( decompiledSrcPath ==null )
		{ decompiledSrcPath =System.getProperty("java.io.tmpdir" ) +System.getProperty("file.separator" ) +"spoon-tmp"
		; decompile =true
	;
	}this. decompiledRoot = newFile(decompiledSrcPath)
	; if(decompiledRoot.exists( ) &&!decompiledRoot.canWrite() )
		{ throw newSpoonException( "Dir " +decompiledRoot.getPath( ) +" already exists and is not deletable.")
	; } else if(decompiledRoot.exists( ) &&decompile )
		{decompiledRoot.delete()
	;
	} if(!decompiledRoot.exists() )
		{decompiledRoot.mkdirs()
		; decompile =true
	;
	} decompiledSrc = newFile(decompiledRoot ,"src/main/java")
	; if(!decompiledSrc.exists() )
		{decompiledSrc.mkdirs()
		; decompile =true
	;

	} if( decompiler ==null )
		{this. decompiler =getDefaultDecompiler()
	;

	} jar = newFile(jarPath)
	; if(!jar.exists( ) ||!jar.isFile() )
		{ throw newSpoonException( "Jar " +jar.getPath( ) +" not found.")
	;

	}
	//We call the decompiler only if jar has changed since last decompilation. if(jar.lastModified( ) >decompiledSrc.lastModified() )
		{ decompile =true
	;
	}init(pom)
;

} private voidinit( StringpomPath )
	{
	//We call the decompiler only if jar has changed since last decompilation. if(decompile )
		{decompiler.decompile(jar.getAbsolutePath())
	;

	} if( pomPath !=null )
		{ File srcPom  = newFile(pomPath)
		; if(!srcPom.exists( ) ||!srcPom.isFile() )
			{ throw newSpoonException( "Pom " +srcPom.getPath( ) +" not found.")
		;
		} try
			{ pom = newFile(decompiledRoot ,"pom.xml")
			;Files.copy(srcPom.toPath() ,pom.toPath() ,REPLACE_EXISTING)
		; } catch( IOExceptione )
			{ throw newSpoonException( "Unable to write " +pom.getPath())
		;
		} try
			{ SpoonPom pomModel = newSpoonPom(pom.getPath() ,null ,MavenLauncher.SOURCE_TYPE.APP_SOURCE ,getEnvironment())
			;getEnvironment().setComplianceLevel(pomModel.getSourceVersion())
			;String[ ] classpath =pomModel.buildClassPath(null ,MavenLauncher.SOURCE_TYPE.APP_SOURCE ,LOGGER ,false)
			;
			// dependenciesthis.getModelBuilder().setSourceClasspath(classpath)
		; } catch( IOException | XmlPullParserExceptione )
			{ throw newSpoonException("Failed to read classpath file.")
		;
		}addInputResource(decompiledSrc.getAbsolutePath())
	; } else
		{addInputResource(decompiledSrc.getAbsolutePath())
	;
}

} protected DecompilergetDefaultDecompiler( )
	{ return newCFRDecompiler(decompiledSrc)
;
}

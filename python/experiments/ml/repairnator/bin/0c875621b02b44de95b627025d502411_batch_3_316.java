package fr.inria.spirals.repairnator.process.maven.output;

import fr.inria.spirals.repairnator.process.inspectors.ProjectInspector;
import fr.inria.spirals.repairnator.process.maven.

MavenHelper ;importorg.apache.maven.shared.invoker.
InvocationOutputHandler ;importorg.slf4j.
Logger ;importorg.slf4j.

LoggerFactory ;importjava.io.
FileWriter ;importjava.io.

IOException
; /**
 * Created by urli on 15/02/2017.
 */ public abstract class MavenOutputHandler implements

    InvocationOutputHandler { private final Logger logger=LoggerFactory.getLogger(this.getClass()

    ) ; privateMavenHelper
    mavenHelper ; protectedProjectInspector
    inspector ; protectedString
    name ; privateFileWriter

    fileWriter ;publicMavenOutputHandler (MavenHelper mavenHelper
        ){this . mavenHelper=
        mavenHelper;this . inspector=mavenHelper.getInspector(
        );this . name=mavenHelper.getName(
        );this.initFileWriter(
    )

    ; } protectedLoggergetLogger (
        ) {returnthis.
    logger

    ; } privatevoidinitFileWriter (
        ) { String filename = "repairnator.maven."+name.toLowerCase ( )+
        ".log" ; String filePath=inspector.getRepoLocalPath ( ) + "/"+

        filename;inspector.getJobStatus().addFileToPush(filename
        ) ;
            try{this . fileWriter =newFileWriter(filePath
        ) ; }catch (IOException e
            ){this.getLogger().error ( "Cannot create file writer for file " + filePath+ ".",e
        )
    ;

    } } privatevoidwriteToFile (String s
        ) {if(this . fileWriter!= null
            ) {
                try{this.fileWriter.write(s
                );this.fileWriter.flush(
            ) ; }catch (IOException e
                ){this.getLogger().error( "Error while writing to repairnator.maven log.",e
            )
        ;
    }

    }}
    @ Override publicvoidconsumeLine (String s
        ){this.mavenHelper.updateLastOutputDate(
        );this.writeToFile ( s+"\n"
    )
;

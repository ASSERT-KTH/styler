package com.developmentontheedge.be5.server.services.impl

; importjavax.mail.MessagingException
; importjavax.mail.Session
; importjavax.mail.internet.MimeMessage
; importjava.io.ByteArrayOutputStream
; importjava.io.InputStream
; importjava.io.OutputStream
; importjava.io.PrintStream


; public class MimeMessage2 extends
MimeMessage
    { public static class PrintStream2 extends
    PrintStream
        { publicPrintStream2( OutputStreamoutputStream , booleanautoFlush
        )
            {super(outputStream ,autoFlush)
        ;

        } public ByteArrayOutputStreamgetOutput(
        )
            { return(ByteArrayOutputStream )out
        ;
    }

    } publicMimeMessage2( Sessionsession
    )
        {super(session)
    ;

    } publicMimeMessage2( Sessionsession , InputStreamis
            ) throws
    MessagingException
        {super(session ,is)
    ;

    } public SessiongetSession(
    )
        { returnsession
    ;
}

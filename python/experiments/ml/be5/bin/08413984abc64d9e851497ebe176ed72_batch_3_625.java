package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;importjavax.mail
.internet .MimeMessage;importjava.io.ByteArrayOutputStream;importjava.io.InputStream
;import java.io.OutputStream;importjava.io.PrintStream
;public classMimeMessage2extendsMimeMessage{publicstaticclass
PrintStream2 extendsPrintStream{publicPrintStream2(
OutputStream outputStream,booleanautoFlush){


super ( outputStream , autoFlush
)
    ; } public ByteArrayOutputStream getOutput (
    )
        { return(ByteArrayOutputStream )out ; }}
        public
            MimeMessage2(Sessionsession ){super
        (

        session ) ;}public
        MimeMessage2
            ( Sessionsession, InputStreamis
        )
    throws

    MessagingException {super( session,
    is
        );}publicSession
    getSession

    ( ){return session; } }
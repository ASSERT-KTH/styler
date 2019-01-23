package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;importjava.io.
PrintStream; publicclassMimeMessage2extendsMimeMessage{publicstaticclassPrintStream2extendsPrintStream


{public PrintStream2( OutputStreamoutputStream ,boolean autoFlush)
{super
    (outputStream ,autoFlush ); }public ByteArrayOutputStream getOutput (
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
package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;publicclassMimeMessage2


extendsMimeMessage {public staticclass PrintStream2extends PrintStream{
publicPrintStream2
    (OutputStream outputStream, booleanautoFlush ){ super(
    outputStream,
        autoFlush) ;}publicByteArrayOutputStreamgetOutput( ){return ( ByteArrayOutputStream)
        out
            ;}}public MimeMessage2(Session
        session

        ) { super(session
        )
            ; }publicMimeMessage2 (Session
        session
    ,

    InputStream is)throws MessagingException{
    super
        (session,is)
    ;

    } publicSessiongetSession () { returnsession
            ; }
    }
        
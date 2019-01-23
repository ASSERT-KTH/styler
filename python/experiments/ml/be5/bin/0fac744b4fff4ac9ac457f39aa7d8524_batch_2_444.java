package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream
; importjava.io.InputStream


; import java . io
.
    OutputStream ; import java . io
    .
        PrintStream ;publicclass MimeMessage2extends MimeMessage {public
        static
            classPrintStream2extendsPrintStream {publicPrintStream2
        (

        OutputStream outputStream ,booleanautoFlush
        )
            { super(outputStream ,autoFlush
        )
    ;

    } publicByteArrayOutputStreamgetOutput ()
    {
        return(ByteArrayOutputStream)out
    ;

    } }publicMimeMessage2 (Session session ){
            super (
    session
        );}public MimeMessage2(Session
    session

    , InputStream is)throws
    MessagingException
        { super(
    session
,

package com.developmentontheedge.be5.server.services.impl;importjavax.mail
. MessagingException;importjavax.mail.Session
; importjavax.mail.internet
. MimeMessage;importjava.io
. ByteArrayOutputStream;importjava.io
. InputStream;importjava.io


. OutputStream ; import java
.
    io . PrintStream ; public class
    MimeMessage2
        extends MimeMessage{public staticclass PrintStream2 extendsPrintStream
        {
            publicPrintStream2(OutputStream outputStream,boolean
        autoFlush

        ) { super(outputStream
        ,
            autoFlush );} publicByteArrayOutputStream
        getOutput
    (

    ) {return( ByteArrayOutputStream)
    out
        ;}}publicMimeMessage2
    (

    Session session){ super( session );
            } public
    MimeMessage2
        (Sessionsession, InputStreamis)
    throws

    MessagingException { super(session
    ,
        is );
    }
public

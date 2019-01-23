package com.developmentontheedge.be5.server.services.impl;importjavax.mail.MessagingException;import

javax. mail.Session;importjavax.mail.internet.MimeMessage
;import java.io.ByteArrayOutputStream;importjava.
io .InputStream;importjava.io.
OutputStream ;importjava.io.
PrintStream ;publicclassMimeMessage2extendsMimeMessage
{ publicstaticclassPrintStream2extendsPrintStream
{ publicPrintStream2(OutputStreamoutputStream,


boolean autoFlush ) { super
(
    outputStream , autoFlush ) ; }
    public
        ByteArrayOutputStream getOutput() {return ( ByteArrayOutputStream)
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
        
package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;import
java. io.InputStream;importjava.io.OutputStream;import
java. io.PrintStream;publicclassMimeMessage2extendsMimeMessage{publicstaticclassPrintStream2extendsPrintStream
{public PrintStream2(OutputStreamoutputStream,booleanautoFlush){super(


outputStream , autoFlush ) ;
}
    public ByteArrayOutputStream getOutput ( ) {
    return
        ( ByteArrayOutputStream)out ;} } publicMimeMessage2
        (
            Sessionsession){ super(session
        )

        ; } publicMimeMessage2(
        Session
            session ,InputStreamis )throws
        MessagingException
    {

    super (session, is)
    ;
        }publicSessiongetSession(
    )

    { returnsession; }} 
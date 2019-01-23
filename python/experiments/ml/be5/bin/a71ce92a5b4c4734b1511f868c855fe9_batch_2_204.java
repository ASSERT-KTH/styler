package com.developmentontheedge.be5.server.services.impl;importjavax.

mail. MessagingException;importjavax.mail.Session;importjavax.mail.internet.
MimeMessage; importjava.io.ByteArrayOutputStream;importjava.io.
InputStream; importjava.io.OutputStream;importjava.io
. PrintStream;publicclassMimeMessage2extends
MimeMessage {publicstaticclassPrintStream2extends
PrintStream {publicPrintStream2(OutputStreamoutputStream
, booleanautoFlush){super(


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
package com.developmentontheedge.be5.server.services.impl;importjavax.mail

.MessagingException ;importjavax.mail.Session;importjavax.mail
.internet .MimeMessage;importjava.io.ByteArrayOutputStream;import
java .io.InputStream;importjava.
io .OutputStream;importjava.
io .PrintStream;publicclassMimeMessage2
extends MimeMessage{publicstaticclassPrintStream2
extends PrintStream{publicPrintStream2(OutputStream


outputStream , boolean autoFlush )
{
    super ( outputStream , autoFlush )
    ;
        } publicByteArrayOutputStreamgetOutput () { return(
        ByteArrayOutputStream
            )out;} }publicMimeMessage2
        (

        Session session ){super
        (
            session );} publicMimeMessage2
        (
    Session

    session ,InputStreamis )throws
    MessagingException
        {super(session,
    is

    ) ;}public SessiongetSession ( ){
            return session
    ;
        }}
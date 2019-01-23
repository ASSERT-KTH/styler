package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;import
java. io.InputStream;importjava.io.OutputStream;import
java. io.PrintStream;publicclassMimeMessage2extendsMimeMessage{publicstatic
classPrintStream2 extendsPrintStream{publicPrintStream2(OutputStream


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
package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;importjava.
io. InputStream;importjava.io.OutputStream;importjava.


io. PrintStream;publicclassMimeMessage2extendsMimeMessage{publicstaticclassPrintStream2
extendsPrintStream {publicPrintStream2(OutputStreamoutputStream,booleanautoFlush


) { super ( outputStream
,
    autoFlush ) ; } public ByteArrayOutputStream
    getOutput
        ( ){return (ByteArrayOutputStream ) out;
        }
            }publicMimeMessage2( Sessionsession)
        {

        super ( session);
        }
            public MimeMessage2(Session session,
        InputStream
    is

    ) throwsMessagingException{ super(
    session
        ,is);}
    public

    Session getSession() {return session ;}
            } 
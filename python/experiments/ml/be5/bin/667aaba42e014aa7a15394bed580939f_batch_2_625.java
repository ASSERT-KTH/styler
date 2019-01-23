package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;importjavax.mail
.internet .MimeMessage;importjava.io.ByteArrayOutputStream;importjava.io.InputStream
;import java.io.OutputStream;importjava.io.PrintStream
;public classMimeMessage2extendsMimeMessage{public
static classPrintStream2extendsPrintStream{public
PrintStream2 (OutputStreamoutputStream,booleanautoFlush


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
package com.developmentontheedge.be5.server.services.impl;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;


public class MimeMessage2 extends MimeMessage
{
    public static class PrintStream2 extends
PrintStream
    { publicPrintStream2( OutputStreamoutputStream , booleanautoFlush
    )
        {super(outputStream ,autoFlush)
    ;

    } public ByteArrayOutputStreamgetOutput(
    )
        { return(ByteArrayOutputStream )out
    ;
}

} publicMimeMessage2( Sessionsession
)
    {super(session)
;

} publicMimeMessage2( Sessionsession , InputStreamis
        ) throws
MessagingException
    {super(session ,is)
;

} public SessiongetSession(
)
    { returnsession
;
}

package com.developmentontheedge.be5.metadata;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class TestUtils
{
    public static void assertFileEquals(File expectedFile, File testFile) throws Exception
    {
        assertFileEquals("", expectedFile, testFile);
    }

    public static void assertFileEquals(String message, File expectedFile, File testFile) throws Exception
    {
        try (InputStream stream1 = new FileInputStream(expectedFile); InputStream stream2 = new FileInputStream(testFile))
        {
            assertFileEquals(message, stream1, stream2);
        }
    }

    public static void assertFileEquals(InputStream expectedFile, InputStream testFile) throws Exception
    {
        assertFileEquals("", expectedFile, testFile);
    }

    public static void assertFileEquals(String message, InputStream expectedFile, InputStream testFile) throws Exception
    {
        assertFileEquals(message, new InputStreamReader(expectedFile, StandardCharsets.UTF_8), new InputStreamReader(testFile, StandardCharsets.UTF_8));
    }

    public static void assertFileEquals(Reader expectedFile, Reader testFile) throws Exception
    {
        assertFileEquals("", expectedFile, testFile);
    }

    public static void assertFileEquals(String message, Reader expectedFile, Reader testFile) throws Exception
    {
        try (final BufferedReader brOrig = new BufferedReader(expectedFile);
             final BufferedReader brTest = new BufferedReader(testFile))
        {
            final String cleanMessage = cleanUpMessage(message);
            int i = 0;
            while (true)
            {
                i++;
                String a = brOrig.readLine();
                String b = brTest.readLine();
                if (a == null)
                {
                    if (b == null)
                    {
                        break;
                    }
                    Assert.fail(cleanMessage + "Different line count");
                }
                else
                {
                    if (b == null)
                    {
                        Assert.fail(cleanMessage + "Different line count");
                    }
                }
                Assert.assertEquals(cleanMessage + "Line [" + i + "] does not match: ", a.trim(), b.trim());
            }
        }
    }

    private static String cleanUpMessage(String message)
    {
        if (message == null)
        {
            return message;
        }
        if (!message.isEmpty())
        {
            return message + ": ";
        }
        return message;
    }
}

package com.developmentontheedge.be5.database.sql;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class ResultSetDelegatorTest
{
    /**
     * https://stackoverflow.com/questions/22225663/checking-in-a-unit-test-whether-all-methods-are-delegated?answertab=active#tab-top
     */
    @Test
    public void setResultSet() throws Exception
    {
        ResultSet delegate = Mockito.mock(ResultSet.class);
        ResultSet wrapper = new ResultSetWrapper(delegate);

        // For each method in the Foo class...
        for (Method fooMethod : ResultSet.class.getDeclaredMethods())
        {
            if (fooMethod.getName().equals("next")) continue;
            boolean methodCalled = false;

            // Find matching method in wrapper class and call it
            for (Method wrapperMethod : ResultSetWrapper.class.getDeclaredMethods())
            {
                Class<?>[] fooParameterTypes = fooMethod.getParameterTypes();
                Class<?>[] parameterTypes = wrapperMethod.getParameterTypes();
                if (fooMethod.getName().equals(wrapperMethod.getName()) &&
                        Arrays.equals(fooParameterTypes, parameterTypes))
                {

                    Object[] arguments = new Object[parameterTypes.length];
                    for (int j = 0; j < arguments.length; j++)
                    {
                        if (parameterTypes[j] == long.class)
                        {
                            arguments[j] = 1L;
                            continue;
                        }
                        if (parameterTypes[j] == int.class)
                        {
                            arguments[j] = 2;
                            continue;
                        }
                        if (parameterTypes[j] == short.class)
                        {
                            arguments[j] = (short) 2;
                            continue;
                        }
                        if (parameterTypes[j] == double.class)
                        {
                            arguments[j] = 1.1;
                            continue;
                        }
                        if (parameterTypes[j] == float.class)
                        {
                            arguments[j] = 1.2f;
                            continue;
                        }
                        if (parameterTypes[j] == boolean.class)
                        {
                            arguments[j] = true;
                            continue;
                        }
                        if (parameterTypes[j] == char.class)
                        {
                            arguments[j] = 'c';
                            continue;
                        }
                        if (parameterTypes[j] == byte.class)
                        {
                            arguments[j] = (byte) 3;
                            continue;
                        }

                        if (parameterTypes[j] == byte[].class)
                        {
                            arguments[j] = new byte[]{(byte) 3};
                            continue;
                        }

                        if (parameterTypes[j] == String.class)
                        {
                            arguments[j] = "test";
                            continue;
                        }

                        if (parameterTypes[j] == Class.class)
                        {
                            arguments[j] = String.class;
                            continue;
                        }
                        arguments[j] = Mockito.mock(parameterTypes[j]);
                    }

                    // Invoke wrapper method
                    try
                    {
                        wrapperMethod.invoke(wrapper, arguments);
                    }
                    catch (IllegalArgumentException e)
                    {
                        e.printStackTrace();
                    }
                    // Ensure method was called on delegate exactly once with the correct arguments
                    fooMethod.invoke(Mockito.verify(delegate), arguments);

                    // Set flag to indicate that this foo method is wrapped properly.
                    methodCalled = true;
                }
            }

            assertTrue("Foo method '" + fooMethod.getName() + "' has not been wrapped correctly in Foo wrapper", methodCalled);
        }
    }

}
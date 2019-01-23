/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.Builder;

/**
 * Builds a string from constituent parts providing a more flexible and powerful API
 * than StringBuffer.
 * <p>
 * The main differences from StringBuffer/StringBuilder are:
 * </p>
 * <ul>
 * <li>Not synchronized</li>
 * <li>Not final</li>
 * <li>Subclasses have direct access to character array</li>
 * <li>Additional methods
 *  <ul>
 *   <li>appendWithSeparators - adds an array of values, with a separator</li>
 *   <li>appendPadding - adds a length padding characters</li>
 *   <li>appendFixedLength - adds a fixed width field to the builder</li>
 *   <li>toCharArray/getChars - simpler ways to get a range of the character array</li>
 *   <li>delete - delete char or string</li>
 *   <li>replace - search and replace for a char or string</li>
 *   <li>leftString/rightString/midString - substring without exceptions</li>
 *   <li>contains - whether the builder contains a char or string</li>
 *   <li>size/clear/isEmpty - collections style API methods</li>
 *  </ul>
 * </li>
 * <li>Views
 *  <ul>
 *   <li>asTokenizer - uses the internal buffer as the source of a StrTokenizer</li>
 *   <li>asReader - uses the internal buffer as the source of a Reader</li>
 *   <li>asWriter - allows a Writer to write directly to the internal buffer</li>
 *  </ul>
 * </li>
 * </ul>
 * <p>
 * The aim has been to provide an API that mimics very closely what StringBuffer
 * provides, but with additional methods. It should be noted that some edge cases,
 * with invalid indices or null input, have been altered - see individual methods.
 * The biggest of these changes is that by default, null will not output the text
 * 'null'. This can be controlled by a property, {@link #setNullText(String)}.
 * <p>
 * Prior to 3.0, this class implemented Cloneable but did not implement the
 * clone method so could not be used. From 3.0 onwards it no longer implements
 * the interface.
 *
 * @since 2.2
 * @deprecated as of 3.6, use commons-text
 * <a href="https://commons.apache.org/proper/commons-text/javadocs/api-release/org/apache/commons/text/TextStringBuilder.html">
 * TextStringBuilder</a> instead
 */
@Deprecated
public class StrBuilder implements CharSequence, Appendable, Serializable, Builder<String> {

    /**
     * The extra capacity for new builders.
     */
    static final int CAPACITY = 32;

    /**
     * Required for serialization support.
     *
     * @see java.io.Serializable
     */
    private static final long serialVersionUID = 7628716375283629643L;

    /** Internal data storage. */
    protected char[] buffer; // TODO make private?
    /** Current size of the buffer. */
    protected int size; // TODO make private?
    /** The new line. */
    private String newLine;
    /** The null text. */
    private String nullText;

    //-----------------------------------------------------------------------
    /**
     * Constructor that creates an empty builder initial capacity 32 characters.
     */
    public StrBuilder() {
        this(CAPACITY);
    }

    /**
     * Constructor that creates an empty builder the specified initial capacity.
     *
     * @param initialCapacity  the initial capacity, zero or less will be converted to 32
     */
    public StrBuilder(int initialCapacity) {
        super();
        if (initialCapacity <= 0) {
            initialCapacity = CAPACITY;
        }
        buffer = new char[initialCapacity];
    }

    /**
     * Constructor that creates a builder from the string, allocating
     * 32 extra characters for growth.
     *
     * @param str  the string to copy, null treated as blank string
     */
    public StrBuilder(final String str) {
        super();
        if (str == null) {
            buffer = new char[CAPACITY];
        } else {
            buffer = new char[str.length() + CAPACITY];
            append(str);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the text to be appended when a new line is added.
     *
     * @return the new line text, null means use system default
     */
    public String getNewLineText() {
        return newLine;
    }

    /**
     * Sets the text to be appended when a new line is added.
     *
     * @param newLine  the new line text, null means use system default
     * @return this, to enable chaining
     */
    public StrBuilder setNewLineText(final String newLine) {
        this.newLine = newLine;
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the text to be appended when null is added.
     *
     * @return the null text, null means no append
     */
    public String getNullText() {
        return nullText;
    }

    /**
     * Sets the text to be appended when null is added.
     *
     * @param nullText  the null text, null means no append
     * @return this, to enable chaining
     */
    public StrBuilder setNullText(String nullText) {
        if (nullText != null && nullText.isEmpty()) {
            nullText = null;
        }
        this.nullText = nullText;
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of the string builder.
     *
     * @return the length
     */
    @Override
    public int length() {
        return size;
    }

    /**
     * Updates the length of the builder by either dropping the last characters
     * or adding filler of Unicode zero.
     *
     * @param length  the length to set to, must be zero or positive
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the length is negative
     */
    public StrBuilder setLength(final int length) {
        if (length < 0) {
            throw new StringIndexOutOfBoundsException(length);
        }
        if (length < size) {
            size = length;
        } else if (length > size) {
            ensureCapacity(length);
            final int oldEnd = size;
            final int newEnd = length;
            size = length;
            for (int i = oldEnd; i < newEnd; i++) {
                buffer[i] = CharUtils.NUL;
            }
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current size of the internal character array buffer.
     *
     * @return the capacity
     */
    public int capacity() {
        return buffer.length;
    }

    /**
     * Checks the capacity and ensures that it is at least the size specified.
     *
     * @param capacity  the capacity to ensure
     * @return this, to enable chaining
     */
    public StrBuilder ensureCapacity(final int capacity) {
        if (capacity > buffer.length) {
            final char[] old = buffer;
            buffer = new char[capacity * 2];
            System.arraycopy(old, 0, buffer, 0, size);
        }
        return this;
    }

    /**
     * Minimizes the capacity to the actual length of the string.
     *
     * @return this, to enable chaining
     */
    public StrBuilder minimizeCapacity() {
        if (buffer.length > length()) {
            final char[] old = buffer;
            buffer = new char[length()];
            System.arraycopy(old, 0, buffer, 0, size);
        }
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the length of the string builder.
     * <p>
     * This method is the same as {@link #length()} and is provided to match the
     * API of Collections.
     *
     * @return the length
     */
    public int size() {
        return size;
    }

    /**
     * Checks is the string builder is empty (convenience Collections API style method).
     * <p>
     * This method is the same as checking {@link #length()} and is provided to match the
     * API of Collections.
     *
     * @return <code>true</code> if the size is <code>0</code>.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears the string builder (convenience Collections API style method).
     * <p>
     * This method does not reduce the size of the internal character buffer.
     * To do that, call <code>clear()</code> followed by {@link #minimizeCapacity()}.
     * <p>
     * This method is the same as {@link #setLength(int)} called with zero
     * and is provided to match the API of Collections.
     *
     * @return this, to enable chaining
     */
    public StrBuilder clear() {
        size = 0;
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the character at the specified index.
     *
     * @see #setCharAt(int, char)
     * @see #deleteCharAt(int)
     * @param index  the index to retrieve, must be valid
     * @return the character at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    @Override
    public char charAt(final int index) {
        if (index < 0 || index >= length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return buffer[index];
    }

    /**
     * Sets the character at the specified index.
     *
     * @see #charAt(int)
     * @see #deleteCharAt(int)
     * @param index  the index to set
     * @param ch  the new character
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public StrBuilder setCharAt(final int index, final char ch) {
        if (index < 0 || index >= length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        buffer[index] = ch;
        return this;
    }

    /**
     * Deletes the character at the specified index.
     *
     * @see #charAt(int)
     * @see #setCharAt(int, char)
     * @param index  the index to delete
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public StrBuilder deleteCharAt(final int index) {
        if (index < 0 || index >= size) {
            throw new StringIndexOutOfBoundsException(index);
        }
        deleteImpl(index, index + 1, 1);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Copies the builder's character array into a new character array.
     *
     * @return a new array that represents the contents of the builder
     */
    public char[] toCharArray() {
        if (size == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char chars[] = new char[size];
        System.arraycopy(buffer, 0, chars, 0, size);
        return chars;
    }

    /**
     * Copies part of the builder's character array into a new character array.
     *
     * @param startIndex  the start index, inclusive, must be valid
     * @param endIndex  the end index, exclusive, must be valid except that
     *  if too large it is treated as end of string
     * @return a new array that holds part of the contents of the builder
     * @throws IndexOutOfBoundsException if startIndex is invalid,
     *  or if endIndex is invalid (but endIndex greater than size is valid)
     */
    public char[] toCharArray(final int startIndex, int endIndex) {
        endIndex = validateRange(startIndex, endIndex);
        final int len = endIndex - startIndex;
        if (len == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char chars[] = new char[len];
        System.arraycopy(buffer, startIndex, chars, 0, len);
        return chars;
    }

    /**
     * Copies the character array into the specified array.
     *
     * @param destination  the destination array, null will cause an array to be created
     * @return the input array, unless that was null or too small
     */
    public char[] getChars(char[] destination) {
        final int len = length();
        if (destination == null || destination.length < len) {
            destination = new char[len];
        }
        System.arraycopy(buffer, 0, destination, 0, len);
        return destination;
    }

    /**
     * Copies the character array into the specified array.
     *
     * @param startIndex  first index to copy, inclusive, must be valid
     * @param endIndex  last index, exclusive, must be valid
     * @param destination  the destination array, must not be null or too small
     * @param destinationIndex  the index to start copying in destination
     * @throws NullPointerException if the array is null
     * @throws IndexOutOfBoundsException if any index is invalid
     */
    public void getChars(final int startIndex, final int endIndex, final char destination[], final int destinationIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        }
        if (endIndex < 0 || endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (startIndex > endIndex) {
            throw new StringIndexOutOfBoundsException("end < start");
        }
        System.arraycopy(buffer, startIndex, destination, destinationIndex, endIndex - startIndex);
    }

    //-----------------------------------------------------------------------
    /**
     * If possible, reads chars from the provided {@link Readable} directly into underlying
     * character buffer without making extra copies.
     *
     * @param readable  object to read from
     * @return the number of characters read
     * @throws IOException if an I/O error occurs
     *
     * @since 3.4
     * @see #appendTo(Appendable)
     */
    public int readFrom(final Readable readable) throws IOException {
        final int oldSize = size;
        if (readable instanceof Reader) {
            final Reader r = (Reader) readable;
            ensureCapacity(size + 1);
            int read;
            while ((read = r.read(buffer, size, buffer.length - size)) != -1) {
                size += read;
                ensureCapacity(size + 1);
            }
        } else if (readable instanceof CharBuffer) {
            final CharBuffer cb = (CharBuffer) readable;
            final int remaining = cb.remaining();
            ensureCapacity(size + remaining);
            cb.get(buffer, size, remaining);
            size += remaining;
        } else {
            while (true) {
                ensureCapacity(size + 1);
                final CharBuffer buf = CharBuffer.wrap(buffer, size, buffer.length - size);
                final int read = readable.read(buf);
                if (read == -1) {
                    break;
                }
                size += read;
            }
        }
        return size - oldSize;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the new line string to this string builder.
     * <p>
     * The new line string can be altered using {@link #setNewLineText(String)}.
     * This might be used to force the output to always use Unix line endings
     * even when on Windows.
     *
     * @return this, to enable chaining
     */
    public StrBuilder appendNewLine() {
        if (newLine == null)  {
            append(System.lineSeparator());
            return this;
        }
        return append(newLine);
    }

    /**
     * Appends the text representing <code>null</code> to this string builder.
     *
     * @return this, to enable chaining
     */
    public StrBuilder appendNull() {
        if (nullText == null)  {
            return this;
        }
        return append(nullText);
    }

    /**
     * Appends an object to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param obj  the object to append
     * @return this, to enable chaining
     */
    public StrBuilder append(final Object obj) {
        if (obj == null) {
            return appendNull();
        }
        if (obj instanceof CharSequence) {
            return append((CharSequence) obj);
        }
        return append(obj.toString());
    }

    /**
     * Appends a CharSequence to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param seq  the CharSequence to append
     * @return this, to enable chaining
     * @since 3.0
     */
    @Override
    public StrBuilder append(final CharSequence seq) {
        if (seq == null) {
            return appendNull();
        }
        if (seq instanceof StrBuilder) {
            return append((StrBuilder) seq);
        }
        if (seq instanceof StringBuilder) {
            return append((StringBuilder) seq);
        }
        if (seq instanceof StringBuffer) {
            return append((StringBuffer) seq);
        }
        if (seq instanceof CharBuffer) {
            return append((CharBuffer) seq);
        }
        return append(seq.toString());
    }

    /**
     * Appends part of a CharSequence to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param seq  the CharSequence to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 3.0
     */
    @Override
    public StrBuilder append(final CharSequence seq, final int startIndex, final int length) {
        if (seq == null) {
            return appendNull();
        }
        return append(seq.toString(), startIndex, length);
    }

    /**
     * Appends a string to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @return this, to enable chaining
     */
    public StrBuilder append(final String str) {
        if (str == null) {
            return appendNull();
        }
        final int strLen = str.length();
        if (strLen > 0) {
            final int len = length();
            ensureCapacity(len + strLen);
            str.getChars(0, strLen, buffer, len);
            size += strLen;
        }
        return this;
    }


    /**
     * Appends part of a string to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     */
    public StrBuilder append(final String str, final int startIndex, final int length) {
        if (str == null) {
            return appendNull()
        ;
        } if( startIndex < 0 || startIndex >str.length() )
            { throw newStringIndexOutOfBoundsException("startIndex must be valid")
        ;
        } if( length < 0 ||( startIndex +length ) >str.length() )
            { throw newStringIndexOutOfBoundsException("length must be valid")
        ;
        } if( length >0 )
            { final int len =length()
            ;ensureCapacity( len +length)
            ;str.getChars(startIndex , startIndex +length ,buffer ,len)
            ; size +=length
        ;
        } returnthis
    ;

    }
    /**
     * Calls {@link String#format(String, Object...)} and appends the result.
     *
     * @param format the format string
     * @param objs the objects to use in the format string
     * @return {@code this} to enable chaining
     * @see String#format(String, Object...)
     * @since 3.2
     */ public StrBuilderappend( final Stringformat , finalObject ...objs )
        { returnappend(String.format(format ,objs))
    ;

    }
    /**
     * Appends the contents of a char buffer to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param buf  the char buffer to append
     * @return this, to enable chaining
     * @since 3.4
     */ public StrBuilderappend( final CharBufferbuf )
        { if( buf ==null )
            { returnappendNull()
        ;
        } if(buf.hasArray() )
            { final int length =buf.remaining()
            ; final int len =length()
            ;ensureCapacity( len +length)
            ;System.arraycopy(buf.array() ,buf.arrayOffset( ) +buf.position() ,buffer ,len ,length)
            ; size +=length
        ; } else
            {append(buf.toString())
        ;
        } returnthis
    ;

    }
    /**
     * Appends the contents of a char buffer to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param buf  the char buffer to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 3.4
     */ public StrBuilderappend( final CharBufferbuf , final intstartIndex , final intlength )
        { if( buf ==null )
            { returnappendNull()
        ;
        } if(buf.hasArray() )
            { final int totalLength =buf.remaining()
            ; if( startIndex < 0 || startIndex >totalLength )
                { throw newStringIndexOutOfBoundsException("startIndex must be valid")
            ;
            } if( length < 0 ||( startIndex +length ) >totalLength )
                { throw newStringIndexOutOfBoundsException("length must be valid")
            ;
            } final int len =length()
            ;ensureCapacity( len +length)
            ;System.arraycopy(buf.array() ,buf.arrayOffset( ) +buf.position( ) +startIndex ,buffer ,len ,length)
            ; size +=length
        ; } else
            {append(buf.toString() ,startIndex ,length)
        ;
        } returnthis
    ;

    }
    /**
     * Appends a string buffer to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string buffer to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final StringBufferstr )
        { if( str ==null )
            { returnappendNull()
        ;
        } final int strLen =str.length()
        ; if( strLen >0 )
            { final int len =length()
            ;ensureCapacity( len +strLen)
            ;str.getChars(0 ,strLen ,buffer ,len)
            ; size +=strLen
        ;
        } returnthis
    ;

    }
    /**
     * Appends part of a string buffer to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     */ public StrBuilderappend( final StringBufferstr , final intstartIndex , final intlength )
        { if( str ==null )
            { returnappendNull()
        ;
        } if( startIndex < 0 || startIndex >str.length() )
            { throw newStringIndexOutOfBoundsException("startIndex must be valid")
        ;
        } if( length < 0 ||( startIndex +length ) >str.length() )
            { throw newStringIndexOutOfBoundsException("length must be valid")
        ;
        } if( length >0 )
            { final int len =length()
            ;ensureCapacity( len +length)
            ;str.getChars(startIndex , startIndex +length ,buffer ,len)
            ; size +=length
        ;
        } returnthis
    ;

    }
    /**
     * Appends a StringBuilder to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str the StringBuilder to append
     * @return this, to enable chaining
     * @since 3.2
     */ public StrBuilderappend( final StringBuilderstr )
        { if( str ==null )
            { returnappendNull()
        ;
        } final int strLen =str.length()
        ; if( strLen >0 )
            { final int len =length()
            ;ensureCapacity( len +strLen)
            ;str.getChars(0 ,strLen ,buffer ,len)
            ; size +=strLen
        ;
        } returnthis
    ;

    }
    /**
     * Appends part of a StringBuilder to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str the StringBuilder to append
     * @param startIndex the start index, inclusive, must be valid
     * @param length the length to append, must be valid
     * @return this, to enable chaining
     * @since 3.2
     */ public StrBuilderappend( final StringBuilderstr , final intstartIndex , final intlength )
        { if( str ==null )
            { returnappendNull()
        ;
        } if( startIndex < 0 || startIndex >str.length() )
            { throw newStringIndexOutOfBoundsException("startIndex must be valid")
        ;
        } if( length < 0 ||( startIndex +length ) >str.length() )
            { throw newStringIndexOutOfBoundsException("length must be valid")
        ;
        } if( length >0 )
            { final int len =length()
            ;ensureCapacity( len +length)
            ;str.getChars(startIndex , startIndex +length ,buffer ,len)
            ; size +=length
        ;
        } returnthis
    ;

    }
    /**
     * Appends another string builder to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string builder to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final StrBuilderstr )
        { if( str ==null )
            { returnappendNull()
        ;
        } final int strLen =str.length()
        ; if( strLen >0 )
            { final int len =length()
            ;ensureCapacity( len +strLen)
            ;System.arraycopy(str.buffer ,0 ,buffer ,len ,strLen)
            ; size +=strLen
        ;
        } returnthis
    ;

    }
    /**
     * Appends part of a string builder to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     */ public StrBuilderappend( final StrBuilderstr , final intstartIndex , final intlength )
        { if( str ==null )
            { returnappendNull()
        ;
        } if( startIndex < 0 || startIndex >str.length() )
            { throw newStringIndexOutOfBoundsException("startIndex must be valid")
        ;
        } if( length < 0 ||( startIndex +length ) >str.length() )
            { throw newStringIndexOutOfBoundsException("length must be valid")
        ;
        } if( length >0 )
            { final int len =length()
            ;ensureCapacity( len +length)
            ;str.getChars(startIndex , startIndex +length ,buffer ,len)
            ; size +=length
        ;
        } returnthis
    ;

    }
    /**
     * Appends a char array to the string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param chars  the char array to append
     * @return this, to enable chaining
     */ public StrBuilderappend( finalchar[ ]chars )
        { if( chars ==null )
            { returnappendNull()
        ;
        } final int strLen =chars.length
        ; if( strLen >0 )
            { final int len =length()
            ;ensureCapacity( len +strLen)
            ;System.arraycopy(chars ,0 ,buffer ,len ,strLen)
            ; size +=strLen
        ;
        } returnthis
    ;

    }
    /**
     * Appends a char array to the string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param chars  the char array to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     */ public StrBuilderappend( finalchar[ ]chars , final intstartIndex , final intlength )
        { if( chars ==null )
            { returnappendNull()
        ;
        } if( startIndex < 0 || startIndex >chars.length )
            { throw newStringIndexOutOfBoundsException( "Invalid startIndex: " +length)
        ;
        } if( length < 0 ||( startIndex +length ) >chars.length )
            { throw newStringIndexOutOfBoundsException( "Invalid length: " +length)
        ;
        } if( length >0 )
            { final int len =length()
            ;ensureCapacity( len +length)
            ;System.arraycopy(chars ,startIndex ,buffer ,len ,length)
            ; size +=length
        ;
        } returnthis
    ;

    }
    /**
     * Appends a boolean value to the string builder.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final booleanvalue )
        { if(value )
            {ensureCapacity( size +4)
            ;buffer[size++ ] ='t'
            ;buffer[size++ ] ='r'
            ;buffer[size++ ] ='u'
            ;buffer[size++ ] ='e'
        ; } else
            {ensureCapacity( size +5)
            ;buffer[size++ ] ='f'
            ;buffer[size++ ] ='a'
            ;buffer[size++ ] ='l'
            ;buffer[size++ ] ='s'
            ;buffer[size++ ] ='e'
        ;
        } returnthis
    ;

    }
    /**
     * Appends a char value to the string builder.
     *
     * @param ch  the value to append
     * @return this, to enable chaining
     * @since 3.0
     */@
    Override public StrBuilderappend( final charch )
        { final int len =length()
        ;ensureCapacity( len +1)
        ;buffer[size++ ] =ch
        ; returnthis
    ;

    }
    /**
     * Appends an int value to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final intvalue )
        { returnappend(String.valueOf(value))
    ;

    }
    /**
     * Appends a long value to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final longvalue )
        { returnappend(String.valueOf(value))
    ;

    }
    /**
     * Appends a float value to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final floatvalue )
        { returnappend(String.valueOf(value))
    ;

    }
    /**
     * Appends a double value to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     */ public StrBuilderappend( final doublevalue )
        { returnappend(String.valueOf(value))
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Appends an object followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param obj  the object to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final Objectobj )
        { returnappend(obj).appendNewLine()
    ;

    }
    /**
     * Appends a string followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final Stringstr )
        { returnappend(str).appendNewLine()
    ;

    }
    /**
     * Appends part of a string followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final Stringstr , final intstartIndex , final intlength )
        { returnappend(str ,startIndex ,length).appendNewLine()
    ;

    }
    /**
     * Calls {@link String#format(String, Object...)} and appends the result.
     *
     * @param format the format string
     * @param objs the objects to use in the format string
     * @return {@code this} to enable chaining
     * @see String#format(String, Object...)
     * @since 3.2
     */ public StrBuilderappendln( final Stringformat , finalObject ...objs )
        { returnappend(format ,objs).appendNewLine()
    ;

    }
    /**
     * Appends a string buffer followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string buffer to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final StringBufferstr )
        { returnappend(str).appendNewLine()
    ;

    }
    /**
     * Appends a string builder followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string builder to append
     * @return this, to enable chaining
     * @since 3.2
     */ public StrBuilderappendln( final StringBuilderstr )
        { returnappend(str).appendNewLine()
    ;

    }
    /**
     * Appends part of a string builder followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string builder to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 3.2
     */ public StrBuilderappendln( final StringBuilderstr , final intstartIndex , final intlength )
        { returnappend(str ,startIndex ,length).appendNewLine()
    ;

    }
    /**
     * Appends part of a string buffer followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final StringBufferstr , final intstartIndex , final intlength )
        { returnappend(str ,startIndex ,length).appendNewLine()
    ;

    }
    /**
     * Appends another string builder followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string builder to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final StrBuilderstr )
        { returnappend(str).appendNewLine()
    ;

    }
    /**
     * Appends part of a string builder followed by a new line to this string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param str  the string to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final StrBuilderstr , final intstartIndex , final intlength )
        { returnappend(str ,startIndex ,length).appendNewLine()
    ;

    }
    /**
     * Appends a char array followed by a new line to the string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param chars  the char array to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( finalchar[ ]chars )
        { returnappend(chars).appendNewLine()
    ;

    }
    /**
     * Appends a char array followed by a new line to the string builder.
     * Appending null will call {@link #appendNull()}.
     *
     * @param chars  the char array to append
     * @param startIndex  the start index, inclusive, must be valid
     * @param length  the length to append, must be valid
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( finalchar[ ]chars , final intstartIndex , final intlength )
        { returnappend(chars ,startIndex ,length).appendNewLine()
    ;

    }
    /**
     * Appends a boolean value followed by a new line to the string builder.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final booleanvalue )
        { returnappend(value).appendNewLine()
    ;

    }
    /**
     * Appends a char value followed by a new line to the string builder.
     *
     * @param ch  the value to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final charch )
        { returnappend(ch).appendNewLine()
    ;

    }
    /**
     * Appends an int value followed by a new line to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final intvalue )
        { returnappend(value).appendNewLine()
    ;

    }
    /**
     * Appends a long value followed by a new line to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final longvalue )
        { returnappend(value).appendNewLine()
    ;

    }
    /**
     * Appends a float value followed by a new line to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final floatvalue )
        { returnappend(value).appendNewLine()
    ;

    }
    /**
     * Appends a double value followed by a new line to the string builder using <code>String.valueOf</code>.
     *
     * @param value  the value to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendln( final doublevalue )
        { returnappend(value).appendNewLine()
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Appends each item in an array to the builder without any separators.
     * Appending a null array will have no effect.
     * Each object is appended using {@link #append(Object)}.
     *
     * @param <T>  the element type
     * @param array  the array to append
     * @return this, to enable chaining
     * @since 2.3
     */ public<T > StrBuilderappendAll(@SuppressWarnings("unchecked" ) finalT ...array )
        {
        /*
         * @SuppressWarnings used to hide warning about vararg usage. We cannot
         * use @SafeVarargs, since this method is not final. Using @SuppressWarnings
         * is fine, because it isn't inherited by subclasses, so each subclass must
         * vouch for itself whether its use of 'array' is safe.
         */ if( array != null &&array. length >0 )
            { for( final Object element :array )
                {append(element)
            ;
        }
        } returnthis
    ;

    }
    /**
     * Appends each item in an iterable to the builder without any separators.
     * Appending a null iterable will have no effect.
     * Each object is appended using {@link #append(Object)}.
     *
     * @param iterable  the iterable to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendAll( finalIterable<? >iterable )
        { if( iterable !=null )
            { for( final Object o :iterable )
                {append(o)
            ;
        }
        } returnthis
    ;

    }
    /**
     * Appends each item in an iterator to the builder without any separators.
     * Appending a null iterator will have no effect.
     * Each object is appended using {@link #append(Object)}.
     *
     * @param it  the iterator to append
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendAll( finalIterator<? >it )
        { if( it !=null )
            { while(it.hasNext() )
                {append(it.next())
            ;
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Appends an array placing separators between each value, but
     * not before the first or after the last.
     * Appending a null array will have no effect.
     * Each object is appended using {@link #append(Object)}.
     *
     * @param array  the array to append
     * @param separator  the separator to use, null means no separator
     * @return this, to enable chaining
     */ public StrBuilderappendWithSeparators( finalObject[ ]array , final Stringseparator )
        { if( array != null &&array. length >0 )
            { final String sep =Objects.toString(separator ,"")
            ;append(array[0])
            ; for( int i =1 ; i <array.length ;i++ )
                {append(sep)
                ;append(array[i])
            ;
        }
        } returnthis
    ;

    }
    /**
     * Appends an iterable placing separators between each value, but
     * not before the first or after the last.
     * Appending a null iterable will have no effect.
     * Each object is appended using {@link #append(Object)}.
     *
     * @param iterable  the iterable to append
     * @param separator  the separator to use, null means no separator
     * @return this, to enable chaining
     */ public StrBuilderappendWithSeparators( finalIterable<? >iterable , final Stringseparator )
        { if( iterable !=null )
            { final String sep =Objects.toString(separator ,"")
            ; finalIterator<? > it =iterable.iterator()
            ; while(it.hasNext() )
                {append(it.next())
                ; if(it.hasNext() )
                    {append(sep)
                ;
            }
        }
        } returnthis
    ;

    }
    /**
     * Appends an iterator placing separators between each value, but
     * not before the first or after the last.
     * Appending a null iterator will have no effect.
     * Each object is appended using {@link #append(Object)}.
     *
     * @param it  the iterator to append
     * @param separator  the separator to use, null means no separator
     * @return this, to enable chaining
     */ public StrBuilderappendWithSeparators( finalIterator<? >it , final Stringseparator )
        { if( it !=null )
            { final String sep =Objects.toString(separator ,"")
            ; while(it.hasNext() )
                {append(it.next())
                ; if(it.hasNext() )
                    {append(sep)
                ;
            }
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Appends a separator if the builder is currently non-empty.
     * Appending a null separator will have no effect.
     * The separator is appended using {@link #append(String)}.
     * <p>
     * This method is useful for adding a separator each time around the
     * loop except the first.
     * <pre>
     * for (Iterator it = list.iterator(); it.hasNext(); ) {
     *   appendSeparator(",");
     *   append(it.next());
     * }
     * </pre>
     * Note that for this simple example, you should use
     * {@link #appendWithSeparators(Iterable, String)}.
     *
     * @param separator  the separator to use, null means no separator
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendSeparator( final Stringseparator )
        { returnappendSeparator(separator ,null)
    ;

    }
    /**
     * Appends one of both separators to the StrBuilder.
     * If the builder is currently empty it will append the defaultIfEmpty-separator
     * Otherwise it will append the standard-separator
     *
     * Appending a null separator will have no effect.
     * The separator is appended using {@link #append(String)}.
     * <p>
     * This method is for example useful for constructing queries
     * <pre>
     * StrBuilder whereClause = new StrBuilder();
     * if (searchCommand.getPriority() != null) {
     *  whereClause.appendSeparator(" and", " where");
     *  whereClause.append(" priority = ?")
     * }
     * if (searchCommand.getComponent() != null) {
     *  whereClause.appendSeparator(" and", " where");
     *  whereClause.append(" component = ?")
     * }
     * selectClause.append(whereClause)
     * </pre>
     *
     * @param standard the separator if builder is not empty, null means no separator
     * @param defaultIfEmpty the separator if builder is empty, null means no separator
     * @return this, to enable chaining
     * @since 2.5
     */ public StrBuilderappendSeparator( final Stringstandard , final StringdefaultIfEmpty )
        { final String str =isEmpty( ) ? defaultIfEmpty :standard
        ; if( str !=null )
            {append(str)
        ;
        } returnthis
    ;

    }
    /**
     * Appends a separator if the builder is currently non-empty.
     * The separator is appended using {@link #append(char)}.
     * <p>
     * This method is useful for adding a separator each time around the
     * loop except the first.
     * <pre>
     * for (Iterator it = list.iterator(); it.hasNext(); ) {
     *   appendSeparator(',');
     *   append(it.next());
     * }
     * </pre>
     * Note that for this simple example, you should use
     * {@link #appendWithSeparators(Iterable, String)}.
     *
     * @param separator  the separator to use
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendSeparator( final charseparator )
        { if(size( ) >0 )
            {append(separator)
        ;
        } returnthis
    ;

    }
    /**
     * Append one of both separators to the builder
     * If the builder is currently empty it will append the defaultIfEmpty-separator
     * Otherwise it will append the standard-separator
     *
     * The separator is appended using {@link #append(char)}.
     * @param standard the separator if builder is not empty
     * @param defaultIfEmpty the separator if builder is empty
     * @return this, to enable chaining
     * @since 2.5
     */ public StrBuilderappendSeparator( final charstandard , final chardefaultIfEmpty )
        { if(size( ) >0 )
            {append(standard)
        ; } else
            {append(defaultIfEmpty)
        ;
        } returnthis
    ;
    }
    /**
     * Appends a separator to the builder if the loop index is greater than zero.
     * Appending a null separator will have no effect.
     * The separator is appended using {@link #append(String)}.
     * <p>
     * This method is useful for adding a separator each time around the
     * loop except the first.
     * </p>
     * <pre>
     * for (int i = 0; i &lt; list.size(); i++) {
     *   appendSeparator(",", i);
     *   append(list.get(i));
     * }
     * </pre>
     * Note that for this simple example, you should use
     * {@link #appendWithSeparators(Iterable, String)}.
     *
     * @param separator  the separator to use, null means no separator
     * @param loopIndex  the loop index
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendSeparator( final Stringseparator , final intloopIndex )
        { if( separator != null && loopIndex >0 )
            {append(separator)
        ;
        } returnthis
    ;

    }
    /**
     * Appends a separator to the builder if the loop index is greater than zero.
     * The separator is appended using {@link #append(char)}.
     * <p>
     * This method is useful for adding a separator each time around the
     * loop except the first.
     * </p>
     * <pre>
     * for (int i = 0; i &lt; list.size(); i++) {
     *   appendSeparator(",", i);
     *   append(list.get(i));
     * }
     * </pre>
     * Note that for this simple example, you should use
     * {@link #appendWithSeparators(Iterable, String)}.
     *
     * @param separator  the separator to use
     * @param loopIndex  the loop index
     * @return this, to enable chaining
     * @since 2.3
     */ public StrBuilderappendSeparator( final charseparator , final intloopIndex )
        { if( loopIndex >0 )
            {append(separator)
        ;
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Appends the pad character to the builder the specified number of times.
     *
     * @param length  the length to append, negative means no append
     * @param padChar  the character to append
     * @return this, to enable chaining
     */ public StrBuilderappendPadding( final intlength , final charpadChar )
        { if( length >=0 )
            {ensureCapacity( size +length)
            ; for( int i =0 ; i <length ;i++ )
                {buffer[size++ ] =padChar
            ;
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Appends an object to the builder padding on the left to a fixed width.
     * The <code>toString</code> of the object is used.
     * If the object is larger than the length, the left hand side is lost.
     * If the object is null, the null text value is used.
     *
     * @param obj  the object to append, null uses null text
     * @param width  the fixed field width, zero or negative has no effect
     * @param padChar  the pad character to use
     * @return this, to enable chaining
     */ public StrBuilderappendFixedWidthPadLeft( final Objectobj , final intwidth , final charpadChar )
        { if( width >0 )
            {ensureCapacity( size +width)
            ; String str =( obj == null ?getNullText( ) :obj.toString())
            ; if( str ==null )
                { str =StringUtils.EMPTY
            ;
            } final int strLen =str.length()
            ; if( strLen >=width )
                {str.getChars( strLen -width ,strLen ,buffer ,size)
            ; } else
                { final int padLen = width -strLen
                ; for( int i =0 ; i <padLen ;i++ )
                    {buffer[ size +i ] =padChar
                ;
                }str.getChars(0 ,strLen ,buffer , size +padLen)
            ;
            } size +=width
        ;
        } returnthis
    ;

    }
    /**
     * Appends an object to the builder padding on the left to a fixed width.
     * The <code>String.valueOf</code> of the <code>int</code> value is used.
     * If the formatted value is larger than the length, the left hand side is lost.
     *
     * @param value  the value to append
     * @param width  the fixed field width, zero or negative has no effect
     * @param padChar  the pad character to use
     * @return this, to enable chaining
     */ public StrBuilderappendFixedWidthPadLeft( final intvalue , final intwidth , final charpadChar )
        { returnappendFixedWidthPadLeft(String.valueOf(value) ,width ,padChar)
    ;

    }
    /**
     * Appends an object to the builder padding on the right to a fixed length.
     * The <code>toString</code> of the object is used.
     * If the object is larger than the length, the right hand side is lost.
     * If the object is null, null text value is used.
     *
     * @param obj  the object to append, null uses null text
     * @param width  the fixed field width, zero or negative has no effect
     * @param padChar  the pad character to use
     * @return this, to enable chaining
     */ public StrBuilderappendFixedWidthPadRight( final Objectobj , final intwidth , final charpadChar )
        { if( width >0 )
            {ensureCapacity( size +width)
            ; String str =( obj == null ?getNullText( ) :obj.toString())
            ; if( str ==null )
                { str =StringUtils.EMPTY
            ;
            } final int strLen =str.length()
            ; if( strLen >=width )
                {str.getChars(0 ,width ,buffer ,size)
            ; } else
                { final int padLen = width -strLen
                ;str.getChars(0 ,strLen ,buffer ,size)
                ; for( int i =0 ; i <padLen ;i++ )
                    {buffer[ size + strLen +i ] =padChar
                ;
            }
            } size +=width
        ;
        } returnthis
    ;

    }
    /**
     * Appends an object to the builder padding on the right to a fixed length.
     * The <code>String.valueOf</code> of the <code>int</code> value is used.
     * If the object is larger than the length, the right hand side is lost.
     *
     * @param value  the value to append
     * @param width  the fixed field width, zero or negative has no effect
     * @param padChar  the pad character to use
     * @return this, to enable chaining
     */ public StrBuilderappendFixedWidthPadRight( final intvalue , final intwidth , final charpadChar )
        { returnappendFixedWidthPadRight(String.valueOf(value) ,width ,padChar)
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Inserts the string representation of an object into this builder.
     * Inserting null will use the stored null text value.
     *
     * @param index  the index to add at, must be valid
     * @param obj  the object to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final Objectobj )
        { if( obj ==null )
            { returninsert(index ,nullText)
        ;
        } returninsert(index ,obj.toString())
    ;

    }
    /**
     * Inserts the string into this builder.
     * Inserting null will use the stored null text value.
     *
     * @param index  the index to add at, must be valid
     * @param str  the string to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , Stringstr )
        {validateIndex(index)
        ; if( str ==null )
            { str =nullText
        ;
        } if( str !=null )
            { final int strLen =str.length()
            ; if( strLen >0 )
                { final int newSize = size +strLen
                ;ensureCapacity(newSize)
                ;System.arraycopy(buffer ,index ,buffer , index +strLen , size -index)
                ; size =newSize
                ;str.getChars(0 ,strLen ,buffer ,index)
            ;
        }
        } returnthis
    ;

    }
    /**
     * Inserts the character array into this builder.
     * Inserting null will use the stored null text value.
     *
     * @param index  the index to add at, must be valid
     * @param chars  the char array to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final charchars[] )
        {validateIndex(index)
        ; if( chars ==null )
            { returninsert(index ,nullText)
        ;
        } final int len =chars.length
        ; if( len >0 )
            {ensureCapacity( size +len)
            ;System.arraycopy(buffer ,index ,buffer , index +len , size -index)
            ;System.arraycopy(chars ,0 ,buffer ,index ,len)
            ; size +=len
        ;
        } returnthis
    ;

    }
    /**
     * Inserts part of the character array into this builder.
     * Inserting null will use the stored null text value.
     *
     * @param index  the index to add at, must be valid
     * @param chars  the char array to insert
     * @param offset  the offset into the character array to start at, must be valid
     * @param length  the length of the character array part to copy, must be positive
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if any index is invalid
     */ public StrBuilderinsert( final intindex , final charchars[] , final intoffset , final intlength )
        {validateIndex(index)
        ; if( chars ==null )
            { returninsert(index ,nullText)
        ;
        } if( offset < 0 || offset >chars.length )
            { throw newStringIndexOutOfBoundsException( "Invalid offset: " +offset)
        ;
        } if( length < 0 || offset + length >chars.length )
            { throw newStringIndexOutOfBoundsException( "Invalid length: " +length)
        ;
        } if( length >0 )
            {ensureCapacity( size +length)
            ;System.arraycopy(buffer ,index ,buffer , index +length , size -index)
            ;System.arraycopy(chars ,offset ,buffer ,index ,length)
            ; size +=length
        ;
        } returnthis
    ;

    }
    /**
     * Inserts the value into this builder.
     *
     * @param index  the index to add at, must be valid
     * @param value  the value to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( intindex , final booleanvalue )
        {validateIndex(index)
        ; if(value )
            {ensureCapacity( size +4)
            ;System.arraycopy(buffer ,index ,buffer , index +4 , size -index)
            ;buffer[index++ ] ='t'
            ;buffer[index++ ] ='r'
            ;buffer[index++ ] ='u'
            ;buffer[index ] ='e'
            ; size +=4
        ; } else
            {ensureCapacity( size +5)
            ;System.arraycopy(buffer ,index ,buffer , index +5 , size -index)
            ;buffer[index++ ] ='f'
            ;buffer[index++ ] ='a'
            ;buffer[index++ ] ='l'
            ;buffer[index++ ] ='s'
            ;buffer[index ] ='e'
            ; size +=5
        ;
        } returnthis
    ;

    }
    /**
     * Inserts the value into this builder.
     *
     * @param index  the index to add at, must be valid
     * @param value  the value to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final charvalue )
        {validateIndex(index)
        ;ensureCapacity( size +1)
        ;System.arraycopy(buffer ,index ,buffer , index +1 , size -index)
        ;buffer[index ] =value
        ;size++
        ; returnthis
    ;

    }
    /**
     * Inserts the value into this builder.
     *
     * @param index  the index to add at, must be valid
     * @param value  the value to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final intvalue )
        { returninsert(index ,String.valueOf(value))
    ;

    }
    /**
     * Inserts the value into this builder.
     *
     * @param index  the index to add at, must be valid
     * @param value  the value to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final longvalue )
        { returninsert(index ,String.valueOf(value))
    ;

    }
    /**
     * Inserts the value into this builder.
     *
     * @param index  the index to add at, must be valid
     * @param value  the value to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final floatvalue )
        { returninsert(index ,String.valueOf(value))
    ;

    }
    /**
     * Inserts the value into this builder.
     *
     * @param index  the index to add at, must be valid
     * @param value  the value to insert
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderinsert( final intindex , final doublevalue )
        { returninsert(index ,String.valueOf(value))
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Internal method to delete a range without validation.
     *
     * @param startIndex  the start index, must be valid
     * @param endIndex  the end index (exclusive), must be valid
     * @param len  the length, must be valid
     * @throws IndexOutOfBoundsException if any index is invalid
     */ private voiddeleteImpl( final intstartIndex , final intendIndex , final intlen )
        {System.arraycopy(buffer ,endIndex ,buffer ,startIndex , size -endIndex)
        ; size -=len
    ;

    }
    /**
     * Deletes the characters between the two specified indices.
     *
     * @param startIndex  the start index, inclusive, must be valid
     * @param endIndex  the end index, exclusive, must be valid except
     *  that if too large it is treated as end of string
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderdelete( final intstartIndex , intendIndex )
        { endIndex =validateRange(startIndex ,endIndex)
        ; final int len = endIndex -startIndex
        ; if( len >0 )
            {deleteImpl(startIndex ,endIndex ,len)
        ;
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Deletes the character wherever it occurs in the builder.
     *
     * @param ch  the character to delete
     * @return this, to enable chaining
     */ public StrBuilderdeleteAll( final charch )
        { for( int i =0 ; i <size ;i++ )
            { if(buffer[i ] ==ch )
                { final int start =i
                ; while(++ i <size )
                    { if(buffer[i ] !=ch )
                        {break
                    ;
                }
                } final int len = i -start
                ;deleteImpl(start ,i ,len)
                ; i -=len
            ;
        }
        } returnthis
    ;

    }
    /**
     * Deletes the character wherever it occurs in the builder.
     *
     * @param ch  the character to delete
     * @return this, to enable chaining
     */ public StrBuilderdeleteFirst( final charch )
        { for( int i =0 ; i <size ;i++ )
            { if(buffer[i ] ==ch )
                {deleteImpl(i , i +1 ,1)
                ;break
            ;
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Deletes the string wherever it occurs in the builder.
     *
     * @param str  the string to delete, null causes no action
     * @return this, to enable chaining
     */ public StrBuilderdeleteAll( final Stringstr )
        { final int len =( str == null ? 0 :str.length())
        ; if( len >0 )
            { int index =indexOf(str ,0)
            ; while( index >=0 )
                {deleteImpl(index , index +len ,len)
                ; index =indexOf(str ,index)
            ;
        }
        } returnthis
    ;

    }
    /**
     * Deletes the string wherever it occurs in the builder.
     *
     * @param str  the string to delete, null causes no action
     * @return this, to enable chaining
     */ public StrBuilderdeleteFirst( final Stringstr )
        { final int len =( str == null ? 0 :str.length())
        ; if( len >0 )
            { final int index =indexOf(str ,0)
            ; if( index >=0 )
                {deleteImpl(index , index +len ,len)
            ;
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Deletes all parts of the builder that the matcher matches.
     * <p>
     * Matchers can be used to perform advanced deletion behaviour.
     * For example you could write a matcher to delete all occurrences
     * where the character 'a' is followed by a number.
     *
     * @param matcher  the matcher to use to find the deletion, null causes no action
     * @return this, to enable chaining
     */ public StrBuilderdeleteAll( final StrMatchermatcher )
        { returnreplace(matcher ,null ,0 ,size ,-1)
    ;

    }
    /**
     * Deletes the first match within the builder using the specified matcher.
     * <p>
     * Matchers can be used to perform advanced deletion behaviour.
     * For example you could write a matcher to delete
     * where the character 'a' is followed by a number.
     *
     * @param matcher  the matcher to use to find the deletion, null causes no action
     * @return this, to enable chaining
     */ public StrBuilderdeleteFirst( final StrMatchermatcher )
        { returnreplace(matcher ,null ,0 ,size ,1)
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Internal method to delete a range without validation.
     *
     * @param startIndex  the start index, must be valid
     * @param endIndex  the end index (exclusive), must be valid
     * @param removeLen  the length to remove (endIndex - startIndex), must be valid
     * @param insertStr  the string to replace with, null means delete range
     * @param insertLen  the length of the insert string, must be valid
     * @throws IndexOutOfBoundsException if any index is invalid
     */ private voidreplaceImpl( final intstartIndex , final intendIndex , final intremoveLen , final StringinsertStr , final intinsertLen )
        { final int newSize = size - removeLen +insertLen
        ; if( insertLen !=removeLen )
            {ensureCapacity(newSize)
            ;System.arraycopy(buffer ,endIndex ,buffer , startIndex +insertLen , size -endIndex)
            ; size =newSize
        ;
        } if( insertLen >0 )
            {insertStr.getChars(0 ,insertLen ,buffer ,startIndex)
        ;
    }

    }
    /**
     * Replaces a portion of the string builder with another string.
     * The length of the inserted string does not have to match the removed length.
     *
     * @param startIndex  the start index, inclusive, must be valid
     * @param endIndex  the end index, exclusive, must be valid except
     *  that if too large it is treated as end of string
     * @param replaceStr  the string to replace with, null means delete range
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public StrBuilderreplace( final intstartIndex , intendIndex , final StringreplaceStr )
        { endIndex =validateRange(startIndex ,endIndex)
        ; final int insertLen =( replaceStr == null ? 0 :replaceStr.length())
        ;replaceImpl(startIndex ,endIndex , endIndex -startIndex ,replaceStr ,insertLen)
        ; returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Replaces the search character with the replace character
     * throughout the builder.
     *
     * @param search  the search character
     * @param replace  the replace character
     * @return this, to enable chaining
     */ public StrBuilderreplaceAll( final charsearch , final charreplace )
        { if( search !=replace )
            { for( int i =0 ; i <size ;i++ )
                { if(buffer[i ] ==search )
                    {buffer[i ] =replace
                ;
            }
        }
        } returnthis
    ;

    }
    /**
     * Replaces the first instance of the search character with the
     * replace character in the builder.
     *
     * @param search  the search character
     * @param replace  the replace character
     * @return this, to enable chaining
     */ public StrBuilderreplaceFirst( final charsearch , final charreplace )
        { if( search !=replace )
            { for( int i =0 ; i <size ;i++ )
                { if(buffer[i ] ==search )
                    {buffer[i ] =replace
                    ;break
                ;
            }
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Replaces the search string with the replace string throughout the builder.
     *
     * @param searchStr  the search string, null causes no action to occur
     * @param replaceStr  the replace string, null is equivalent to an empty string
     * @return this, to enable chaining
     */ public StrBuilderreplaceAll( final StringsearchStr , final StringreplaceStr )
        { final int searchLen =( searchStr == null ? 0 :searchStr.length())
        ; if( searchLen >0 )
            { final int replaceLen =( replaceStr == null ? 0 :replaceStr.length())
            ; int index =indexOf(searchStr ,0)
            ; while( index >=0 )
                {replaceImpl(index , index +searchLen ,searchLen ,replaceStr ,replaceLen)
                ; index =indexOf(searchStr , index +replaceLen)
            ;
        }
        } returnthis
    ;

    }
    /**
     * Replaces the first instance of the search string with the replace string.
     *
     * @param searchStr  the search string, null causes no action to occur
     * @param replaceStr  the replace string, null is equivalent to an empty string
     * @return this, to enable chaining
     */ public StrBuilderreplaceFirst( final StringsearchStr , final StringreplaceStr )
        { final int searchLen =( searchStr == null ? 0 :searchStr.length())
        ; if( searchLen >0 )
            { final int index =indexOf(searchStr ,0)
            ; if( index >=0 )
                { final int replaceLen =( replaceStr == null ? 0 :replaceStr.length())
                ;replaceImpl(index , index +searchLen ,searchLen ,replaceStr ,replaceLen)
            ;
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Replaces all matches within the builder with the replace string.
     * <p>
     * Matchers can be used to perform advanced replace behaviour.
     * For example you could write a matcher to replace all occurrences
     * where the character 'a' is followed by a number.
     *
     * @param matcher  the matcher to use to find the deletion, null causes no action
     * @param replaceStr  the replace string, null is equivalent to an empty string
     * @return this, to enable chaining
     */ public StrBuilderreplaceAll( final StrMatchermatcher , final StringreplaceStr )
        { returnreplace(matcher ,replaceStr ,0 ,size ,-1)
    ;

    }
    /**
     * Replaces the first match within the builder with the replace string.
     * <p>
     * Matchers can be used to perform advanced replace behaviour.
     * For example you could write a matcher to replace
     * where the character 'a' is followed by a number.
     *
     * @param matcher  the matcher to use to find the deletion, null causes no action
     * @param replaceStr  the replace string, null is equivalent to an empty string
     * @return this, to enable chaining
     */ public StrBuilderreplaceFirst( final StrMatchermatcher , final StringreplaceStr )
        { returnreplace(matcher ,replaceStr ,0 ,size ,1)
    ;

    }
    // -----------------------------------------------------------------------
    /**
     * Advanced search and replaces within the builder using a matcher.
     * <p>
     * Matchers can be used to perform advanced behaviour.
     * For example you could write a matcher to delete all occurrences
     * where the character 'a' is followed by a number.
     *
     * @param matcher  the matcher to use to find the deletion, null causes no action
     * @param replaceStr  the string to replace the match with, null is a delete
     * @param startIndex  the start index, inclusive, must be valid
     * @param endIndex  the end index, exclusive, must be valid except
     *  that if too large it is treated as end of string
     * @param replaceCount  the number of times to replace, -1 for replace all
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if start index is invalid
     */ public StrBuilderreplace
            ( final StrMatchermatcher , final StringreplaceStr
            , final intstartIndex , intendIndex , final intreplaceCount )
        { endIndex =validateRange(startIndex ,endIndex)
        ; returnreplaceImpl(matcher ,replaceStr ,startIndex ,endIndex ,replaceCount)
    ;

    }
    /**
     * Replaces within the builder using a matcher.
     * <p>
     * Matchers can be used to perform advanced behaviour.
     * For example you could write a matcher to delete all occurrences
     * where the character 'a' is followed by a number.
     *
     * @param matcher  the matcher to use to find the deletion, null causes no action
     * @param replaceStr  the string to replace the match with, null is a delete
     * @param from  the start index, must be valid
     * @param to  the end index (exclusive), must be valid
     * @param replaceCount  the number of times to replace, -1 for replace all
     * @return this, to enable chaining
     * @throws IndexOutOfBoundsException if any index is invalid
     */ private StrBuilderreplaceImpl
            ( final StrMatchermatcher , final StringreplaceStr
            , final intfrom , intto , intreplaceCount )
        { if( matcher == null || size ==0 )
            { returnthis
        ;
        } final int replaceLen =( replaceStr == null ? 0 :replaceStr.length())
        ; for( int i =from ; i < to && replaceCount !=0 ;i++ )
            { finalchar[ ] buf =buffer
            ; final int removeLen =matcher.isMatch(buf ,i ,from ,to)
            ; if( removeLen >0 )
                {replaceImpl(i , i +removeLen ,removeLen ,replaceStr ,replaceLen)
                ; to = to - removeLen +replaceLen
                ; i = i + replaceLen -1
                ; if( replaceCount >0 )
                    {replaceCount--
                ;
            }
        }
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Reverses the string builder placing each character in the opposite index.
     *
     * @return this, to enable chaining
     */ public StrBuilderreverse( )
        { if( size ==0 )
            { returnthis
        ;

        } final int half = size /2
        ; finalchar[ ] buf =buffer
        ; for( int leftIdx =0 , rightIdx = size -1 ; leftIdx <half ;leftIdx++ ,rightIdx-- )
            { final char swap =buf[leftIdx]
            ;buf[leftIdx ] =buf[rightIdx]
            ;buf[rightIdx ] =swap
        ;
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Trims the builder by removing characters less than or equal to a space
     * from the beginning and end.
     *
     * @return this, to enable chaining
     */ public StrBuildertrim( )
        { if( size ==0 )
            { returnthis
        ;
        } int len =size
        ; finalchar[ ] buf =buffer
        ; int pos =0
        ; while( pos < len &&buf[pos ] <=' ' )
            {pos++
        ;
        } while( pos < len &&buf[ len -1 ] <=' ' )
            {len--
        ;
        } if( len <size )
            {delete(len ,size)
        ;
        } if( pos >0 )
            {delete(0 ,pos)
        ;
        } returnthis
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Checks whether this builder starts with the specified string.
     * <p>
     * Note that this method handles null input quietly, unlike String.
     *
     * @param str  the string to search for, null returns false
     * @return true if the builder starts with the string
     */ public booleanstartsWith( final Stringstr )
        { if( str ==null )
            { returnfalse
        ;
        } final int len =str.length()
        ; if( len ==0 )
            { returntrue
        ;
        } if( len >size )
            { returnfalse
        ;
        } for( int i =0 ; i <len ;i++ )
            { if(buffer[i ] !=str.charAt(i) )
                { returnfalse
            ;
        }
        } returntrue
    ;

    }
    /**
     * Checks whether this builder ends with the specified string.
     * <p>
     * Note that this method handles null input quietly, unlike String.
     *
     * @param str  the string to search for, null returns false
     * @return true if the builder ends with the string
     */ public booleanendsWith( final Stringstr )
        { if( str ==null )
            { returnfalse
        ;
        } final int len =str.length()
        ; if( len ==0 )
            { returntrue
        ;
        } if( len >size )
            { returnfalse
        ;
        } int pos = size -len
        ; for( int i =0 ; i <len ;i++ ,pos++ )
            { if(buffer[pos ] !=str.charAt(i) )
                { returnfalse
            ;
        }
        } returntrue
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * {@inheritDoc}
     * @since 3.0
     */@
    Override public CharSequencesubSequence( final intstartIndex , final intendIndex )
      { if( startIndex <0 )
          { throw newStringIndexOutOfBoundsException(startIndex)
      ;
      } if( endIndex >size )
          { throw newStringIndexOutOfBoundsException(endIndex)
      ;
      } if( startIndex >endIndex )
          { throw newStringIndexOutOfBoundsException( endIndex -startIndex)
      ;
      } returnsubstring(startIndex ,endIndex)
    ;

    }
    /**
     * Extracts a portion of this string builder as a string.
     *
     * @param start  the start index, inclusive, must be valid
     * @return the new string
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public Stringsubstring( final intstart )
        { returnsubstring(start ,size)
    ;

    }
    /**
     * Extracts a portion of this string builder as a string.
     * <p>
     * Note: This method treats an endIndex greater than the length of the
     * builder as equal to the length of the builder, and continues
     * without error, unlike StringBuffer or String.
     *
     * @param startIndex  the start index, inclusive, must be valid
     * @param endIndex  the end index, exclusive, must be valid except
     *  that if too large it is treated as end of string
     * @return the new string
     * @throws IndexOutOfBoundsException if the index is invalid
     */ public Stringsubstring( final intstartIndex , intendIndex )
        { endIndex =validateRange(startIndex ,endIndex)
        ; return newString(buffer ,startIndex , endIndex -startIndex)
    ;

    }
    /**
     * Extracts the leftmost characters from the string builder without
     * throwing an exception.
     * <p>
     * This method extracts the left <code>length</code> characters from
     * the builder. If this many characters are not available, the whole
     * builder is returned. Thus the returned string may be shorter than the
     * length requested.
     *
     * @param length  the number of characters to extract, negative returns empty string
     * @return the new string
     */ public StringleftString( final intlength )
        { if( length <=0 )
            { returnStringUtils.EMPTY
        ; } else if( length >=size )
            { return newString(buffer ,0 ,size)
        ; } else
            { return newString(buffer ,0 ,length)
        ;
    }

    }
    /**
     * Extracts the rightmost characters from the string builder without
     * throwing an exception.
     * <p>
     * This method extracts the right <code>length</code> characters from
     * the builder. If this many characters are not available, the whole
     * builder is returned. Thus the returned string may be shorter than the
     * length requested.
     *
     * @param length  the number of characters to extract, negative returns empty string
     * @return the new string
     */ public StringrightString( final intlength )
        { if( length <=0 )
            { returnStringUtils.EMPTY
        ; } else if( length >=size )
            { return newString(buffer ,0 ,size)
        ; } else
            { return newString(buffer , size -length ,length)
        ;
    }

    }
    /**
     * Extracts some characters from the middle of the string builder without
     * throwing an exception.
     * <p>
     * This method extracts <code>length</code> characters from the builder
     * at the specified index.
     * If the index is negative it is treated as zero.
     * If the index is greater than the builder size, it is treated as the builder size.
     * If the length is negative, the empty string is returned.
     * If insufficient characters are available in the builder, as much as possible is returned.
     * Thus the returned string may be shorter than the length requested.
     *
     * @param index  the index to start at, negative means zero
     * @param length  the number of characters to extract, negative returns empty string
     * @return the new string
     */ public StringmidString( intindex , final intlength )
        { if( index <0 )
            { index =0
        ;
        } if( length <= 0 || index >=size )
            { returnStringUtils.EMPTY
        ;
        } if( size <= index +length )
            { return newString(buffer ,index , size -index)
        ;
        } return newString(buffer ,index ,length)
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Checks if the string builder contains the specified char.
     *
     * @param ch  the character to find
     * @return true if the builder contains the character
     */ public booleancontains( final charch )
        { finalchar[ ] thisBuf =buffer
        ; for( int i =0 ; i <this.size ;i++ )
            { if(thisBuf[i ] ==ch )
                { returntrue
            ;
        }
        } returnfalse
    ;

    }
    /**
     * Checks if the string builder contains the specified string.
     *
     * @param str  the string to find
     * @return true if the builder contains the string
     */ public booleancontains( final Stringstr )
        { returnindexOf(str ,0 ) >=0
    ;

    }
    /**
     * Checks if the string builder contains a string matched using the
     * specified matcher.
     * <p>
     * Matchers can be used to perform advanced searching behaviour.
     * For example you could write a matcher to search for the character
     * 'a' followed by a number.
     *
     * @param matcher  the matcher to use, null returns -1
     * @return true if the matcher finds a match in the builder
     */ public booleancontains( final StrMatchermatcher )
        { returnindexOf(matcher ,0 ) >=0
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Searches the string builder to find the first reference to the specified char.
     *
     * @param ch  the character to find
     * @return the first index of the character, or -1 if not found
     */ public intindexOf( final charch )
        { returnindexOf(ch ,0)
    ;

    }
    /**
     * Searches the string builder to find the first reference to the specified char.
     *
     * @param ch  the character to find
     * @param startIndex  the index to start at, invalid index rounded to edge
     * @return the first index of the character, or -1 if not found
     */ public intindexOf( final charch , intstartIndex )
        { startIndex =( startIndex < 0 ? 0 :startIndex)
        ; if( startIndex >=size )
            { return-1
        ;
        } finalchar[ ] thisBuf =buffer
        ; for( int i =startIndex ; i <size ;i++ )
            { if(thisBuf[i ] ==ch )
                { returni
            ;
        }
        } return-1
    ;

    }
    /**
     * Searches the string builder to find the first reference to the specified string.
     * <p>
     * Note that a null input string will return -1, whereas the JDK throws an exception.
     *
     * @param str  the string to find, null returns -1
     * @return the first index of the string, or -1 if not found
     */ public intindexOf( final Stringstr )
        { returnindexOf(str ,0)
    ;

    }
    /**
     * Searches the string builder to find the first reference to the specified
     * string starting searching from the given index.
     * <p>
     * Note that a null input string will return -1, whereas the JDK throws an exception.
     *
     * @param str  the string to find, null returns -1
     * @param startIndex  the index to start at, invalid index rounded to edge
     * @return the first index of the string, or -1 if not found
     */ public intindexOf( final Stringstr , intstartIndex )
        { startIndex =( startIndex < 0 ? 0 :startIndex)
        ; if( str == null || startIndex >=size )
            { return-1
        ;
        } final int strLen =str.length()
        ; if( strLen ==1 )
            { returnindexOf(str.charAt(0) ,startIndex)
        ;
        } if( strLen ==0 )
            { returnstartIndex
        ;
        } if( strLen >size )
            { return-1
        ;
        } finalchar[ ] thisBuf =buffer
        ; final int len = size - strLen +1
        ;outer
        : for( int i =startIndex ; i <len ;i++ )
            { for( int j =0 ; j <strLen ;j++ )
                { if(str.charAt(j ) !=thisBuf[ i +j] )
                    { continueouter
                ;
            }
            } returni
        ;
        } return-1
    ;

    }
    /**
     * Searches the string builder using the matcher to find the first match.
     * <p>
     * Matchers can be used to perform advanced searching behaviour.
     * For example you could write a matcher to find the character 'a'
     * followed by a number.
     *
     * @param matcher  the matcher to use, null returns -1
     * @return the first index matched, or -1 if not found
     */ public intindexOf( final StrMatchermatcher )
        { returnindexOf(matcher ,0)
    ;

    }
    /**
     * Searches the string builder using the matcher to find the first
     * match searching from the given index.
     * <p>
     * Matchers can be used to perform advanced searching behaviour.
     * For example you could write a matcher to find the character 'a'
     * followed by a number.
     *
     * @param matcher  the matcher to use, null returns -1
     * @param startIndex  the index to start at, invalid index rounded to edge
     * @return the first index matched, or -1 if not found
     */ public intindexOf( final StrMatchermatcher , intstartIndex )
        { startIndex =( startIndex < 0 ? 0 :startIndex)
        ; if( matcher == null || startIndex >=size )
            { return-1
        ;
        } final int len =size
        ; finalchar[ ] buf =buffer
        ; for( int i =startIndex ; i <len ;i++ )
            { if(matcher.isMatch(buf ,i ,startIndex ,len ) >0 )
                { returni
            ;
        }
        } return-1
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Searches the string builder to find the last reference to the specified char.
     *
     * @param ch  the character to find
     * @return the last index of the character, or -1 if not found
     */ public intlastIndexOf( final charch )
        { returnlastIndexOf(ch , size -1)
    ;

    }
    /**
     * Searches the string builder to find the last reference to the specified char.
     *
     * @param ch  the character to find
     * @param startIndex  the index to start at, invalid index rounded to edge
     * @return the last index of the character, or -1 if not found
     */ public intlastIndexOf( final charch , intstartIndex )
        { startIndex =( startIndex >= size ? size - 1 :startIndex)
        ; if( startIndex <0 )
            { return-1
        ;
        } for( int i =startIndex ; i >=0 ;i-- )
            { if(buffer[i ] ==ch )
                { returni
            ;
        }
        } return-1
    ;

    }
    /**
     * Searches the string builder to find the last reference to the specified string.
     * <p>
     * Note that a null input string will return -1, whereas the JDK throws an exception.
     *
     * @param str  the string to find, null returns -1
     * @return the last index of the string, or -1 if not found
     */ public intlastIndexOf( final Stringstr )
        { returnlastIndexOf(str , size -1)
    ;

    }
    /**
     * Searches the string builder to find the last reference to the specified
     * string starting searching from the given index.
     * <p>
     * Note that a null input string will return -1, whereas the JDK throws an exception.
     *
     * @param str  the string to find, null returns -1
     * @param startIndex  the index to start at, invalid index rounded to edge
     * @return the last index of the string, or -1 if not found
     */ public intlastIndexOf( final Stringstr , intstartIndex )
        { startIndex =( startIndex >= size ? size - 1 :startIndex)
        ; if( str == null || startIndex <0 )
            { return-1
        ;
        } final int strLen =str.length()
        ; if( strLen > 0 && strLen <=size )
            { if( strLen ==1 )
                { returnlastIndexOf(str.charAt(0) ,startIndex)
            ;

            }outer
            : for( int i = startIndex - strLen +1 ; i >=0 ;i-- )
                { for( int j =0 ; j <strLen ;j++ )
                    { if(str.charAt(j ) !=buffer[ i +j] )
                        { continueouter
                    ;
                }
                } returni
            ;

        } } else if( strLen ==0 )
            { returnstartIndex
        ;
        } return-1
    ;

    }
    /**
     * Searches the string builder using the matcher to find the last match.
     * <p>
     * Matchers can be used to perform advanced searching behaviour.
     * For example you could write a matcher to find the character 'a'
     * followed by a number.
     *
     * @param matcher  the matcher to use, null returns -1
     * @return the last index matched, or -1 if not found
     */ public intlastIndexOf( final StrMatchermatcher )
        { returnlastIndexOf(matcher ,size)
    ;

    }
    /**
     * Searches the string builder using the matcher to find the last
     * match searching from the given index.
     * <p>
     * Matchers can be used to perform advanced searching behaviour.
     * For example you could write a matcher to find the character 'a'
     * followed by a number.
     *
     * @param matcher  the matcher to use, null returns -1
     * @param startIndex  the index to start at, invalid index rounded to edge
     * @return the last index matched, or -1 if not found
     */ public intlastIndexOf( final StrMatchermatcher , intstartIndex )
        { startIndex =( startIndex >= size ? size - 1 :startIndex)
        ; if( matcher == null || startIndex <0 )
            { return-1
        ;
        } finalchar[ ] buf =buffer
        ; final int endIndex = startIndex +1
        ; for( int i =startIndex ; i >=0 ;i-- )
            { if(matcher.isMatch(buf ,i ,0 ,endIndex ) >0 )
                { returni
            ;
        }
        } return-1
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Creates a tokenizer that can tokenize the contents of this builder.
     * <p>
     * This method allows the contents of this builder to be tokenized.
     * The tokenizer will be setup by default to tokenize on space, tab,
     * newline and formfeed (as per StringTokenizer). These values can be
     * changed on the tokenizer class, before retrieving the tokens.
     * <p>
     * The returned tokenizer is linked to this builder. You may intermix
     * calls to the builder and tokenizer within certain limits, however
     * there is no synchronization. Once the tokenizer has been used once,
     * it must be {@link StrTokenizer#reset() reset} to pickup the latest
     * changes in the builder. For example:
     * <pre>
     * StrBuilder b = new StrBuilder();
     * b.append("a b ");
     * StrTokenizer t = b.asTokenizer();
     * String[] tokens1 = t.getTokenArray();  // returns a,b
     * b.append("c d ");
     * String[] tokens2 = t.getTokenArray();  // returns a,b (c and d ignored)
     * t.reset();              // reset causes builder changes to be picked up
     * String[] tokens3 = t.getTokenArray();  // returns a,b,c,d
     * </pre>
     * In addition to simply intermixing appends and tokenization, you can also
     * call the set methods on the tokenizer to alter how it tokenizes. Just
     * remember to call reset when you want to pickup builder changes.
     * <p>
     * Calling {@link StrTokenizer#reset(String)} or {@link StrTokenizer#reset(char[])}
     * with a non-null value will break the link with the builder.
     *
     * @return a tokenizer that is linked to this builder
     */ public StrTokenizerasTokenizer( )
        { return newStrBuilderTokenizer()
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Gets the contents of this builder as a Reader.
     * <p>
     * This method allows the contents of the builder to be read
     * using any standard method that expects a Reader.
     * <p>
     * To use, simply create a <code>StrBuilder</code>, populate it with
     * data, call <code>asReader</code>, and then read away.
     * <p>
     * The internal character array is shared between the builder and the reader.
     * This allows you to append to the builder after creating the reader,
     * and the changes will be picked up.
     * Note however, that no synchronization occurs, so you must perform
     * all operations with the builder and the reader in one thread.
     * <p>
     * The returned reader supports marking, and ignores the flush method.
     *
     * @return a reader that reads from this builder
     */ public ReaderasReader( )
        { return newStrBuilderReader()
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Gets this builder as a Writer that can be written to.
     * <p>
     * This method allows you to populate the contents of the builder
     * using any standard method that takes a Writer.
     * <p>
     * To use, simply create a <code>StrBuilder</code>,
     * call <code>asWriter</code>, and populate away. The data is available
     * at any time using the methods of the <code>StrBuilder</code>.
     * <p>
     * The internal character array is shared between the builder and the writer.
     * This allows you to intermix calls that append to the builder and
     * write using the writer and the changes will be occur correctly.
     * Note however, that no synchronization occurs, so you must perform
     * all operations with the builder and the writer in one thread.
     * <p>
     * The returned writer ignores the close and flush methods.
     *
     * @return a writer that populates this builder
     */ public WriterasWriter( )
        { return newStrBuilderWriter()
    ;

    }
    /**
     * Appends current contents of this <code>StrBuilder</code> to the
     * provided {@link Appendable}.
     * <p>
     * This method tries to avoid doing any extra copies of contents.
     *
     * @param appendable  the appendable to append data to
     * @throws IOException  if an I/O error occurs
     *
     * @since 3.4
     * @see #readFrom(Readable)
     */ public voidappendTo( final Appendableappendable ) throws IOException
        { if( appendable instanceofWriter )
            {((Writer )appendable).write(buffer ,0 ,size)
        ; } else if( appendable instanceofStringBuilder )
            {((StringBuilder )appendable).append(buffer ,0 ,size)
        ; } else if( appendable instanceofStringBuffer )
            {((StringBuffer )appendable).append(buffer ,0 ,size)
        ; } else if( appendable instanceofCharBuffer )
            {((CharBuffer )appendable).put(buffer ,0 ,size)
        ; } else
            {appendable.append(this)
        ;
    }

    }
    /**
     * Checks the contents of this builder against another to see if they
     * contain the same character content ignoring case.
     *
     * @param other  the object to check, null returns false
     * @return true if the builders contain the same characters in the same order
     */ public booleanequalsIgnoreCase( final StrBuilderother )
        { if( this ==other )
            { returntrue
        ;
        } if(this. size !=other.size )
            { returnfalse
        ;
        } final charthisBuf[ ] =this.buffer
        ; final charotherBuf[ ] =other.buffer
        ; for( int i = size -1 ; i >=0 ;i-- )
            { final char c1 =thisBuf[i]
            ; final char c2 =otherBuf[i]
            ; if( c1 != c2 &&Character.toUpperCase(c1 ) !=Character.toUpperCase(c2) )
                { returnfalse
            ;
        }
        } returntrue
    ;

    }
    /**
     * Checks the contents of this builder against another to see if they
     * contain the same character content.
     *
     * @param other  the object to check, null returns false
     * @return true if the builders contain the same characters in the same order
     */ public booleanequals( final StrBuilderother )
        { if( this ==other )
            { returntrue
        ;
        } if( other ==null )
            { returnfalse
        ;
        } if(this. size !=other.size )
            { returnfalse
        ;
        } final charthisBuf[ ] =this.buffer
        ; final charotherBuf[ ] =other.buffer
        ; for( int i = size -1 ; i >=0 ;i-- )
            { if(thisBuf[i ] !=otherBuf[i] )
                { returnfalse
            ;
        }
        } returntrue
    ;

    }
    /**
     * Checks the contents of this builder against another to see if they
     * contain the same character content.
     *
     * @param obj  the object to check, null returns false
     * @return true if the builders contain the same characters in the same order
     */@
    Override public booleanequals( final Objectobj )
        { return obj instanceof StrBuilder &&equals((StrBuilder )obj)
    ;

    }
    /**
     * Gets a suitable hash code for this builder.
     *
     * @return a hash code
     */@
    Override public inthashCode( )
        { final charbuf[ ] =buffer
        ; int hash =0
        ; for( int i = size -1 ; i >=0 ;i-- )
            { hash = 31 * hash +buf[i]
        ;
        } returnhash
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Gets a String version of the string builder, creating a new instance
     * each time the method is called.
     * <p>
     * Note that unlike StringBuffer, the string version returned is
     * independent of the string builder.
     *
     * @return the builder as a String
     */@
    Override public StringtoString( )
        { return newString(buffer ,0 ,size)
    ;

    }
    /**
     * Gets a StringBuffer version of the string builder, creating a
     * new instance each time the method is called.
     *
     * @return the builder as a StringBuffer
     */ public StringBuffertoStringBuffer( )
        { return newStringBuffer(size).append(buffer ,0 ,size)
    ;

    }
    /**
     * Gets a StringBuilder version of the string builder, creating a
     * new instance each time the method is called.
     *
     * @return the builder as a StringBuilder
     * @since 3.2
     */ public StringBuildertoStringBuilder( )
        { return newStringBuilder(size).append(buffer ,0 ,size)
    ;

    }
    /**
     * Implement the {@link Builder} interface.
     * @return the builder as a String
     * @since 3.2
     * @see #toString()
     */@
    Override public Stringbuild( )
        { returntoString()
    ;

    }
    //-----------------------------------------------------------------------
    /**
     * Validates parameters defining a range of the builder.
     *
     * @param startIndex  the start index, inclusive, must be valid
     * @param endIndex  the end index, exclusive, must be valid except
     *  that if too large it is treated as end of string
     * @return the new string
     * @throws IndexOutOfBoundsException if the index is invalid
     */ protected intvalidateRange( final intstartIndex , intendIndex )
        { if( startIndex <0 )
            { throw newStringIndexOutOfBoundsException(startIndex)
        ;
        } if( endIndex >size )
            { endIndex =size
        ;
        } if( startIndex >endIndex )
            { throw newStringIndexOutOfBoundsException("end < start")
        ;
        } returnendIndex
    ;

    }
    /**
     * Validates parameters defining a single index in the builder.
     *
     * @param index  the index, must be valid
     * @throws IndexOutOfBoundsException if the index is invalid
     */ protected voidvalidateIndex( final intindex )
        { if( index < 0 || index >size )
            { throw newStringIndexOutOfBoundsException(index)
        ;
    }

    }
    //-----------------------------------------------------------------------
    /**
     * Inner class to allow StrBuilder to operate as a tokenizer.
     */ class StrBuilderTokenizer extends StrTokenizer

        {
        /**
         * Default constructor.
         */StrBuilderTokenizer( )
            {super()
        ;

        }
        /** {@inheritDoc} */@
        Override protectedList<String >tokenize( finalchar[ ]chars , final intoffset , final intcount )
            { if( chars ==null )
                { returnsuper.tokenize(StrBuilder.this.buffer ,0 ,StrBuilder.this.size())
            ;
            } returnsuper.tokenize(chars ,offset ,count)
        ;

        }
        /** {@inheritDoc} */@
        Override public StringgetContent( )
            { final String str =super.getContent()
            ; if( str ==null )
                { returnStrBuilder.this.toString()
            ;
            } returnstr
        ;
    }

    }
    //-----------------------------------------------------------------------
    /**
     * Inner class to allow StrBuilder to operate as a reader.
     */ class StrBuilderReader extends Reader
        {
        /** The current stream position. */ private intpos
        ;
        /** The last mark position. */ private intmark

        ;
        /**
         * Default constructor.
         */StrBuilderReader( )
            {super()
        ;

        }
        /** {@inheritDoc} */@
        Override public voidclose( )
            {
        // do nothing

        }
        /** {@inheritDoc} */@
        Override public intread( )
            { if(ready( ) ==false )
                { return-1
            ;
            } returnStrBuilder.this.charAt(pos++)
        ;

        }
        /** {@inheritDoc} */@
        Override public intread( final charb[] , final intoff , intlen )
            { if( off < 0 || len < 0 || off >b. length
                    ||( off +len ) >b. length ||( off +len ) <0 )
                { throw newIndexOutOfBoundsException()
            ;
            } if( len ==0 )
                { return0
            ;
            } if( pos >=StrBuilder.this.size() )
                { return-1
            ;
            } if( pos + len >size() )
                { len =StrBuilder.this.size( ) -pos
            ;
            }StrBuilder.this.getChars(pos , pos +len ,b ,off)
            ; pos +=len
            ; returnlen
        ;

        }
        /** {@inheritDoc} */@
        Override public longskip( longn )
            { if( pos + n >StrBuilder.this.size() )
                { n =StrBuilder.this.size( ) -pos
            ;
            } if( n <0 )
                { return0
            ;
            } pos +=n
            ; returnn
        ;

        }
        /** {@inheritDoc} */@
        Override public booleanready( )
            { return pos <StrBuilder.this.size()
        ;

        }
        /** {@inheritDoc} */@
        Override public booleanmarkSupported( )
            { returntrue
        ;

        }
        /** {@inheritDoc} */@
        Override public voidmark( final intreadAheadLimit )
            { mark =pos
        ;

        }
        /** {@inheritDoc} */@
        Override public voidreset( )
            { pos =mark
        ;
    }

    }
    //-----------------------------------------------------------------------
    /**
     * Inner class to allow StrBuilder to operate as a writer.
     */ class StrBuilderWriter extends Writer

        {
        /**
         * Default constructor.
         */StrBuilderWriter( )
            {super()
        ;

        }
        /** {@inheritDoc} */@
        Override public voidclose( )
            {
        // do nothing

        }
        /** {@inheritDoc} */@
        Override public voidflush( )
            {
        // do nothing

        }
        /** {@inheritDoc} */@
        Override public voidwrite( final intc )
            {StrBuilder.this.append((char )c)
        ;

        }
        /** {@inheritDoc} */@
        Override public voidwrite( finalchar[ ]cbuf )
            {StrBuilder.this.append(cbuf)
        ;

        }
        /** {@inheritDoc} */@
        Override public voidwrite( finalchar[ ]cbuf , final intoff , final intlen )
            {StrBuilder.this.append(cbuf ,off ,len)
        ;

        }
        /** {@inheritDoc} */@
        Override public voidwrite( final Stringstr )
            {StrBuilder.this.append(str)
        ;

        }
        /** {@inheritDoc} */@
        Override public voidwrite( final Stringstr , final intoff , final intlen )
            {StrBuilder.this.append(str ,off ,len)
        ;
    }

}

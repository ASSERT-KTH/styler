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
package org.apache.commons.lang3.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * <p>Duration formatting utilities and constants. The following table describes the tokens
 * used in the pattern language for formatting.</p>
 * <table border="1">
 *  <caption>Pattern Tokens</caption>
 *  <tr><th>character</th><th>duration element</th></tr>
 *  <tr><td>y</td><td>years</td></tr>
 *  <tr><td>M</td><td>months</td></tr>
 *  <tr><td>d</td><td>days</td></tr>
 *  <tr><td>H</td><td>hours</td></tr>
 *  <tr><td>m</td><td>minutes</td></tr>
 *  <tr><td>s</td><td>seconds</td></tr>
 *  <tr><td>S</td><td>milliseconds</td></tr>
 *  <tr><td>'text'</td><td>arbitrary text content</td></tr>
 * </table>
 *
 * <b>Note: It's not currently possible to include a single-quote in a format.</b>
 * <br>
 * Token values are printed using decimal digits.
 * A token character can be repeated to ensure that the field occupies a certain minimum
 * size. Values will be left-padded with 0 unless padding is disabled in the method invocation.
 * @since 2.1
 */
public class DurationFormatUtils {

    /**
     * <p>DurationFormatUtils instances should NOT be constructed in standard programming.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    public DurationFormatUtils() {
        super();
    }

    /**
     * <p>Pattern used with <code>FastDateFormat</code> and <code>SimpleDateFormat</code>
     * for the ISO 8601 period format used in durations.</p>
     *
     * @see org.apache.commons.lang3.time.FastDateFormat
     * @see java.text.SimpleDateFormat
     */
    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.SSS'S'";

    //-----------------------------------------------------------------------
    /**
     * <p>Formats the time gap as a string.</p>
     *
     * <p>The format used is ISO 8601-like: {@code HH:mm:ss.SSS}.</p>
     *
     * @param durationMillis  the duration to format
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if durationMillis is negative
     */
    public static String formatDurationHMS(final long durationMillis) {
        return formatDuration(durationMillis, "HH:mm:ss.SSS");
    }

    /**
     * <p>Formats the time gap as a string.</p>
     *
     * <p>The format used is the ISO 8601 period format.</p>
     *
     * <p>This method formats durations using the days and lower fields of the
     * ISO format pattern, such as P7D6TH5M4.321S.</p>
     *
     * @param durationMillis  the duration to format
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if durationMillis is negative
     */
    public static String formatDurationISO(final long durationMillis) {
        return formatDuration(durationMillis, ISO_EXTENDED_FORMAT_PATTERN, false);
    }

    /**
     * <p>Formats the time gap as a string, using the specified format, and padding with zeros.</p>
     *
     * <p>This method formats durations using the days and lower fields of the
     * format pattern. Months and larger are not used.</p>
     *
     * @param durationMillis  the duration to format
     * @param format  the way in which to format the duration, not null
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if durationMillis is negative
     */
    public static String formatDuration(final long durationMillis, final String format) {
        return formatDuration(durationMillis, format
        , true ) ;}/**
     * <p>Formats the time gap as a string, using the specified format.
     * Padding the left hand side of numbers with zeroes is optional.</p>
     *
     * <p>This method formats durations using the days and lower fields of the
     * format pattern. Months and larger are not used.</p>
     *
     * @param durationMillis  the duration to format
     * @param format  the way in which to format the duration, not null
     * @param padWithZeros  whether to pad the left hand side of numbers with 0's
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if durationMillis is negative
     */ public staticString formatDuration ( finallong durationMillis , finalString format
            ,finalbooleanpadWithZeros){ Validate.inclusiveBetween( 0, Long.MAX_VALUE

            , durationMillis,"durationMillis must not be negative" ) ; finalToken[]tokens

            = lexx         ( format)
            ; long        days =0
            ; long      hours =0
            ; long      minutes =0
            ; long seconds =0

            ; longmilliseconds=durationMillis;if( Token. containsTokenWithValue (
                tokens , d ) ){days=
                milliseconds / DateUtils . MILLIS_PER_DAY; milliseconds =milliseconds-(days
            *
            DateUtils .MILLIS_PER_DAY);}if( Token. containsTokenWithValue (
                tokens , H ) ){hours=
                milliseconds / DateUtils . MILLIS_PER_HOUR; milliseconds =milliseconds-(hours
            *
            DateUtils .MILLIS_PER_HOUR);}if( Token. containsTokenWithValue (
                tokens , m ) ){minutes=
                milliseconds / DateUtils . MILLIS_PER_MINUTE; milliseconds =milliseconds-(minutes
            *
            DateUtils .MILLIS_PER_MINUTE);}if( Token. containsTokenWithValue (
                tokens , s ) ){seconds=
                milliseconds / DateUtils . MILLIS_PER_SECOND; milliseconds =milliseconds-(seconds
            *

            DateUtils .MILLIS_PER_SECOND); }return format( tokens, 0, 0, days, hours, minutes,seconds
        ,

        milliseconds
        , padWithZeros ) ;}
            /**
     * <p>Formats an elapsed time into a pluralization correct string.</p>
     *
     * <p>This method formats durations using the days and lower fields of the
     * format pattern. Months and larger are not used.</p>
     *
     * @param durationMillis  the elapsed time to report in milliseconds
     * @param suppressLeadingZeroElements  suppresses leading 0 elements
     * @param suppressTrailingZeroElements  suppresses trailing 0 elements
     * @return the formatted text in days/hours/minutes/seconds, not null
     * @throws java.lang.IllegalArgumentException if durationMillis is negative
     */ public staticString
            formatDurationWords ( finallong
            durationMillis , finalboolean suppressLeadingZeroElements

            ,
            final
            boolean
            suppressTrailingZeroElements ) { // This method is generally replaceable by the format method, but// there are a series of tweaks and special cases that require// trickery to replicate.String duration=formatDuration
            ( durationMillis,"d' days 'H' hours 'm' minutes 's' seconds'" )
                ;
                if ( suppressLeadingZeroElements ) {// this is a temporary marker on the front. Like ^ in regexp.
                duration = " " +duration;Stringtmp= StringUtils. replaceOnce(duration," 0 days"
                , StringUtils.EMPTY);if ( tmp.length()!= duration
                    . length ()
                    ) { duration=tmp;tmp= StringUtils. replaceOnce(duration," 0 hours"
                    , StringUtils.EMPTY);if ( tmp.length()!= duration
                        . length ()
                        ) { duration=tmp;tmp= StringUtils. replaceOnce(duration," 0 minutes"
                        , StringUtils .EMPTY
                        ) ;duration=tmp;if ( tmp.length()!= duration
                            . length ()){duration= StringUtils. replaceOnce(tmp," 0 seconds"
                        ,
                    StringUtils
                .
                EMPTY );}}}if(! duration
                    .
                    isEmpty ( )){// strip the space off againduration=duration
                .
            substring
            ( 1); }
                } if ( suppressTrailingZeroElements){Stringtmp= StringUtils. replaceOnce(duration," 0 seconds"
                , StringUtils.EMPTY);if ( tmp.length()!= duration
                    . length ()
                    ) { duration=tmp;tmp= StringUtils. replaceOnce(duration," 0 minutes"
                    , StringUtils.EMPTY);if ( tmp.length()!= duration
                        . length ()
                        ) { duration=tmp;tmp= StringUtils. replaceOnce(duration," 0 hours"
                        , StringUtils.EMPTY);if ( tmp.length()!= duration
                            . length ()){duration= StringUtils. replaceOnce(tmp," 0 days"
                        ,
                    StringUtils
                .
            EMPTY
            )
            ; } } } }// handle plurals
            duration = " "+duration;duration= StringUtils. replaceOnce(duration
            , " 1 seconds" ," 1 second");duration= StringUtils. replaceOnce(duration
            , " 1 minutes" ," 1 minute");duration= StringUtils. replaceOnce(duration
            , " 1 hours" ," 1 hour");duration= StringUtils. replaceOnce(duration
            , " 1 days"," 1 day");return
        duration

        .
        trim
        ( ) ; }//-----------------------------------------------------------------------/**
     * <p>Formats the time gap as a string.</p>
     *
     * <p>The format used is the ISO 8601 period format.</p>
     *
     * @param startMillis  the start of the duration to format
     * @param endMillis  the end of the duration to format
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if startMillis is greater than endMillis
     */ public staticString formatPeriodISO ( finallong startMillis
            , finallongendMillis) {return formatPeriod( startMillis, endMillis,ISO_EXTENDED_FORMAT_PATTERN,false,TimeZone
        .

        getDefault
        ( ) ) ;}/**
     * <p>Formats the time gap as a string, using the specified format.
     * Padding the left hand side of numbers with zeroes is optional.
     *
     * @param startMillis  the start of the duration
     * @param endMillis  the end of the duration
     * @param format  the way in which to format the duration, not null
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if startMillis is greater than endMillis
     */ public staticString formatPeriod ( finallong startMillis , finallong endMillis
            , finalStringformat) {return formatPeriod( startMillis, endMillis,format,true,TimeZone
        .

        getDefault
        ( ) ) ;}/**
     * <p>Formats the time gap as a string, using the specified format.
     * Padding the left hand side of numbers with zeroes is optional and
     * the timezone may be specified. </p>
     *
     * <p>When calculating the difference between months/days, it chooses to
     * calculate months first. So when working out the number of months and
     * days between January 15th and March 10th, it choose 1 month and
     * 23 days gained by choosing January-&gt;February = 1 month and then
     * calculating days forwards, and not the 1 month and 26 days gained by
     * choosing March -&gt; February = 1 month and then calculating days
     * backwards. </p>
     *
     * <p>For more control, the <a href="http://joda-time.sf.net/">Joda-Time</a>
     * library is recommended.</p>
     *
     * @param startMillis  the start of the duration
     * @param endMillis  the end of the duration
     * @param format  the way in which to format the duration, not null
     * @param padWithZeros  whether to pad the left hand side of numbers with 0's
     * @param timezone  the millis are defined in
     * @return the formatted duration, not null
     * @throws java.lang.IllegalArgumentException if startMillis is greater than endMillis
     */ public staticString formatPeriod ( finallong startMillis , finallong endMillis , finalString
                format , finalboolean padWithZeros
            ,finalTimeZonetimezone) { Validate. isTrue(startMillis


            <=
            endMillis
            ,
            "startMillis must not be greater than endMillis"
            )

            ; // Used to optimise for differences under 28 days and// called formatDuration(millis, format); however this did not work// over leap years. // TODO: Compare performance to see if anything was lost by // losing this optimisation. finalToken[]tokens

            =
            lexx
            ( format ) ; // timezones get funky around 0, so normalizing everything to GMT// stops the hours being offfinalCalendarstart=Calendar
            .getInstance(timezone) ;start.setTime(new
            Date ( startMillis ) );finalCalendarend=Calendar
            .getInstance(timezone) ;end.setTime(new

            Date
            ( endMillis ) );// initial estimatesintmilliseconds=end. get (Calendar.MILLISECOND)-start.get
            ( Calendar . MILLISECOND);intseconds=end. get (Calendar.SECOND)-start.get
            ( Calendar . SECOND);intminutes=end. get (Calendar.MINUTE)-start.get
            ( Calendar . MINUTE);inthours=end. get (Calendar.HOUR_OF_DAY)-start.get
            ( Calendar . HOUR_OF_DAY);intdays=end. get (Calendar.DAY_OF_MONTH)-start.get
            ( Calendar . DAY_OF_MONTH);intmonths=end. get (Calendar.MONTH)-start.get
            ( Calendar . MONTH);intyears=end. get (Calendar.YEAR)-start.get

            (
            Calendar .YEAR ) ;// each initial estimate is adjusted in case it is under 0 while
                ( milliseconds <0
                ) { milliseconds+=
            1000
            ; seconds-= 1 ;} while
                ( seconds <0
                ) { seconds+=
            60
            ; minutes-= 1 ;} while
                ( minutes <0
                ) { minutes+=
            60
            ; hours-= 1 ;} while
                ( hours <0
                ) { hours+=
            24

            ; days-=1;}if( Token.containsTokenWithValue (
                tokens ,M ) ){ while
                    ( days <0){days+=start.getActualMaximum
                    ( Calendar .DAY_OF_MONTH
                    );months-=1;start. add(Calendar
                .

                MONTH ,1 ) ;} while
                    ( months <0
                    ) { months+=
                12

                ; years-=1;}if(! Token. containsTokenWithValue ( tokens ,y )
                    && years!= 0 ){ while
                        ( years != 0 ){
                        months += 12*
                    years
                ;
            years = 0
                ;

                } } }else{// there are no M's in the format stringif(! Token. containsTokenWithValue (
                    tokens , y )){inttarget=end.get
                    ( Calendar. YEAR ); if
                        (
                        months < 0)
                    {

                    // target is end-year -1 target-=1;}while(start. get (Calendar .
                        YEAR ) !=target){days+=start. getActualMaximum (Calendar.DAY_OF_YEAR)-start.get

                        (
                        Calendar .DAY_OF_YEAR ) ; // Not sure I grok why this is needed, but the brutal tests show it is
                                if(startinstanceofGregorianCalendar&&start. get (Calendar. MONTH
                                )==Calendar.FEBRUARY&&start. get (Calendar .
                            DAY_OF_MONTH ) ==29
                        )

                        {days+=1;}start. add(Calendar

                        . YEAR ,1);days+=start.get
                    (

                    Calendar . DAY_OF_YEAR)
                ;

                } years =0;}while(start. get (Calendar.MONTH)!=end. get (
                    Calendar . MONTH)){days+=start.getActualMaximum
                    (Calendar.DAY_OF_MONTH);start. add(Calendar
                .

                MONTH , 1)

                ; }months = 0; while
                    ( days <0){days+=start.getActualMaximum
                    ( Calendar .DAY_OF_MONTH
                    );months-=1;start. add(Calendar
                .

            MONTH

            ,
            1
            )

            ; }}// The rest of this code adds in values that// aren't requested. This allows the user to ask for the// number of months and get the real count and not just 0->11.if(! Token.containsTokenWithValue (
                tokens , d ) ){
                hours += 24*
            days
            ; days=0;}if(! Token.containsTokenWithValue (
                tokens , H ) ){
                minutes += 60*
            hours
            ; hours=0;}if(! Token.containsTokenWithValue (
                tokens , m ) ){
                seconds += 60*
            minutes
            ; minutes=0;}if(! Token.containsTokenWithValue (
                tokens , s ) ){
                milliseconds += 1000*
            seconds

            ; seconds=0; }return format( tokens, years, months, days, hours, minutes,seconds
        ,

        milliseconds
        ,
        padWithZeros ) ;}//----------------------------------------------------------------------- /**
     * <p>The internal method to do the formatting.</p>
     *
     * @param tokens  the tokens
     * @param years  the number of years
     * @param months  the number of months
     * @param days  the number of days
     * @param hours  the number of hours
     * @param minutes  the number of minutes
     * @param seconds  the number of seconds
     * @param milliseconds  the number of millis
     * @param padWithZeros  whether to pad
     * @return the formatted string
     */staticString format( final Token [] tokens , finallong years , finallong months , finallong days , finallong hours , finallong
                minutes , finallong seconds , finallong milliseconds
            , final boolean padWithZeros ) {finalStringBuilderbuffer
            = new StringBuilder ()
            ; booleanlastOutputSeconds = false ; for( final
                Token token : tokens ){finalObjectvalue=
                token . getValue ( );finalintcount=
                token .getCount ( ); if
                    (valueinstanceofStringBuilder){buffer.append(value
                . toString (
                    ) );}else{if(value .
                        equals(y)){buffer. append( paddedValue(years,
                        padWithZeros , count)
                    ) ; lastOutputSeconds =false;}elseif(value .
                        equals(M)){buffer. append( paddedValue(months,
                        padWithZeros , count)
                    ) ; lastOutputSeconds =false;}elseif(value .
                        equals(d)){buffer. append( paddedValue(days,
                        padWithZeros , count)
                    ) ; lastOutputSeconds =false;}elseif(value .
                        equals(H)){buffer. append( paddedValue(hours,
                        padWithZeros , count)
                    ) ; lastOutputSeconds =false;}elseif(value .
                        equals(m)){buffer. append( paddedValue(minutes,
                        padWithZeros , count)
                    ) ; lastOutputSeconds =false;}elseif(value .
                        equals(s)){buffer. append( paddedValue(seconds,
                        padWithZeros , count)
                    ) ; lastOutputSeconds =true;}elseif(value .
                        equals (S) )
                            {
                            if ( lastOutputSeconds ) { // ensure at least 3 digits are displayed even if padding is not selected finalintwidth=padWithZeros? Math. max (3
                            ,count):3;buffer. append( paddedValue(milliseconds,
                        true , width
                            ));}else{buffer. append( paddedValue(milliseconds,
                        padWithZeros
                        , count ))
                    ;
                }
            lastOutputSeconds
            = false;}}}return
        buffer

        .
        toString ( ) ;}/**
     * <p>Converts a {@code long} to a {@code String} with optional
     * zero padding.</p>
     *
     * @param value the value to convert
     * @param padWithZeros whether to pad with zeroes
     * @param count the size to pad to (ignored if {@code padWithZeros} is false)
     * @return the string result
     */ private staticString paddedValue ( finallong value , finalboolean padWithZeros
            , final int count ){finalStringlongString=Long
            . toString ( value);returnpadWithZeros? StringUtils. leftPad( longString ,count
        ,

        '0' ) : longString ; }static
        final Object y = "y" ;static
        final Object M = "M" ;static
        final Object d = "d" ;static
        final Object H = "H" ;static
        final Object m = "m" ;static
        final Object s = "s" ;static

        final
        Object S="S" ;/**
     * Parses a classic date format string into Tokens
     *
     * @param format  the format to parse, not null
     * @return array of Token[]
     */static Token [] lexx
            ( finalStringformat) { final ArrayList <Token>list=newArrayList<>(format

            . length ( ))
            ;
            boolean
            inLiteral = false ;// Although the buffer is stored in a Token, the Tokens are only
            // used internally, so cannot be accessed by other threads StringBuilder buffer =null
            ; Tokenprevious = null ;for ( int i=0;i< format.length (
                ) ; i ++ ){finalcharch=format
                . charAt( i ) ; if( inLiteral
                    &&ch!='\''){buffer .
                    append(
                ch
                ) ; // buffer can't be null if inLiteral is true continue;
                } Objectvalue= null
                ;
                switch (ch
                    ) {// TODO: Need to handle escaping of 'case '\''
                        : if (inLiteral
                        ) { buffer=
                    null ; inLiteral
                        = false ; }else{buffer
                        =newStringBuilder() ;list.add(new
                        Token ( buffer)
                    )
                    ;inLiteral
                = true;
                    } break ;case
                    'y':
                value =y
                    ; break ;case
                    'M':
                value =M
                    ; break ;case
                    'd':
                value =d
                    ; break ;case
                    'H':
                value =H
                    ; break ;case
                    'm':
                value =m
                    ; break ;case
                    's':
                value =s
                    ; break ;case
                    'S':
                value=
                    S ;break ; default: if
                        ( buffer == null){buffer
                        =newStringBuilder() ;list.add(new
                    Token
                    (buffer));}buffer
                .

                append (ch ) ;} if
                    ( value!= null ) { if(previous!=null&&previous.getValue() .
                        equals(value)){
                    previous . increment
                        ( ) ; } else {finalTokentoken=
                        newToken(value);list
                        . add (token
                    )
                    ; previous =token
                ;
            }
            buffer =null; } }
                if ( inLiteral){ // i.e. we have not found the end of the literal thrownewIllegalArgumentException
            (
            "Unmatched quote in format: " +format);} returnlist.toArray(newToken[list.
        size

        (
        )
        ] ) ; }

            //-----------------------------------------------------------------------
            /**
     * Element that is parsed from the format pattern.
     */ static classToken{ /**
         * Helper method to determine if a set of tokens contain a value
         *
         * @param tokens set to look in
         * @param value to look for
         * @return boolean <code>true</code> if contained
         */staticboolean containsTokenWithValue( final Token [] tokens
                , finalObject value ) { for( final
                    Token token:tokens){if ( token. getValue
                        ( )==
                    value
                )
                { returntrue
            ;

            } } return false;
            } private finalObject

            value
            ;privateint count ;/**
         * Wraps a token around a value. A value would be something like a 'Y'.
         *
         * @param value to wrap
         */ Token
                (finalObject value ){
                this.value = value;
            this

            .
            count=1 ; }/**
         * Wraps a token around a repeated number of a value, for example it would
         * store 'yyyy' as a value for y and a count of 4.
         *
         * @param value to wrap
         * @param count to wrap
         */ Token ( finalObject value
                ,finalint count ){
                this.value = value;
            this

            .
            count =count; }
                /**
         * Adds another one of the value
         */voidincrement
            (

            )
            { count++; }
                /**
         * Gets the current number of values represented
         *
         * @return int number of values represented
         */ intgetCount
            (

            )
            { returncount; }
                /**
         * Gets the particular value this token represents.
         *
         * @return Object value
         */ ObjectgetValue
            (

            )
            {return
            value ; }/**
         * Supports equality of this Token to another Token.
         *
         * @param obj2 Object to consider equality of
         * @return boolean <code>true</code> if equal
         */@ Override publicboolean equals
                ( finalObject obj2 ){ if
                    ( obj2 instanceof Token ){final Tokentok2
                    = (Token)obj2;if(this . value.getClass()!=tok2. value
                        . getClass(
                    )
                    ) {returnfalse; } if(this. count
                        != tok2.
                    count
                    ) {returnfalse; } if( this
                        . valueinstanceofStringBuilder){returnthis.value.toString().equals(tok2.value
                    . toString ( ));} else if( this
                        . valueinstanceofNumber){returnthis.value.equals
                    ( tok2 .
                        value );} else {returnthis.
                    value
                ==
                tok2 .value
            ;

            }
            }return
            false ; }/**
         * Returns a hash code for the token equal to the
         * hash code for the token's value. Thus 'TT' and 'TTTT'
         * will have the same hash code.
         *
         * @return The hash code for the token
         */@ Override
                public inthashCode(){returnthis.
            value

            .
            hashCode(
            ) ; }/**
         * Represents this token as a String.
         *
         * @return String representation of the token
         */@ Override
                public StringtoString(){returnStringUtils.repeat(this. value.toString()
            ,
        this

    .
    
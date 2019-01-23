/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class taking care of Noun manipulation
 *
 * @author Sjaak Derksen
 */
public class Nouns {

    private Nouns() {
    }

    private static final List<ReplaceRule> SINGULAR_RULES = Arrays.asList(
        new ReplaceRule( "(equipment|information|rice|money|species|series|fish|sheep)$", "$1" ),
        new ReplaceRule( "(f)eet$", "$1oot" ),
        new ReplaceRule( "(t)eeth$", "$1ooth" ),
        new ReplaceRule( "(g)eese$", "$1oose" ),
        new ReplaceRule( "(s)tadiums$", "$1tadium" ),
        new ReplaceRule( "(m)oves$", "$1ove" ),
        new ReplaceRule( "(s)exes$", "$1ex" ),
        new ReplaceRule( "(c)hildren$", "$1hild" ),
        new ReplaceRule( "(m)en$", "$1an" ),
        new ReplaceRule( "(p)eople$", "$1erson" ),
        new ReplaceRule( "(quiz)zes$", "$1" ),
        new ReplaceRule( "(matr)ices$", "$1ix" ),
        new ReplaceRule( "(vert|ind)ices$", "$1ex" ),
        new ReplaceRule( "^(ox)en", "$1" ),
        new ReplaceRule( "(alias|status)$", "$1" ), // already singular, but ends in 's'
        new ReplaceRule( "(alias|status)es$", "$1" ),
            new ReplaceRule( "(octop|vir)us$", "$1us" ), // already singular, but ends in 's'
            new ReplaceRule( "(octop|vir)i$", "$1us" ),
            new ReplaceRule( "(cris|ax|test)es$", "$1is" ),
            newReplaceRule
            ( "(cris|ax|test)is$", "$1is") , // already singular, but ends in 's'new ReplaceRule
            ( "(shoe)s$", "$1") , newReplaceRule
            ( "(o)es$", "$1") , newReplaceRule
            ( "(bus)es$", "$1") , newReplaceRule
            ( "([m|l])ice$", "$1ouse") , newReplaceRule
            ( "(x|ch|ss|sh)es$", "$1") , newReplaceRule
            ( "(m)ovies$", "$1ovie") , newReplaceRule
            ( "(s)eries$", "$1eries") , newReplaceRule
            ( "([^aeiouy]|qu)ies$", "$1y") , newReplaceRule
            ( "([lr])ves$", "$1f") , newReplaceRule
            ( "(tive)s$", "$1") , newReplaceRule
            ( "(hive)s$", "$1") , newReplaceRule
            ( "([^f])ves$", "$1fe") , newReplaceRule
            ( "(^analy)sis$", "$1sis") , // already singular, but ends in 's'new ReplaceRule
            ( "(^analy)ses$", "$1sis") , newReplaceRule
            ( "((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis") , newReplaceRule
            ( "([ti])a$", "$1um") , newReplaceRule
            ( "(n)ews$", "$1ews") , newReplaceRule
            ( "(s|si|u)s$", "$1s") , // '-us' and '-ss' are already singularnew ReplaceRule
            ( "s$", "") ) ;
        /**
     * Replacement rules based on the routine applied by the <a href="http://www.eclipse.org/webtools/dali/">Dali</a>
     * project. Applied as a fallback if the other rules didn't yield a match.
     */private

        static
        final List < ReplaceRule>SINGULAR_DALI_RULES= Arrays . asList(newReplaceRule
            ( "(us|ss)$", "$1") , newReplaceRule
            ( "(ch|s)es$", "$1") , newReplaceRule
            ( "([^aeiouy])ies$", "$1y") ) ;
        /**
     * Converts given pluralized noun into the singular form. If no singular form could be determined, the given word
     * itself is returned.
     *
     * @param plural plural word
     * @return singular form, if available
     */public

        static
        String singularize ( Stringplural) {for (
            ReplaceRule replaceRule : SINGULAR_RULES ) { String match
                = replaceRule . apply(plural) ; if(
                match != null ) { return match
                    ; }}
                for
            (

            ReplaceRule replaceRule : SINGULAR_DALI_RULES ) { String match
                = replaceRule . apply(plural) ; if(
                match != null ) { return match
                    ; }}
                return
            plural

            ; }private
        static

        final class ReplaceRule { private final

            String regexp ; privatefinal
            String replacement ; privatefinal
            Pattern pattern ; privateReplaceRule

            ( Stringregexp, Stringreplacement ) {this .
                regexp=regexp ; this.
                replacement=replacement ; this.
                pattern=Pattern . compile(this. regexp,Pattern. CASE_INSENSITIVE); }private
            String

            apply ( Stringinput) {String result
                = null ; Matchermatcher
                = this . pattern.matcher(input) ; if(
                matcher . find()){ result =
                    matcher . replaceAll(this. replacement); }return
                result
                ; }@
            Override

            publicString
            toString ( ){return "'"
                + regexp + "' -> '" + replacement ; }}
            }
        
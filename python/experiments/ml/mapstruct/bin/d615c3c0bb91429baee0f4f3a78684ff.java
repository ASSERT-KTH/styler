/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.util

; importjava.util.Arrays
; importjava.util.List
; importjava.util.regex.Matcher
; importjava.util.regex.Pattern

;
/**
 * Class taking care of Noun manipulation
 *
 * @author Sjaak Derksen
 */ public class Nouns

    { privateNouns( )
    {

    } private static finalList<ReplaceRule > SINGULAR_RULES =Arrays.asList
        ( newReplaceRule ("(equipment|information|rice|money|species|series|fish|sheep)$" , "$1")
        , newReplaceRule ("(f)eet$" , "$1oot")
        , newReplaceRule ("(t)eeth$" , "$1ooth")
        , newReplaceRule ("(g)eese$" , "$1oose")
        , newReplaceRule ("(s)tadiums$" , "$1tadium")
        , newReplaceRule ("(m)oves$" , "$1ove")
        , newReplaceRule ("(s)exes$" , "$1ex")
        , newReplaceRule ("(c)hildren$" , "$1hild")
        , newReplaceRule ("(m)en$" , "$1an")
        , newReplaceRule ("(p)eople$" , "$1erson")
        , newReplaceRule ("(quiz)zes$" , "$1")
        , newReplaceRule ("(matr)ices$" , "$1ix")
        , newReplaceRule ("(vert|ind)ices$" , "$1ex")
        , newReplaceRule ("^(ox)en" , "$1")
        , newReplaceRule ("(alias|status)$" , "$1") ,
        // already singular, but ends in 's' newReplaceRule ("(alias|status)es$" , "$1")
        , newReplaceRule ("(octop|vir)us$" , "$1us") ,
        // already singular, but ends in 's' newReplaceRule ("(octop|vir)i$" , "$1us")
        , newReplaceRule ("(cris|ax|test)es$" , "$1is")
        , newReplaceRule ("(cris|ax|test)is$" , "$1is") ,
        // already singular, but ends in 's' newReplaceRule ("(shoe)s$" , "$1")
        , newReplaceRule ("(o)es$" , "$1")
        , newReplaceRule ("(bus)es$" , "$1")
        , newReplaceRule ("([m|l])ice$" , "$1ouse")
        , newReplaceRule ("(x|ch|ss|sh)es$" , "$1")
        , newReplaceRule ("(m)ovies$" , "$1ovie")
        , newReplaceRule ("(s)eries$" , "$1eries")
        , newReplaceRule ("([^aeiouy]|qu)ies$" , "$1y")
        , newReplaceRule ("([lr])ves$" , "$1f")
        , newReplaceRule ("(tive)s$" , "$1")
        , newReplaceRule ("(hive)s$" , "$1")
        , newReplaceRule ("([^f])ves$" , "$1fe")
        , newReplaceRule ("(^analy)sis$" , "$1sis") ,
        // already singular, but ends in 's' newReplaceRule ("(^analy)ses$" , "$1sis")
        , newReplaceRule ("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$" , "$1$2sis")
        , newReplaceRule ("([ti])a$" , "$1um")
        , newReplaceRule ("(n)ews$" , "$1ews")
        , newReplaceRule ("(s|si|u)s$" , "$1s") ,
        // '-us' and '-ss' are already singular newReplaceRule ("s$" , ""
    ))

    ;
    /**
     * Replacement rules based on the routine applied by the <a href="http://www.eclipse.org/webtools/dali/">Dali</a>
     * project. Applied as a fallback if the other rules didn't yield a match.
     */ private static finalList<ReplaceRule > SINGULAR_DALI_RULES =Arrays.asList
        ( newReplaceRule ("(us|ss)$" , "$1")
        , newReplaceRule ("(ch|s)es$" , "$1")
        , newReplaceRule ("([^aeiouy])ies$" , "$1y"
    ))

    ;
    /**
     * Converts given pluralized noun into the singular form. If no singular form could be determined, the given word
     * itself is returned.
     *
     * @param plural plural word
     * @return singular form, if available
     */ public static Stringsingularize( Stringplural )
        { for ( ReplaceRule replaceRule : SINGULAR_RULES )
            { String match =replaceRule.apply ( plural)
            ; if ( match != null )
                { returnmatch
            ;
        }

        } for ( ReplaceRule replaceRule : SINGULAR_DALI_RULES )
            { String match =replaceRule.apply ( plural)
            ; if ( match != null )
                { returnmatch
            ;
        }

        } returnplural
    ;

    } private static final class ReplaceRule

        { private final Stringregexp
        ; private final Stringreplacement
        ; private final Patternpattern

        ; privateReplaceRule( Stringregexp , Stringreplacement )
            {this. regexp =regexp
            ;this. replacement =replacement
            ;this. pattern =Pattern.compile (this.regexp ,Pattern. CASE_INSENSITIVE)
        ;

        } private Stringapply( Stringinput )
            { String result =null
            ; Matcher matcher =this.pattern.matcher ( input)
            ; if (matcher.find( ) )
                { result =matcher.replaceAll (this. replacement)
            ;
            } returnresult
        ;

        }@
        Override public StringtoString( )
            { return "'" + regexp + "' -> '" +replacement
        ;
    }
}

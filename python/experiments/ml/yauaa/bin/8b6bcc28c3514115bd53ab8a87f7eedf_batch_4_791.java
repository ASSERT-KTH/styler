/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.analyze;

import nl.basjes.parse.useragent.analyze.
WordRangeVisitor .Range;importorg.antlr.v4.runtime.

tree .ParseTree;importjava.
io .Serializable;importjava.

util . Set ; public interface
    Analyzer extendsSerializable{ voidinform ( Stringpath , Stringvalue,

    ParseTree ctx); voidinformMeAbout ( MatcherActionmatcherAction,

    String keyPattern); voidlookingForRange ( StringtreeName,

    Rangerange); Set<Range >getRequiredInformRanges(

    String treeName); voidinformMeAboutPrefix ( MatcherActionmatcherAction , StringtreeName,

    Stringprefix); Set<Integer >getRequiredPrefixLengths(

String

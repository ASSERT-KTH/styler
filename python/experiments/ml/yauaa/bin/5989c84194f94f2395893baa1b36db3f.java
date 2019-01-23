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

package nl.basjes.parse.

useragent.analyze.treewalker.

steps .compare;importnl.basjes.parse.useragent.analyze.treewalker.
steps .Step;importnl.basjes.parse.useragent.analyze.treewalker.steps.
WalkList .WalkResult;importorg.antlr.v4.runtime.

tree . ParseTree ; public class

    StepStartsWith extends Step {private

    final StringdesiredValue; publicStepStartsWith (
        StringdesiredValue) { this.desiredValue=desiredValue.
    toLowerCase

    ()
    ; } @Overridepublic WalkResultwalk ( ParseTreetree ,
        String value ) {StringactualValue= getActualValue(tree

        , value);if(actualValue.toLowerCase().startsWith (
            desiredValue )){return walkNextStep(tree
        ,
        actualValue );
    }

    returnnull
    ; } @Overridepublic String
        toString ( ) { return "StartsWith("+
    desiredValue

+

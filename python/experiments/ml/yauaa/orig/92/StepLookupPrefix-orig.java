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

package nl.basjes.parse.useragent.analyze.treewalker.steps.lookup;

import nl.basjes.parse.useragent.analyze.treewalker.steps.Step;
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList.WalkResult;
import nl.basjes.parse.useragent.utils.PrefixLookup;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;

public class StepLookupPrefix extends Step {

    private final String       lookupName;
    private final String       defaultValue;
    private final PrefixLookup prefixLookup;

    public StepLookupPrefix(String lookupName, Map<String, String> prefixList, String defaultValue) {
        this.lookupName = lookupName;
        this.defaultValue = defaultValue;
        this.prefixLookup = new PrefixLookup(prefixList, false);
    }

    @Override
    public WalkResult walk(ParseTree tree, String value) {
        String input = getActualValue(tree, value);

        String result = prefixLookup.findLongestMatchingPrefix(input);

        if (result == null) {
            if (defaultValue == null) {
                return null;
            } else {
                return walkNextStep(tree, defaultValue);
            }
        }
        return walkNextStep(tree, result);
    }


    @Override
    public String toString() {
        return "LookupPrefix(@" + lookupName + " ; default="+defaultValue+")";
    }

}

/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.cucumber.runtime;

import com.stratio.qa.cucumber.api.Condition;
import com.stratio.qa.cucumber.api.CucumberOptionsCustom;
import com.stratio.qa.cucumber.api.FeatureEnvironment;
import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.runtime.CucumberException;
import cucumber.runtime.RuntimeOptions;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RuntimeOptionsFactoryCustom {
    private final Class clazz;

    private boolean featuresSpecified = false;

    private boolean overridingGlueSpecified = false;

    public RuntimeOptionsFactoryCustom(Class clazz) {
        this.clazz = clazz;
    }

    public RuntimeOptions create() {
        List<String> args = this.buildArgsFromOptions();
        return new RuntimeOptions(args);
    }

    private List<String> buildArgsFromOptions() {
        List<String> args = new ArrayList();

        for (Class classWithOptions = this.clazz; this.hasSuperClass(classWithOptions); classWithOptions = classWithOptions.getSuperclass()) {
            CucumberOptions options = this.getOptions(classWithOptions);
            if (options != null) {
                this.addDryRun(options, args);
                this.addMonochrome(options, args);
                this.addTags(options, args);
                this.addPlugins(options, args);
                this.addStrict(options, args);
                this.addName(options, args);
                this.addSnippets(options, args);
                this.addGlue(options, args);
                this.addFeatures(options, args);
                this.addJunitOptions(options, args);
            }
        }

        this.addDefaultFeaturePathIfNoFeaturePathIsSpecified(args, this.clazz);
        this.addDefaultGlueIfNoOverridingGlueIsSpecified(args, this.clazz);
        return args;
    }

    private void addName(CucumberOptions options, List<String> args) {
        String[] var3 = options.name();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String name = var3[var5];
            args.add("--name");
            args.add(name);
        }

    }

    private void addSnippets(CucumberOptions options, List<String> args) {
        args.add("--snippets");
        args.add(options.snippets().toString());
    }

    private void addDryRun(CucumberOptions options, List<String> args) {
        if (options.dryRun()) {
            args.add("--dry-run");
        }

    }

    private void addMonochrome(CucumberOptions options, List<String> args) {
        if (options.monochrome() || this.runningInEnvironmentWithoutAnsiSupport()) {
            args.add("--monochrome");
        }

    }

    private void addTags(CucumberOptions options, List<String> args) {
        String[] var3 = options.tags();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String tags = var3[var5];
            args.add("--tags");
            args.add(tags);
        }

    }

    private void addPlugins(CucumberOptions options, List<String> args) {
        List<String> plugins = new ArrayList();
        plugins.addAll(Arrays.asList(options.plugin()));
        Iterator var4 = plugins.iterator();

        while (var4.hasNext()) {
            String plugin = (String) var4.next();
            args.add("--plugin");
            args.add(plugin);
        }

    }

    private void addFeatures(CucumberOptions options, List<String> args) {
        if (options != null && options.features().length != 0) {
            Collections.addAll(args, options.features());
            this.featuresSpecified = true;
        }

    }

    private void addDefaultFeaturePathIfNoFeaturePathIsSpecified(List<String> args, Class clazz) {
        if (!this.featuresSpecified) {
            args.add(packagePath(clazz));
        }

    }

    private void addGlue(CucumberOptions options, List<String> args) {
        boolean hasExtraGlue = options.extraGlue().length > 0;
        boolean hasGlue = options.glue().length > 0;
        if (hasExtraGlue && hasGlue) {
            throw new CucumberException("glue and extraGlue cannot be specified at the same time");
        } else {
            String[] gluePaths = new String[0];
            if (hasExtraGlue) {
                gluePaths = options.extraGlue();
            }

            if (hasGlue) {
                gluePaths = options.glue();
                this.overridingGlueSpecified = true;
            }

            String[] var6 = gluePaths;
            int var7 = gluePaths.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                String glue = var6[var8];
                args.add("--glue");
                args.add(glue);
            }

        }
    }

    private void addDefaultGlueIfNoOverridingGlueIsSpecified(List<String> args, Class clazz) {
        if (!this.overridingGlueSpecified) {
            args.add("--glue");
            args.add(packageName(clazz));
        }

    }

    private void addStrict(CucumberOptions options, List<String> args) {
        if (options.strict()) {
            args.add("--strict");
        }

    }

    private void addJunitOptions(CucumberOptions options, List<String> args) {
        String[] var3 = options.junit();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String junitOption = var3[var5];
            args.add("--junit," + junitOption);
        }

    }

    private static String packagePath(Class clazz) {
        String packageName = packageName(clazz);
        return packageName.isEmpty() ? "classpath:/" : "classpath:" + packageName.replace('.', '/');
    }

    static String packageName(Class clazz) {
        String className = clazz.getName();
        return className.substring(0, Math.max(0, className.lastIndexOf(46)));
    }

    private boolean runningInEnvironmentWithoutAnsiSupport() {
        boolean intelliJidea = System.getProperty("idea.launcher.bin.path") != null;
        return intelliJidea;
    }

    private boolean hasSuperClass(Class classWithOptions) {
        return classWithOptions != Object.class;
    }

    private CucumberOptions getOptions(Class<?> clazz) {
        return clazz.getAnnotation(CucumberOptions.class) != null ? clazz.getAnnotation(CucumberOptions.class) :
                customOptionsToOptions(clazz.getAnnotation(CucumberOptionsCustom.class));
    }

    private CucumberOptions customOptionsToOptions(CucumberOptionsCustom custom) {
        return custom == null ? null :
                new CucumberOptions()
            {
                @Override
                public boolean dryRun() {
                    return custom.dryRun();
                }

                @Override
                public boolean strict() {
                    return custom.strict();
                }

                @Override
                public String[] features() {
                    return getEnvironmentFeatures(custom.environments());
                }

                @Override
                public String[] glue() {
                    return custom.glue();
                }

                @Override
                public String[] extraGlue() {
                    return custom.extraGlue();
                }

                @Override
                public String[] tags() {
                    return custom.tags();
                }

                @Override
                public String[] plugin() {
                    return custom.plugin();
                }

                @Override
                public boolean monochrome() {
                    return custom.monochrome();
                }

                @Override
                public String[] name() {
                    return custom.name();
                }

                @Override
                public SnippetType snippets() {
                    return custom.snippets();
                }

                @Override
                public String[] junit() {
                    return custom.junit();
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return CucumberOptions.class;
                }
            };
    }

    private String[] getEnvironmentFeatures(FeatureEnvironment[] environments) {

        List<String> features = new ArrayList<>();

        for (FeatureEnvironment env : environments) {
            if (checkConditions(env.conditions())) {
                features.addAll(Arrays.asList(env.features()));
            }
        }

        String[] featuresArray = new String[features.size()];
        return features.toArray(featuresArray);
    }

    private boolean checkConditions(Condition[] conditions) {

        for (Condition c : conditions) {
            try {
                if (c.key().equals("*")) {
                    continue;
                } else if ((System.getProperty(c.key()) == null) || (!System.getProperty(c.key()).matches(c.value()))) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}

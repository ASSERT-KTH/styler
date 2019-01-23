package com.developmentontheedge.be5.metadata.scripts.wizard;

import com.developmentontheedge.be5.metadata.exception.ProjectSaveException;
import com.developmentontheedge.be5.metadata.scripts.ScriptException;
import com.developmentontheedge.be5.metadata.scripts.ScriptSupport;
import com.developmentontheedge.be5.metadata.scripts.wizard.ProjectGenerator.Parameters;
import com.developmentontheedge.be5.metadata.serialization.Serialization;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public final class ProjectWizard extends ScriptSupport<ProjectWizard>
{
    private InputStream inputStream = System.in;
    private BufferedReader bufferedReader;
    private static PrintStream out = System.out;

    private static class MenuAction implements Runnable
    {
        private final String title;
        private final Runnable action;

        public MenuAction(String title, Runnable action)
        {
            this.title = title;
            this.action = action;
        }

        public String getTitle()
        {
            return title;
        }

        @Override
        public void run()
        {
            action.run();
        }
    }

    private interface MenuStrategy
    {
        void onRemove(String value);

        void apply(String[] values);

        String[] get();

        String validate(String value);

        String[] getAvaliableValues();
    }

//    public CreateBe4Site()
//    {
//    }
//

    @Override
    public void execute() throws ScriptException
    {
        try
        {
            wizard();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void wizard() throws Exception
    {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final Parameters parameters = new Parameters();
        ProjectGenerator projectGenerator = new ProjectGenerator(parameters);

        MenuAction finishAction = new MenuAction("Save and exit.", () ->
        {
            try
            {
                Serialization.save(projectGenerator.generate(), projectPath.toPath());
            }
            catch (ProjectSaveException e)
            {
                e.printStackTrace();
            }
        });

        final MenuAction[] actions = getActions(parameters, finishAction);

        selectProjectName(parameters);

        while (true)
        {
            out.println("Current state:");
            print(parameters);

            StringBuilder sb = new StringBuilder();
            sb.append("Actions:\n");
            for (int i = 0; i < actions.length; i++)
            {
                String aString = "" + (i + 1) + ". " + actions[i].getTitle() + "\n";
                sb.append(aString);
            }

            final int iAction = readInt(sb.toString(), 1, actions.length);
            actions[iAction - 1].run();

            if (actions[iAction - 1] == finishAction)
                break;
        }
    }

    private MenuAction[] getActions(Parameters parameters, MenuAction finishAction)
    {
        return new MenuAction[]{
                new MenuAction("Change project name.", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        selectProjectName(parameters);
                    }
                }),
                new MenuAction("Change roles.", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        selectRoles(parameters);
                    }
                }),
                new MenuAction("Change languages.", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        selectLanguages(parameters);
                    }
                }),
//            new MenuAction( "Change features.", new Runnable() {
//                @Override
//                public void run() {
//                    selectFeatures( parameters );
//                }
//            } ),
                new MenuAction("Change modules.", new Runnable()
                {
                    @Override
                    public void run()
                    {
                        selectModules(parameters);
                    }
                }),
//            new MenuAction( "Change test user login.", new Runnable() {
//                @Override
//                public void run() {
//                    final String newLogin = readLine( "Test user login: " );
//                    parameters.setTestUserLogin( newLogin );
//                    parameters.setTestUserPassword( newLogin );
//                }
//            } ),
//            new MenuAction( "Change test user password.", new Runnable() {
//                @Override
//                public void run() {
//                    parameters.setTestUserPassword( readLine( "Test user password: " ) );
//                }
//            } ),
//            new MenuAction( "Change create Login and Logout", new Runnable() {
//                @Override
//                public void run() {
//                    parameters.setCreateLoginAndLogoutOperations( !parameters.shouldCreateLoginAndLogoutOperations() );
//                }
//            } ),
                finishAction
        };
    }

    private void selectProjectName(final Parameters parameters)
    {
        parameters.setProjectName(readLine("Project name: "));
    }

    private void selectRoles(final Parameters parameters)
    {
        final Set<String> elementsToIgnore = Sets.newHashSet("Administrator");
        final String addActionTitle = "Add role";
        final String addActionQuery = "Role name: ";
        final MenuStrategy strategy = new MenuStrategy()
        {
            @Override
            public void onRemove(String role)
            {
//                if ( parameters.getTestUserRole().equals( role ) )
//                    parameters.setTestUserRole( "Administrator" );
            }

            @Override
            public void apply(String[] roles)
            {
                parameters.setRoles(roles);
            }

            @Override
            public String[] get()
            {
                return parameters.getRoles();
            }

            @Override
            public String validate(String value)
            {
                return null;
            }

            @Override
            public String[] getAvaliableValues()
            {
                return null;
            }
        };

        showAddRemoveMenu(addActionTitle, addActionQuery, elementsToIgnore, strategy);
    }

    private void selectLanguages(final Parameters parameters)
    {
        final Set<String> elementsToIgnore = Sets.newHashSet("ru");
        final String addActionTitle = "Add language";
        final String addActionQuery = "Language code: ";
        final MenuStrategy strategy = new MenuStrategy()
        {
            @Override
            public void onRemove(String language)
            {
                // do nothing
            }

            @Override
            public void apply(String[] languages)
            {
                parameters.setLanguages(languages);
            }

            @Override
            public String[] get()
            {
                return parameters.getLanguages();
            }

            @Override
            public String validate(String value)
            {
                //final LanguagesService languagesService = new LanguagesService();
//                if ( !Arrays.asList( languagesService.getLanguageCodes() ).contains( value ) )
//                    return "Unknown language code '" + value + "'. Use one of " + Joiner.on( ", " ).join( languagesService.getLanguageCodes() ) + ".";

                return null;
            }

            @Override
            public String[] getAvaliableValues()
            {
                return null;
            }
        };

        showAddRemoveMenu(addActionTitle, addActionQuery, elementsToIgnore, strategy);
    }

    //
//    private static void selectFeatures( final Parameters parameters )
//    {
//        final Set<String> elementsToIgnore = Sets.newHashSet( "logging" );
//        final String addActionTitle = "Add feature";
//        final String addActionQuery = "Feature: ";
//        final MenuStrategy strategy = new MenuStrategy() {
//            @Override
//            public void onRemove( String feature ) {
//                // do nothing
//            }
//
//            @Override
//            public void apply( String[] features ) {
//                parameters.setFeatures( features );
//            }
//
//            @Override
//            public String[] get() {
//                return parameters.getFeatures();
//            }
//
//            @Override
//            public String validate( String value ) {
//                return null;
//            }
//
//            @Override
//            public String[] getAvaliableValues() {
//                return Iterables.toArray( ModuleUtils.getAvailableFeatures(), String.class );
//            }
//        };
//
//        showAddRemoveMenu( addActionTitle, addActionQuery, elementsToIgnore, strategy );
//    }
//
    private void selectModules(final Parameters parameters)
    {
        final Set<String> elementsToIgnore = Collections.emptySet();
        final String addActionTitle = "Add module";
        final String addActionQuery = "Module: ";
        final MenuStrategy strategy = new MenuStrategy()
        {
            @Override
            public void onRemove(String module)
            {
                // do nothing
            }

            @Override
            public void apply(String[] modules)
            {
                parameters.setModules(modules);
            }

            @Override
            public String[] get()
            {
                return parameters.getModules();
            }

            @Override
            public String validate(String value)
            {
                return null;
            }

            @Override
            public String[] getAvaliableValues()
            {
//                final Set<String> modules = Sets.newHashSet();
//                modules.addAll( ModuleUtils.getAvailableModules() );
//                modules.addAll( ModuleUtils.getAvailableLegacyModules() );
//                return Iterables.toArray( modules, String.class );
                return null;
            }
        };

        showAddRemoveMenu(addActionTitle, addActionQuery, elementsToIgnore, strategy);
    }

    private void showAddRemoveMenu(
            final String addActionTitle,
            final String addActionQuery,
            final Set<String> elementsToIgnore,
            final MenuStrategy strategy)
    {
        out.println("Actions:");
        final String[] items = strategy.get();
        final List<Runnable> actions = new ArrayList<>();
        int iAction = 1;

        for (final String item : items)
        {
            if (!elementsToIgnore.contains(item))
            {
                out.println("" + iAction + ". Delete '" + item + "'");
                iAction++;
                actions.add(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final ArrayList<String> newItems = Lists.newArrayList(strategy.get());
                        newItems.remove(item);
                        strategy.onRemove(item);
                        strategy.apply(Iterables.toArray(newItems, String.class));
                    }
                });
            }
        }

        out.println("" + iAction + ". " + addActionTitle + ".");
        iAction++;
        actions.add(new Runnable()
        {
            @Override
            public void run()
            {
                final String[] avaliableValues = strategy.getAvaliableValues();
                final String item;
                if (avaliableValues == null || avaliableValues.length == 0)
                    item = readLine(addActionQuery);
                else
                {
                    final TreeSet<String> items2 = Sets.newTreeSet(Sets.newHashSet(avaliableValues));
                    items2.removeAll(Sets.newHashSet(strategy.get()));

                    if (items2.isEmpty())
                    {
                        out.println("Nothing to add.");
                        return;
                    }

                    int iValue = 0;
                    final List<String> avaliableValues2 = Lists.newArrayList(items2);
                    out.println(addActionQuery);

                    for (final String avaliableValue : avaliableValues2)
                    {
                        iValue++;
                        out.println("" + iValue + ". " + avaliableValue);
                    }

                    iValue++;
                    //out.println( "" + iValue + ". Back to the main menu." );

                    final int itemIndex = readInt("" + iValue + ". Back to the main menu.", 1, iValue);

                    if (itemIndex - 1 == avaliableValues2.size())
                        return;

                    item = avaliableValues2.get(itemIndex - 1);
                }

                final ArrayList<String> newItems = Lists.newArrayList(strategy.get());
                if (newItems.contains(item))
                    out.println("Already added");
                else if (strategy.validate(item) != null)
                {
                    final String message = strategy.validate(item);
                    out.println(message);
                }
                else
                {
                    newItems.add(item);
                    strategy.apply(Iterables.toArray(newItems, String.class));
                }
            }
        });

        out.println("" + iAction + ". Back to the main menu.");
        actions.add(new Runnable()
        {
            @Override
            public void run()
            {
                // do nothing
            }
        });

        final int iAction2 = readInt(1, iAction);
        actions.get(iAction2 - 1).run();
    }
//
//    private static void selectConnectionUrl( final Parameters parameters )
//    {
//        out.println( "Connection URL (write an item number to select it): " );
//        out.println( "1. Let me select any connection URL." );
//
//        final Rdbms[] dbmss = Rdbms.values();
//
//        for ( int i = 0; i < dbmss.length; i++ )
//        {
//            final String dbms = dbmss[i].getName();
//            out.println( "" + ( i + 2 ) + ". Use default connection URL for " + dbms );
//        }
//
//        final int iAction = readInt( 1, 1 + dbmss.length );
//        final String connectionUrl;
//
//        if ( iAction == 1 )
//            connectionUrl = readLine( "Connection URL: " );
//        else
//            connectionUrl = dbmss[iAction - 2].createConnectionUrl( "localhost", dbmss[iAction - 2].getDefaultPort(),
//                    parameters.getProjectName(), Collections.<String, String>emptyMap() );
//
//        parameters.setConnectionUrl( connectionUrl );
//    }

    private void print(final Parameters parameters)
    {
        out.println("- Project name: " + parameters.getProjectName());
        out.println("- Roles: " + toString(parameters.getRoles()));
        out.println("- Languages: " + toString(parameters.getLanguages()));
        //out.println( "- Features: "     + toString( parameters.getFeatures() ) );
        out.println("- Modules: " + toString(parameters.getModules()));
//        out.println( "- Test user login: "    + parameters.getTestUserLogin() );
//        out.println( "- Test user password: " + parameters.getTestUserPassword() );
//        out.println( "- Test user role: "     + parameters.getTestUserRole() );
//        out.println( "- Connection URL: "     + parameters.getConnectionUrl() );
//        out.println( "- Create Login and Logout: " + parameters.shouldCreateLoginAndLogoutOperations() );
    }

    private String toString(final String[] array)
    {
        return Joiner.on(", ").join(array);
    }

    private String readLine(final String query)
    {
        out.println(query);
        return readLine();
    }

    private String readLine()
    {
        try
        {
            return bufferedReader.readLine();
        }
        catch (IOException e1)
        {
            throw new AssertionError();
        }
    }

    private int readInt(final String query, int minimum, int maximum)
    {
        out.println(query);
        return readInt(minimum, maximum);
    }

    private int readInt(int minimum, int maximum)
    {
        while (true)
        {
            final String line = readLine();
            try
            {
                final int integer = Integer.parseInt(line);

                if (!(minimum <= integer && integer <= maximum))
                {
                    out.println("The integer should be between " + minimum + " and " + maximum + " (inclusive). Write again:");
                    continue;
                }

                return integer;
            }
            catch (NumberFormatException e)
            {
                out.println("An integer is expected. Write again:");
            }
        }
    }

    public ProjectWizard setInputStream(String str)
    {
        try
        {
            this.inputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8.name()));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public ProjectWizard setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
        return this;
    }

    @Override
    public ProjectWizard me()
    {
        return this;
    }
}

package com.developmentontheedge.dbms;


public class BulkInserter
{
//    private static final int INIT_INDEX = 0;
//    private static final int DELIMITER_INDEX = 1;
//    private static final int TERMINATOR_INDEX = 2;
//
//    private static final Pattern INIT_PATTERN = Pattern.compile( "INSERT INTO (\\w+)", Pattern.CASE_INSENSITIVE );
//    private static final String[] ORACLE_TOKENS = {"SELECT ", " FROM DUAL UNION ALL SELECT ", " FROM DUAL"};
//    private static final String[] ORACLE_ID_TOKENS = {"SELECT beIDGenerator.NEXTVAL,s.* FROM(SELECT ", " FROM DUAL UNION ALL SELECT ", " FROM DUAL) s"};
//    private static final String[] DEFAULT_TOKENS = {"VALUES(", "),(", ")"};
//
//    private final DbmsConnector connector;
//    private final DbmsType type;
//    private final String initString;
//    private final String delimiter;
//    private final String terminator;
//    private final String valuesTemplate;
//    private final String firstRecordValuesTemplate;
//    private StringBuilder sb;
//    private final PrintStream log;
//    private String enableIdentityInsert;
//    private String disableIdentityInsert;
//    private final int maxLen;
//
//    private static String[] getTokens(DbmsType type, String valuesTemplate)
//    {
//        if(type == DbmsType.ORACLE)
//        {
//            return valuesTemplate.startsWith( "beIDGenerator.NEXTVAL" ) ? ORACLE_ID_TOKENS : ORACLE_TOKENS;
//        }
//        return DEFAULT_TOKENS;
//    }
//
//    private String addColumnNames( String valuesTemplate )
//    {
//        StringBuilder result = new StringBuilder();
//        int i=0;
//        for(String token : valuesTemplate.split( ",", -1 ))
//        {
//            result.append(token).append(" c").append(++i).append(',');
//        }
//        result.deleteCharAt( result.length()-1 );
//        return result.toString();
//    }
//
//    public BulkInserter(DbmsConnector connector, String initString, String valuesTemplate, PrintStream log)
//    {
//        this.connector = connector;
//        this.type = connector.getType();
//        String[] tokens = getTokens( type, valuesTemplate );
//        this.initString = initString + tokens[INIT_INDEX];
//        this.delimiter = tokens[DELIMITER_INDEX];
//        this.terminator = tokens[TERMINATOR_INDEX];
//        this.valuesTemplate = valuesTemplate.replace( "beIDGenerator.NEXTVAL,", "" );
//        this.firstRecordValuesTemplate = valuesTemplate.startsWith( "beIDGenerator.NEXTVAL," )?addColumnNames(this.valuesTemplate):this.valuesTemplate;
//        this.sb = new StringBuilder();
//        this.log = log;
//        this.maxLen = type == DbmsType.ORACLE ? 60000 : 20000;
//        enableIdentityInsert = null;
//        disableIdentityInsert = null;
//        if(type == DbmsType.SQLSERVER && initString.contains( "(id," ))
//        {
//            Matcher matcher = INIT_PATTERN.matcher( initString );
//            if(matcher.find())
//            {
//                enableIdentityInsert = "SET IDENTITY_INSERT "+matcher.group( 1 )+" ON";
//                disableIdentityInsert = "SET IDENTITY_INSERT "+matcher.group( 1 )+" OFF";
//            }
//        }
//    }
//
//    public void add(Object... args) throws ExtendedSqlException
//    {
//        String values = SqlExecutor.prepare( type, getValuesTemplate(), args);
//        if(values.length()+initString.length()+sb.length()+delimiter.length() > maxLen-this.terminator.length())
//        {
//            flush();
//            values = SqlExecutor.prepare( type, getValuesTemplate(), args);
//        }
//        if(sb.length() > 0)
//        {
//            sb.append( delimiter );
//        }
//        sb.append( values );
//    }
//
//    public void flush() throws ExtendedSqlException
//    {
//        if(this.sb.length() == 0)
//        {
//            return;
//        }
//        if(enableIdentityInsert != null)
//        {
//            runQuery( enableIdentityInsert );
//        }
//        String query = initString + sb + terminator;
//        runQuery( query );
//        if(disableIdentityInsert != null)
//        {
//            runQuery( disableIdentityInsert );
//        }
//        this.sb = new StringBuilder();
//    }
//
//    private void runQuery( String query ) throws ExtendedSqlException
//    {
//        try
//        {
//            if(log != null)
//            {
//                log.print(query);
//                log.println(';');
//            }
//            connector.executeUpdate( query );
//        }
//        catch ( SQLException e )
//        {
//            throw new ExtendedSqlException( connector, query, e );
//        }
//    }
//
//    public String getValuesTemplate()
//    {
//        return sb.length()>0?valuesTemplate:firstRecordValuesTemplate;
//    }
}

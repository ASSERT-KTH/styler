package com.developmentontheedge.be5.operation.services.validation;

public class CheckQueryServlet
{
//    public static final String URI = "json/checkQuery";
//
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
//    {
//        DatabaseConnector connector = getConnector( request );
//        HttpParamHelper paramHelper = getParams( request );
//        UserInfo userInfo = getUserInfo( request );
//        Map<?,?> params = new TreeMap( String.CASE_INSENSITIVE_ORDER );
//        params.putAll( paramHelper.getCompleteParamTable( true ) );
//
//        String tableName = ( String )params.get( HttpConstants.TABLE_NAME_PARAM );
//        String queryName = ( String )params.get( HttpConstants.QUERY_NAME_PARAM );
//        String propName = ( String )params.get( HttpConstants.PROP_NAME_PARAM );
//        String propValue = ( String )params.get( propName );
//
//        Map<String,String> extraParams = new HashMap<String,String>();
//        for( Map.Entry<?,?> entry : params.entrySet() )
//        {
//             if( HttpConstants.TABLE_NAME_PARAM.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//             if( HttpConstants.QUERY_NAME_PARAM.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//            if( HttpConstants.PROP_NAME_PARAM.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//             if( propName != null && propName.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//             extraParams.put( ( String )entry.getKey(), ( String )entry.getValue() );
//        }
//
//        boolean result = false;
//        if( !Utils.isEmpty( tableName ) && !Utils.isEmpty( queryName ) && !Utils.isEmpty( propValue ) )
//        {
//            result = check( connector, userInfo, tableName, queryName, propName, propValue, extraParams );
//        }
//        response.getWriter().write( String.valueOf( result ) );
//    }
//
//    private boolean check(
//        String tableName, String queryName, String paramName, String value, Map<String,String> extraParams )
//    {
//        try
//        {
//            boolean isAccess = Utils.getAccessibleQuery(
//                connector, userInfo, tableName, queryName, null ) != null;
//
//            if( !isAccess )
//            {
//                return false;
//            }
//        }
//        catch( Exception err )
//        {
//            //Logger.error( cat, "Utils.getAccessibleQuery " + tableName + "::" + queryName, err );
//            return false;
//        }
//
//        QueryExecuter qe = new QueryExecuter( connector, userInfo, Collections.emptyMap(), "" );
//        try
//        {
//            HashMap<String,String> pars = new HashMap<>();
//            pars.putAll( Collections.singletonMap( paramName, value ) );
//            if( extraParams != null )
//            {
//                pars.putAll( extraParams );
//            }
//            qe.makeIterator( tableName, queryName, pars );
//            String[][] vals = qe.calcUsingQuery();
//            return vals.length == 0;
//        }
//        catch( Exception err )
//        {
//            Logger.error( cat, "Unable to validate via query " + tableName + "::" + queryName, err );
//        }
//        finally
//        {
//            qe.closeIterator();
//        }
//        return false;
//    }
}

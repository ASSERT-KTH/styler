/**
 * $Id: CheckUniqueServlet.java,v 1.12 2013/11/08 07:16:17 sav Exp $
 */

package com.developmentontheedge.be5.operation.services.validation;

/**
 * Servlet for checking unuqueness of the field value.
 * Client should send two parameters to this servlet:
 * <ol>
 * <li>{link HttpConstants#TABLE_NAME_PARAM} - name of the table</li>
 * <li>field name and field value as a key-value pair</li>
 * </ol>
 *
 * @author Andrey Anisimov <andrey@developmentontheedge.com>
 * @see <a href="http://docs.jquery.com/Plugins/Validation/Methods/remote#options">AJAX validation method</a>
 */
public class CheckUniqueServlet
{
//    public static final String URI = "json/checkUnique";
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
//    {
//        DatabaseConnector connector = getConnector( request );
//        Map<?,?> params = new TreeMap( String.CASE_INSENSITIVE_ORDER );
//        params.putAll( getParams( request ).getCompleteParamTable( true ) );
//
//        String tableName = ( String )params.get( HttpConstants.TABLE_NAME_PARAM );
//        String fieldName = ( String )params.get( HttpConstants.FIELD_NAME_PARAM );
//        String propName = ( String )params.get( HttpConstants.PROP_NAME_PARAM );
//        String fieldValue = ( String )params.get( propName );
//
//        Map<String,String> extraParams = new HashMap<String,String>();
//        for( Map.Entry<?,?> entry : params.entrySet() )
//        {
//             if( HttpConstants.TABLE_NAME_PARAM.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//             if( HttpConstants.FIELD_NAME_PARAM.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//            if( HttpConstants.PROP_NAME_PARAM.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//             if( propName != null && propName.equalsIgnoreCase( ( String )entry.getKey() ) )
//                 continue;
//             extraParams.put( ( String )entry.getKey(), ( String )entry.getValue() );
//        }
//
//        boolean result = false;
//        if( !Utils.isEmpty( tableName ) && !Utils.isEmpty( fieldName ) && !Utils.isEmpty( fieldValue ) )
//        {
//            result = check( connector, tableName, fieldName, fieldValue, extraParams );
//        }
//        response.getWriter().write( String.valueOf( result ) );
//    }
//
//    private boolean check( String tableName, String fieldName, String value, Map<String,String> extraParams )
//    {
//
//            tableName = Utils.subst( tableName, "'", "" );
//            fieldName = Utils.subst( fieldName, "'", "" );
//
//            String sql = "SELECT 1 FROM " + tableName + " WHERE " + fieldName + " = ";
//            if( Utils.isNumericColumn( connector, tableName, fieldName ) )
//            {
//                sql += Utils.subst( value, "'", "" );
//            }
//            else
//            {
//                sql += Utils.safestr( connector, value, true );
//            }
//
//            for( Map.Entry<String,String> entry : extraParams.entrySet() )
//            {
//                if( Validation.OWNER_IDS_IGNORED.equals(entry.getKey() ))
//                {
//                    sql += " AND ID <> " + Utils.safePKValue ( connector, tableName, entry.getValue());
//                }
//                else
//                {
//                    String fname = Utils.subst( entry.getKey(), "'", "" );
//                    sql += " AND " + entry.getKey() + " = ";
//
//                    if( Utils.isNumericColumn( connector, tableName, fname ) )
//                    {
//                        sql += Utils.subst( entry.getValue(), "'", "" );
//                    }
//                    else
//                    {
//                        sql += Utils.safestr( connector, entry.getValue(), true );
//                    }
//                }
//            }
//
//            new JDBCRecordAdapterAsQuery( connector, sql );
//            return false;
//
//    }
}

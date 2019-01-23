package com.developmentontheedge.be5.databasemodel.groovy;

import com.developmentontheedge.be5.base.model.groovy.ExtensionMethodsMetaClass;
import com.developmentontheedge.be5.databasemodel.EntityModel;
import com.developmentontheedge.be5.databasemodel.RecordModel;
import com.developmentontheedge.beans.DynamicPropertySet;

import java.util.Map;


public class EntityModelMetaClass extends ExtensionMethodsMetaClass
{
    public EntityModelMetaClass(Class<? extends EntityModel> theClass)
    {
        super(theClass);
    }

    @Override
    public Object getProperty(Object object, String property)
    {
        return getPropertyImpl((EntityModel) object, property);
    }

    private Object getPropertyImpl(EntityModel entityModel, String property)
    {
//        if( "array".equals( property ) )
//        {
//            return entityModel.toArray();
//        }
//        else if( "list".equals( property ) )
//        {
//            return entityModel.toList();
//        }
        return super.getProperty(entityModel, property);
    }
//
//    @Override
//    @SuppressWarnings( "unchecked" )
//    public void setProperty( Object object, String entityName, Object values )
//    {
//        ( ( EntityModel )object ).add( ( Map<String, String> )values );
//    }

//    private static Object callClosureWithDelegate( Closure closure, Object delegate )
//    {
//        // Ищем сначала у делегата, потом в скопе
//        closure.setResolveStrategy( Closure.DELEGATE_FIRST );
//        closure.setDelegate( delegate );
//        return closure.call();
//    }

//    public static Object runOperation( EntityModel self, String opName, Closure closure ) throws Exception
//    {
//        OperationModel operation = self.getOperation( opName );
//        callClosureWithDelegate( closure, operation );
//        return operation.invoke();
//    }

//    @Experimental( comment = "Под вопросом имя метода" )
//    public static Object runOperation( EntityModel self, MessageHandler output, String opName, Closure closure ) throws Exception
//    {
//        OperationModel operation = self.getOperation( opName );
//        //operation.setOut( output );
//        callClosureWithDelegate( closure, operation );
//        return operation.invoke();
//    }
//
//    @Experimental( comment = "Непонятно пока, нужен ли сахар в виде передачи out" )
//    public static Object runOperation( EntityModel self, Writer out, String opName, Closure closure ) throws Exception
//    {
//        OperationModel operation = self.getOperation( opName );
//        operation.setOut( out );
//        callClosureWithDelegate( closure, operation );
//        return operation.invoke();
//    }

    public static <R> R leftShift(EntityModel<R> self, Map<String, ? super Object> values)
    {
        return (R) self.add(values);
    }

//    public static List list( EntityModel self )
//    {
//        return self.toList();
//    }
//
//    public static List list( EntityModel self, LinkedHashMap values )
//    {
//        return self.toList( values );
//    }
//
//    public static RecordModel[] toArray( EntityModel self )
//    {
//        return self.toArray();
//    }
//
//    public static RecordModel[] toArray( EntityModel self, Map<String, String> values )
//    {
//        return self.toArray( values );
//    }

//    @Override
//    @SuppressWarnings( "unchecked" )
//    public Object invokeMethod( Object object, String entityName, Object args )
//    {
//        return ( ( EntityModel )object ).getBy( ( Map<String, String> )( ( Object[] )args )[ 0 ] );
//    }
//
//    public static <R> R leftShift( Object object, Map<String, ? super Object> values )
//    {
//        return (R) ( ( EntityModel<R> )object ).add( values );
//    }

    public static RecordModel call(EntityModel self, Map<String, ? super Object> values)
    {
        return self.getBy(values);
    }

    public static RecordModel getAt(EntityModel self, String id)
    {
        return self.get(id);
    }

    public static RecordModel getAt(EntityModel self, Long id)
    {
        return self.get(id);
    }

    public static void putAt(EntityModel self, String id, Map<String, ? super Object> values)
    {
        self.set(id, values);
    }

    public static void putAt(EntityModel self, String id, DynamicPropertySet dps)
    {
        self.set(id, dps);
    }

    public static void putAt(EntityModel self, Long id, Map<String, ? super Object> values)
    {
        self.set(id, values);
    }

    public static void putAt(EntityModel self, Long id, DynamicPropertySet dps)
    {
        self.set(id, dps);
    }

}

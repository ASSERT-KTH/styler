package com.developmentontheedge.be5.databasemodel.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.databasemodel.EntityModel;
import com.developmentontheedge.be5.databasemodel.RecordModel;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.DynamicPropertySetBlocked;

import java.util.Map;


public class RecordModelBase<T> extends DynamicPropertySetBlocked implements RecordModel<T>
{
    private final EntityModel<T> entityModel;
    private final T id;

    RecordModelBase(T id, EntityModel<T> entityModel, DynamicPropertySet dps)
    {
        super(dps);
        if (dps.getProperty(entityModel.getPrimaryKeyName()) == null)
        {
            throw Be5Exception.internal("DynamicPropertySet not contain primaryKey '" + entityModel.getPrimaryKeyName() + "'");
        }
        this.id = id;
        this.entityModel = entityModel;
    }

    @Override
    public T getPrimaryKey()
    {
        return id;
    }

    @Override
    public int remove()
    {
        return entityModel.remove(id);
    }

    @Override
    public String toString()
    {
        return super.toString() + " { " + this.getClass().getSimpleName() + " [ " + entityModel.getPrimaryKeyName() + " = " + getPrimaryKey() + " ] }";
    }

    @Override
    public void update(String propertyName, Object value)
    {
        entityModel.set(getPrimaryKey(), propertyName, value);

        super.setValueHidden(propertyName, value);
    }

    @Override
    public void update(Map<String, Object> values)
    {
        entityModel.set(getPrimaryKey(), values);

        for (String propertyName : values.keySet())
        {
            if (super.hasProperty(propertyName))
            {
                super.setValueHidden(propertyName, values.get(propertyName));
            }
        }
    }

    @Override
    public void setValue(String propertyName, Object value)
    {
        throw new IllegalAccessError("You can't use this operation. Use EntityModel#set() to update value in database.");
    }
//
//    public class MethodProviderBase implements MethodProvider
//    {
//        protected final Method method;
//
//        MethodProviderBase( Method method )
//        {
//            this.method = method;
//        }
//
//        @Override
//        public Object invoke()
//        {
//            return invoke( new Object[]{} );
//        }
//
//        @Override
//        public Object invoke( Object... args )
//        {
//            try
//            {
//                Object[] fullArgs = new Object[ args.length + 1 ];
//                fullArgs[ 0 ] = RecordModelBase.this;
//                System.arraycopy(args, 0, fullArgs, 1, args.length);
//                return method.invoke(entityModel, fullArgs );
//            }
//            catch( IllegalAccessException | IllegalArgumentException | InvocationTargetException e )
//            {
//                throw new RuntimeException( e );
//            }
//        }
//    }
}

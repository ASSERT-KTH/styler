package com.developmentontheedge.be5.operation.services.validation;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySet;

import javax.inject.Inject;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.developmentontheedge.be5.operation.services.validation.Validation.Status.ERROR;
import static com.developmentontheedge.be5.operation.services.validation.Validation.Status.SUCCESS;


public class Validator
{
    public static final Logger log = Logger.getLogger(Validator.class.getName());

    private final UserAwareMeta userAwareMeta;
    private final UserInfoProvider userInfoProvider;

    @Inject
    public Validator(UserAwareMeta userAwareMeta, UserInfoProvider userInfoProvider)
    {
        this.userAwareMeta = userAwareMeta;
        this.userInfoProvider = userInfoProvider;
    }

    public boolean validate(Object parameters)
    {
        return validateStatusInsteadError(() -> checkAndThrowExceptionIsError(parameters));
    }

    public boolean validate(DynamicProperty property)
    {
        return validateStatusInsteadError(() -> checkAndThrowExceptionIsError(property));
    }

    private boolean validateStatusInsteadError(Runnable function)
    {
        try
        {
            function.run();
            return true;
        }
        catch (RuntimeException e)
        {
            if (userInfoProvider.isSystemDeveloper())
            {
                log.log(Level.INFO, "Error on validate: ", e);
            }
            return false;
        }
    }


    public void checkAndThrowExceptionIsError(Object parameters)
    {
        if (parameters instanceof DynamicPropertySet)
        {
            for (DynamicProperty property : (DynamicPropertySet) parameters)
            {
                checkAndThrowExceptionIsError(property);
            }
        }
    }

    public void checkAndThrowExceptionIsError(DynamicProperty property)
    {
        throwExceptionIsError(property);

        if (property.getValue() == null
                || (property.getBooleanAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST)
                && ((Object[]) property.getValue()).length == 0))
        {
            if (property.isCanBeNull())
            {
                return;
            }
            else
            {
                String msg = userAwareMeta.getLocalizedValidationMessage("This field is required.");
                setError(property, msg);
                throw new IllegalArgumentException(msg + toStringProperty(property));
            }
        }

        if (property.getBooleanAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST))
        {
            if (!(property.getValue() instanceof Object[]))
            {
                setError(property, "Value must be array (MULTIPLE_SELECTION_LIST)");
                throw Be5Exception.internal("Value must be array (MULTIPLE_SELECTION_LIST)" + toStringProperty(property));
            }

            Object[] values = (Object[]) property.getValue();
            Object[] resValues = new Object[values.length];

            for (int i = 0; i < values.length; i++)
            {
                if (values[i] instanceof String)
                    resValues[i] = parseFrom(property, (String) values[i]);
                else
                    resValues[i] = values[i];
                checkValueInTags(property, resValues[i]);
            }
            property.setValue(resValues);
        }
        else
        {
            if (property.getValue() instanceof String && property.getType() != String.class)
            {
                property.setValue(parseFrom(property, (String) property.getValue()));
            }
            else if (property.getType() != property.getValue().getClass())
            {
                String msg = "Error, value must be a " + property.getType().getName();
                setError(property, msg);
                throw new IllegalArgumentException(msg + toStringProperty(property));
            }

            checkValueInTags(property, property.getValue());
        }

    }

    private void checkValueInTags(DynamicProperty property, Object value)
    {
        Object tagsObject = property.getAttribute(BeanInfoConstants.TAG_LIST_ATTR);
        if (tagsObject instanceof Object[][])
        {
            Object[][] tags = (Object[][]) tagsObject;

            if (Arrays.stream(tags).noneMatch(item -> (item)[0].toString().equals(value.toString())))
            {
                setError(property, "Value is not contained in tags: " + value.toString());
                throw new IllegalArgumentException("Value is not contained in tags" + toStringProperty(property));
            }
        }
        else if (tagsObject instanceof Object[])
        {
            Object[] tags = (Object[]) tagsObject;

            if (Arrays.stream(tags).noneMatch(item -> item.toString().equals(value.toString())))
            {
                setError(property, "Value is not contained in tags: " + value.toString());
                throw new IllegalArgumentException("Value is not contained in tags" + toStringProperty(property));
            }
        }
    }

    public void setSuccess(DynamicPropertySet dps, String name)
    {
        setSuccess(dps, name, "");
    }

    public void setSuccess(DynamicPropertySet dps, String name, String message)
    {
        dps.getProperty(name).setAttribute(BeanInfoConstants.STATUS, SUCCESS.toString());
        dps.getProperty(name).setAttribute(BeanInfoConstants.MESSAGE, message);
    }

    public void setError(DynamicProperty property, String message)
    {
        property.setAttribute(BeanInfoConstants.STATUS, ERROR.toString());
        property.setAttribute(BeanInfoConstants.MESSAGE, message);
    }

    public void setError(DynamicPropertySet dps, String name, String message)
    {
        dps.getProperty(name).setAttribute(BeanInfoConstants.STATUS, ERROR.toString());
        dps.getProperty(name).setAttribute(BeanInfoConstants.MESSAGE, message);
    }

    @Deprecated
    public Object parseFrom(DynamicProperty property, String value)
    {
        if (value == null) return null;

        Class<?> type = property.getType();

        //todo move to IntegerRule, NumberRule
        try
        {
            if (type == Short.class) return Short.parseShort(value);
            if (type == Integer.class) return Integer.parseInt(value);
            if (type == Long.class) return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            String msg = userAwareMeta.getLocalizedValidationMessage("Please specify an integer number.");

            try
            {
                BigInteger bigInteger = new BigInteger(value);
                if (type == Long.class)
                {
                    if (bigInteger.compareTo(BigInteger.ZERO) > 0)
                    {
                        msg += " <= " + Long.MAX_VALUE;
                    }
                    else
                    {
                        msg += " >= " + Long.MIN_VALUE;
                    }
                }
                if (type == Integer.class)
                {
                    if (bigInteger.compareTo(BigInteger.ZERO) > 0)
                    {
                        msg += " <= " + Integer.MAX_VALUE;
                    }
                    else
                    {
                        msg += " >= " + Integer.MIN_VALUE;
                    }
                }
                if (type == Short.class)
                {
                    if (bigInteger.compareTo(BigInteger.ZERO) > 0)
                    {
                        msg += " <= " + Short.MAX_VALUE;
                    }
                    else
                    {
                        msg += " >= " + Short.MIN_VALUE;
                    }
                }
            }
            catch (RuntimeException ignore)
            {
            }

            setError(property, msg);
            throw new NumberFormatException(msg + toStringProperty(property));
        }

        try
        {
            if (type == Float.class) return Float.parseFloat(value);
            if (type == Double.class) return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
            String msg = userAwareMeta.getLocalizedValidationMessage("Please enter a valid number.");
            //добавить информацию о конкресном типе - ограничения type.getName();
            setError(property, msg);
            throw new NumberFormatException(msg + toStringProperty(property));
        }

        if (type == Boolean.class) return Boolean.parseBoolean(value);

        //todo move to DateRule
        try
        {
            if (type == Date.class) return Date.valueOf(value);
        }
        catch (IllegalArgumentException e)
        {
            String msg = userAwareMeta.getLocalizedValidationMessage("Please enter a valid date.");
            setError(property, msg);
            throw new IllegalArgumentException(msg + toStringProperty(property));
        }
        try
        {
            if (type == Timestamp.class) return Timestamp.valueOf(value);
        }
        catch (IllegalArgumentException e)
        {
            String msg = userAwareMeta.getLocalizedValidationMessage("Please enter a valid date with time.");
            setError(property, msg);
            throw new IllegalArgumentException(msg + toStringProperty(property));
        }

        return value;
    }

    public void throwExceptionIsError(Object parameters)
    {
        if (parameters instanceof DynamicPropertySet)
        {
            for (DynamicProperty property : (DynamicPropertySet) parameters)
            {
                throwExceptionIsError(property);
            }
        }
    }

    public void throwExceptionIsError(DynamicProperty property)
    {
        if (isError(property))
        {
            throw new IllegalArgumentException((String) property.getAttribute("message"));
        }
    }

    public boolean isValid(DynamicProperty property)
    {
        return !isError(property);
    }

    public boolean isError(DynamicProperty property)
    {
        Object statusAttr = property.getAttribute(BeanInfoConstants.STATUS);
        if (statusAttr == null) return false;
        Validation.Status status;
        if (statusAttr.getClass() == Validation.Status.class)
        {
            status = (Validation.Status) statusAttr;
        }
        else
        {
            status = Validation.Status.valueOf(((String) statusAttr).toUpperCase());
        }
        return status == Validation.Status.ERROR;
    }

    private String toStringProperty(DynamicProperty property)
    {
        return "";
//        if(!UserInfoHolder.isSystemDeveloper())return "";
//        String value;
//        if(property.getValue() != null)
//        {
//            value = property.getValue().getClass().isArray() ? Arrays.toString((Object[]) property.getValue()) : property.getValue().toString();
//            value += " (" + property.getValue().getClass().getSimpleName() + ")";
//        }
//        else
//        {
//            value = "null";
//        }
//        return " - ["
//                + " name: '"  + property.getName()
//                + "', type: "  + property.getType()
//                + ", value: " + value
//                + " ]";
    }
}

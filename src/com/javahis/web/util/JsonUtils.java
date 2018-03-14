package com.javahis.web.util;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.beans.*;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;

public class JsonUtils
{

    public JsonUtils()
    {
    }

    public static String stringToJson(String s)
    {
        StringBuilder sb = new StringBuilder(s.length() + 20);
        sb.append('"');
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            switch(c)
            {
            case 34: // '"'
                sb.append("\\\"");
                break;

            case 92: // '\\'
                sb.append("\\\\");
                break;

            case 47: // '/'
                sb.append("\\/");
                break;

            case 8: // '\b'
                sb.append("\\b");
                break;

            case 12: // '\f'
                sb.append("\\f");
                break;

            case 10: // '\n'
                sb.append("\\n");
                break;

            case 13: // '\r'
                sb.append("\\r");
                break;

            case 9: // '\t'
                sb.append("\\t");
                break;

            default:
                sb.append(c);
                break;
            }
        }

        sb.append('"');
        return sb.toString();
    }

    public static String numberToJson(Number number)
    {
        return number.toString();
    }

    public static String booleanToJson(Boolean bool)
    {
        return bool.toString();
    }

    public static String dateToJson(Date date)
    {
        return DateFormat.getInstance().format(date);
    }

    public static String arrayToJson(Object array[])
    {
        if(array.length == 0)
            return "[]";
        StringBuilder sb = new StringBuilder(array.length << 4);
        sb.append('[');
        Object aobj[] = array;
        int i = 0;
        for(int j = aobj.length; i < j; i++)
        {
            Object o = aobj[i];
            sb.append(objectToJson(o)).append(',');
        }

        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    public static String collectionToJson(Collection collection)
    {
        return arrayToJson(collection.toArray());
    }

    public static String mapToJson(Map map)
    {
        if(map.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder(map.size() << 4);
        sb.append('{');
        String key;
        for(Iterator iterator = map.keySet().iterator(); iterator.hasNext(); sb.append(stringToJson(key)).append(':').append(objectToJson(map.get(key))).append(','))
            key = (String)iterator.next();

        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }

    public static String beanToJson(Object bean)
    {
        if(bean == null)
            return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        PropertyDescriptor properties[] = (PropertyDescriptor[])null;
        try
        {
            properties = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
        }
        catch(IntrospectionException introspectionexception) { }
        if(properties != null)
        {
            for(int i = 0; i < properties.length; i++)
                try
                {
                    Method reader = properties[i].getReadMethod();
                    if(reader != null)
                    {
                        Expression expression = new Expression(bean, reader.getName(), null);
                        expression.execute();
                        sb.append(stringToJson(properties[i].getName())).append(':').append(objectToJson(expression.getValue())).append(',');
                    }
                }
                catch(Exception exception) { }

            sb.setCharAt(sb.length() - 1, '}');
        } else
        {
            sb.append("}");
        }
        return sb.toString();
    }

    public static String objectToJson(Object o)
    {
        if(o == null)
            return "null";
        try
        {
            if(o instanceof String)
                return stringToJson((String)o);
        }
        catch(Throwable e)
        {
            throw new RuntimeException((new StringBuilder("Unsupported type: ")).append(o.getClass().getName()).append("------").append(e.getMessage()).toString());
        }
        if(o instanceof Enum)
            return stringToJson(o.toString());
        if(o instanceof Boolean)
            return booleanToJson((Boolean)o);
        if(o instanceof Number)
            return numberToJson((Number)o);
        if(o instanceof Object[])
            return arrayToJson((Object[])o);
        if(o instanceof Collection)
            return collectionToJson((Collection)o);
        if(o instanceof Map)
            return mapToJson((Map)o);
        return beanToJson(o);
    }
}

package org.pojava.persistence.factories;

import java.util.Map;

import org.pojava.datetime.DateTime;

/**
 * The DateTimeFactory is a factory for handling objects compatible with the DateTime class. It
 * is compatible with a wide variety of Date formats.
 * 
 * @author John Pile
 * 
 */
public class DateTimeFactory<T> implements SerialFactory<T> {

    /**
     * Construct a DateTime object from a parameter array.
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class type, Object[] params) {
        DateTime dt = null;
        if (type == DateTime.class) {
            if (params.length == 1) {
                if (params[0].toString().matches("^-?[0-9]+\\.[0-9]{1,9}$")) {
                    int point = params[0].toString().indexOf('.');
                    long seconds = Long.parseLong(params[0].toString().substring(0, point));
                    StringBuffer sb = new StringBuffer(params[0].toString()
                            .substring(point + 1));
                    sb.append("000000000");
                    int nanos = Integer.parseInt(sb.substring(0, 9));
                    dt = new DateTime(seconds, nanos);
                } else {
                    dt = new DateTime(params[0].toString());
                }
            }
        }
        return dt;
    }

    /**
     * Costruct an object from mapped parameters.
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class type, Map params) {
        if (type == DateTime.class) {
            if (params.containsKey("seconds")) {
                long seconds = ((Long) params.get("seconds")).longValue();
                int nanos = 0;
                if (params.containsKey("nanos")) {
                    nanos = ((Integer) params.get("nanos")).intValue();
                }
                if (params.containsKey("tzId")) {
                    return new DateTime(seconds, nanos, (String) params.get("tzId"));
                }
                return new DateTime(seconds, nanos);
            }
            if (params.containsKey("time")) {
                return new DateTime(((Long) params.get("time")).longValue());
            }
            if (params.containsKey("millis")) {
                return new DateTime(((Long) params.get("millis")).longValue());
            }
        }
        return null;
    }

    /**
     * Serialize a DateTime class to xml.
     */
    public String serialize(Object obj) {
        StringBuffer sb = new StringBuffer();
        if (DateTime.class == obj.getClass()) {
            DateTime dt = (DateTime) obj;
            sb.append(dt.getSeconds());
            sb.append(".");
            int dot = sb.length() + 1;
            sb.append(dt.getNanos());
            while (sb.charAt(sb.length() - 1) == '0' && sb.length() > dot) {
                sb.setLength(sb.length() - 1);
            }
        }
        return sb.toString();
    }

}

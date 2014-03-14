package org.pojava.persistence.factories;

import org.pojava.datetime.DateTime;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * DateFactory provides serialization for the java.util.Date and its derived objects up to
 * millisecond precision.
 *
 * @author John Pile
 */
public class DateFactory<T> implements SerialFactory<T> {

    /**
     * Construct a java.util.Date object.
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class type, Object[] params) {
        if (Date.class.isAssignableFrom(type)) {
            if (type == Timestamp.class) {
                Timestamp ts;
                if (params.length == 1) {
                    if (params[0].toString().matches("^-?[0-9]+\\.[0-9]{1,9}$")) {
                        int point = params[0].toString().indexOf('.');
                        long seconds = Long.parseLong(params[0].toString().substring(0, point));
                        String zeroPrefixedLong=params[0].toString().substring(
                                point + 1) + "000000000";
                        int nanos = Integer.parseInt(zeroPrefixedLong.substring(0, 9));
                        ts = new Timestamp(seconds * 1000 + nanos / 1000000);
                        ts.setNanos(nanos);
                        return ts;
                    } else {
                        return new DateTime(params[0].toString()).toTimestamp();
                    }
                }
            }
            if (params.length == 1) {
                if (params[0] == null || params[0].toString().length() == 0) {
                    return null;
                }
                return new Date(Long.parseLong(params[0].toString()));
            }
        }
        return null;
    }

    /**
     * Construct a java.util.Date object.
     */
    @SuppressWarnings("unchecked")
    public Object construct(Class type, Map params) {
        if (Date.class.isAssignableFrom(type)) {
            long time = 0;
            if (params.containsKey("time")) {
                time = (Long) params.get("time");
            }
            if (params.containsKey("millis")) {
                time = (Long) params.get("millis");
            }
            if (type == Timestamp.class) {
                Timestamp ts = new Timestamp(time);
                if (params.containsKey("nanos")) {
                    ts.setNanos((Integer) params.get("nanos"));
                }
                return ts;
            } else {
                return new Date(time);
            }
        }
        return null;
    }

    /**
     * Serialize a java.util.Date object to UTC millis.
     */
    public String serialize(Object obj) {
        if (Date.class.isAssignableFrom(obj.getClass())) {
            if (Timestamp.class.equals(obj.getClass())) {
                Timestamp ts = (Timestamp) obj;
                StringBuilder sb = new StringBuilder();
                sb.append("000000000");
                sb.append(ts.getNanos());
                char[] nanos = sb.substring(sb.length() - 9, sb.length()).toCharArray();
                int i;
                for (i = 8; i > 1; i--) {
                    if (nanos[i] != '0')
                        break;
                }
                String nanoString = sb.substring(sb.length() - 9, sb.length() - 8 + i);
                sb.setLength(0);
                sb.append(ts.getTime() / 1000);
                sb.append('.');
                sb.append(nanoString);
                return sb.toString();
            } else {
                Date d = (Date) obj;
                return Long.toString(d.getTime());
            }
        }
        return "";
    }

}

package org.pojava.datetime;

import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/*
 Copyright 2010 John Pile

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * DateTimeFormat formats a DateTime object as a String according to a template. This class
 * provides similar functionality to Java's SimpleDateFormat class, with some notable
 * differences. It changes the meaning of the "S" character from "millisecond" to
 * "fractional second" so that, for example, nine consecutive "S" characters would represent
 * nanoseconds, while three "S" characters represent milliseconds. The upper-case "G" still
 * represents "BC" or "AD", but I added a lower-cased "g" to the format to use "BCE" or "CE".
 * While "Z" still shows time zone offset as "-HHmm", "ZZ" will add a colon, as "-HH:mm".
 * 
 * Because it does not "compile" the format String, DateTimeFormat can provide a static method
 * with the same performance as a constructed object. It does allow a constructed object for
 * similar behavior to existing formatters, but there is no performance advantage in doing so.
 * In either case, this class is thread-safe, provided your application is not trying to
 * change the internals of Java's TimeZone object as you're using it.
 * 
 * It is important to understand that the default behavior is to format the output according to
 * the system's time zone. If you want to format the output according to the DateTime object's
 * internal time zone, then pass the time zone as a parameter. For example,
 * 
 * <pre>
 * DateTimeFormat(&quot;yyyy-MM-dd&quot;, dateTimeObj, dateTimeObj.getTimezone);
 * </pre>
 * 
 * For this version, you're kind of stuck with an English-only version of dates. I'll be
 * revising that at a future date.
 * 
 * @author John Pile
 * 
 */
public class DateTimeFormat {

    private static final int[] dom = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334,
            365 };
    private String template;
    private static Map <Locale, DateFormatSymbols> symbols=new HashMap<Locale,DateFormatSymbols>();

    /**
     * 
     * @param template
     * 		Format specifier
     */
    public DateTimeFormat(String template) {
        this.template = template;
    }

    /**
     * 
     * @param dt
     * 		Format the given DateTime value to a String
     * @return
     * 		Formatted output
     */
    public String format(DateTime dt) {
        return format(this.template, dt);
    }

    /**
     * 
     * @param millis
     * 		Format the given millis value to a String
     * @return
     * 		Formatted output
     */
    public String format(long millis) {
        return format(this.template, new DateTime(millis));
    }

    /**
     * @param template
     * 		Template under which the output is formatted
     * @param millis
     * 		Format the given millis value to a String
     * @return
     * 		Formatted output
     */
    public static String format(String template, long millis) {
        return format(template, new DateTime(millis));
    }

    /**
     * @param template
     * 		Template under which the output is formatted
     * @param dt
     * 		Format the given DateTime value to a String
     * @return
     * 		Formatted output
     */
    public static String format(String template, DateTime dt) {
        return format(template, dt, dt.config().getOutputTimeZone());
    }

    /**
     * @param template
     * 		Template under which the output is formatted
     * @param dt
     * 		Format the given DateTime value to a String
     * @param tz
     * 		TimeZone for which the output is displayed
     * @return
     * 		Formatted output
     */
    public static String format(String template, DateTime dt, TimeZone tz) {
        return format(template, dt, tz, dt.config().getLocale());
    }
    
    /**
     * @param template
     * 		Template under which the output is formatted
     * @param dt
     * 		Format the given DateTime value to a String
     * @param tz
     * 		TimeZone for which the output is displayed
     * @param locale
     * 		Locale governing language of non-numeric output
     * @return
     * 		Formatted output
     */
    public static String format(String template, DateTime dt, TimeZone tz, Locale locale) {
        Tm tm = new Tm(dt, tz);
        StringBuilder sb = new StringBuilder();
        StringBuilder word = new StringBuilder();
        char[] fmt = template.toCharArray();
        char prior = fmt[0];
        if (prior!='\'') {
            word.append(prior);
        }
        if (!symbols.containsKey(locale)) {
            symbols.put(locale, new DateFormatSymbols(locale));
        }
        DateFormatSymbols dfs=symbols.get(locale);
        boolean isLiteral=(prior=='\'');
        for (int i = 1; i < fmt.length; i++) {
            if (fmt[i]=='\'') {
                if (prior=='\'') {
                    sb.append('\'');
                } else {
                    appendWord(sb, word, tm, dt, tz, locale, dfs);
                }
                word.setLength(0);
                prior = prior=='\'' ? ' ' : '\'';
                isLiteral=!isLiteral;
            } else if (isLiteral) {
                sb.append(fmt[i]);
                prior=fmt[i];
            } else if (fmt[i] == prior) {
                word.append(prior);
            } else {
                appendWord(sb, word, tm, dt, tz, locale, dfs);
                prior = fmt[i];
                word.setLength(0);
                word.append(prior);
            }
        }
        appendWord(sb, word, tm, dt, tz, locale, dfs);
        return sb.toString();
    }

    /**
     * 
     * @param sb Whole output string
     * @param word Individual word being added
     * @param tm 
     * @param dt DateTime
     * @param tz TimeZone
     * @param locale Locale
     * @param dfs DateFormatSymbols
     */
    private static void appendWord(StringBuilder sb, StringBuilder word, Tm tm, DateTime dt, TimeZone tz, Locale locale, DateFormatSymbols dfs) {
        if (word.length()==0) {
            return;
        }
        char c = word.charAt(0);
        int len = word.length();
        switch (c) {
        case 'g':
            sb.append(tm.getYear() < 0 ? "BCE" : "CE");
            break;
        case 'G':
            sb.append(tm.getYear() < 0 ? "BC" : "AD");
            break;
        case 'y':
            sb.append(zfill(tm.getYear(), Math.max(2, len)));
            break;
        case 'M':
            if (len > 3) {
                sb.append(dfs.getMonths()[tm.getMonth()-1]);
            } else if (len == 3) {
                sb.append(dfs.getShortMonths()[tm.getMonth()-1]);
            } else if (len == 2) {
                sb.append(zfill(tm.getMonth(), 2));
            } else {
                sb.append(tm.getMonth());
            }
            break;
        case 'D':
            if (len > 1) {
                sb.append(zfill(dom[tm.getMonth()] + tm.getDay() + leapDays(tm), len));
            } else {
                sb.append(dom[tm.getMonth()] + tm.getDay() + leapDays(tm));
            }
            break;
        case 'd':
            if (len > 1) {
                sb.append(zfill(tm.getDay(), len));
            } else {
                sb.append(tm.getDay());
            }
            break;
        case 'E':
            if (len > 3) {
                sb.append(dfs.getWeekdays()[tm.getWeekday()]);
            } else {
                sb.append(dfs.getShortWeekdays()[tm.getWeekday()]);
            }
            break;
        case 'a':
            sb.append(dfs.getAmPmStrings()[tm.getHour() > 11 ? 1 : 0]);
            break;
        case 'H':
            if (len > 1) {
                sb.append(zfill(tm.getHour(), len));
            } else {
                sb.append(tm.getHour());
            }
            break;
        case 'k':
            if (len > 1) {
                sb.append(zfill(1 + tm.getHour(), len));
            } else {
                sb.append(1 + tm.getHour());
            }
            break;
        case 'K':
            if (len > 1) {
                sb.append(zfill(tm.getHour() % 12, len));
            } else {
                sb.append(tm.getHour() % 12);
            }
            break;
        case 'h':
            int hr = tm.getHour() % 12;
            if (hr == 0)
                hr = 12;
            if (len > 1) {
                sb.append(zfill(hr, len));
            } else {
                sb.append(hr);
            }
            break;
        case 'm':
            if (len > 1) {
                sb.append(zfill(tm.getMinute(), len));
            } else {
                sb.append(tm.getMinute());
            }
            break;
        case 's':
            if (len > 1) {
                sb.append(zfill(tm.getSecond(), len));
            } else {
                sb.append(tm.getSecond());
            }
            break;
        case 'S':
            sb.append(zfill(tm.getNanosecond(), 9).substring(0, len));
            break;
        case 'z':
            if (len>3) {
                sb.append(dt.timeZone().getDisplayName(locale));
            } else {
                sb.append(tz.getDisplayName(tz.inDaylightTime(dt.toDate()), TimeZone.SHORT, locale));
            }
            break;
        case 'Z':
            int minutes = tz.getOffset(dt.toMillis()) / 60000;
            if (minutes < 0) {
                sb.append('-');
                minutes = -minutes;
            } else {
                sb.append('+');
            }
            int hours = minutes / 60;
            minutes -= hours * 60;
            if (hours<10){
                sb.append('0');
            }
            sb.append(hours);
            if (len>1) {
                sb.append(':');
            }
            if (minutes<10) {
                sb.append('0');
            }
            sb.append(minutes);
            break;
        default:
            sb.append(word);
            break;
        }
    }

    /**
     * Zero-Fill
     * @param value Numeric value
     * @param size Width of zero-fill
     * @return
     * 		Zero-filled representation of number
     */
    private static String zfill(int value, int size) {
        StringBuilder zeros = new StringBuilder("000000000000");
        zeros.append(Integer.toString(value));
        return zeros.substring(zeros.length() - Math.min(zeros.length(), size));
    }

    /**
     * Number leap days in tm's year
     * @param tm
     * @return
     * 		Number of leap days (zero or one) in given year
     */
    private static int leapDays(Tm tm) {
        if (tm.getMonth() < 3) {
            return 0;
        }
        int year = tm.getYear();
        return year % 4 == 0 && (year % 400 == 0 || year % 100 != 0) ? 1 : 0;
    }
}

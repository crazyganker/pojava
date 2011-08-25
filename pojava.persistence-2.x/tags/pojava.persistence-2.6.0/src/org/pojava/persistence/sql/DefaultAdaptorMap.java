package org.pojava.persistence.sql;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;

import org.pojava.datetime.DateTime;
import org.pojava.persistence.adaptor.BigDecimalAdaptor;
import org.pojava.persistence.adaptor.BooleanAdaptor;
import org.pojava.persistence.adaptor.ByteAdaptor;
import org.pojava.persistence.adaptor.CharAdaptor;
import org.pojava.persistence.adaptor.DateTimeSqlAdaptor;
import org.pojava.persistence.adaptor.DoubleAdaptor;
import org.pojava.persistence.adaptor.FloatAdaptor;
import org.pojava.persistence.adaptor.IntegerAdaptor;
import org.pojava.persistence.adaptor.LongAdaptor;
import org.pojava.persistence.adaptor.PassthroughAdaptor;
import org.pojava.persistence.adaptor.TimeAdaptor;
import org.pojava.persistence.adaptor.UtilDateSqlAdaptor;
import org.pojava.transformation.BindingAdaptor;

/**
 * DefaultAdaptorMap is a rules engine for determining which BindingAdaptor to use to translate
 * data between a database field and a bean property.
 * 
 * It uses the (method + columnClass) as search criteria. The search attempts to derive a best
 * fitting Adaptor for that property. Adaptors are stateless, so the algorithm strives for
 * adaptor reuse.
 * 
 * The vision: A default can be supplied for the property returnType. An optional user-defined
 * override can finesse column types where needed.
 * 
 * @author John Pile
 * 
 */
public class DefaultAdaptorMap<I,O> implements AdaptorMap<I,O> {

    
	@SuppressWarnings("unchecked")
	private static final PassthroughAdaptor DEFAULT_ADAPTOR = new PassthroughAdaptor();
    private static final IntegerAdaptor INTEGER_ADAPTOR = new IntegerAdaptor();
    private static final LongAdaptor LONG_ADAPTOR = new LongAdaptor();
    private static final CharAdaptor CHAR_ADAPTOR = new CharAdaptor();
    private static final DoubleAdaptor DOUBLE_ADAPTOR = new DoubleAdaptor();
    private static final FloatAdaptor FLOAT_ADAPTOR = new FloatAdaptor();
    private static final ByteAdaptor BYTE_ADAPTOR = new ByteAdaptor();
    private static final BigDecimalAdaptor BIGDECIMAL_ADAPTOR = new BigDecimalAdaptor();
    private static final TimeAdaptor TIME_ADAPTOR = new TimeAdaptor();
    private static final UtilDateSqlAdaptor UTILDATESQL_ADAPTOR = new UtilDateSqlAdaptor();
    private static final DateTimeSqlAdaptor DATETIMESQL_ADAPTOR = new DateTimeSqlAdaptor();
    private static final BooleanAdaptor BOOLEAN_ADAPTOR = new BooleanAdaptor();

    private static final DefaultAdaptorMap<?, ?> adaptorMap = new DefaultAdaptorMap<Object, Object>();

    /**
     * Select an adaptor based on a variety of possible criteria.
     */
    @SuppressWarnings("unchecked")
	public BindingAdaptor chooseAdaptor(Method method, Class columnClass) {
        BindingAdaptor<?, ?> adaptor;

        if (AdaptorRegistry.containsKey(method)) {
            return AdaptorRegistry.get(method);
        }
        // Default behavior is based solely on the property type
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(int.class) || returnType.equals(Integer.class)) {
            adaptor = INTEGER_ADAPTOR;
        } else if (returnType.equals(long.class) || returnType.equals(Long.class)) {
            adaptor = LONG_ADAPTOR;
        } else if (returnType.equals(char.class) || returnType.equals(Character.class)) {
            adaptor = CHAR_ADAPTOR;
        } else if (returnType.equals(double.class) || returnType.equals(Double.class)) {
            adaptor = DOUBLE_ADAPTOR;
        } else if (returnType.equals(boolean.class) || returnType.equals(Boolean.class)) {
            adaptor = BOOLEAN_ADAPTOR;
        } else if (returnType.equals(byte.class) || returnType.equals(Byte.class)) {
            adaptor = BYTE_ADAPTOR;
        } else if (returnType.equals(Time.class)) {
            adaptor = TIME_ADAPTOR;
        } else if (returnType.equals(float.class) || returnType.equals(Float.class)) {
            adaptor = FLOAT_ADAPTOR;
        } else if (returnType.equals(BigDecimal.class)) {
            adaptor = BIGDECIMAL_ADAPTOR;
        } else if (java.util.Date.class.isAssignableFrom(returnType)) {
            adaptor = UTILDATESQL_ADAPTOR;
        } else if ((java.sql.Date.class.equals(columnClass) || (java.sql.Timestamp.class
                .equals(columnClass))
                && returnType.equals(DateTime.class))) {
            adaptor = DATETIMESQL_ADAPTOR;
        } else {
            adaptor = DEFAULT_ADAPTOR;
        }
        return adaptor;
    }

    /**
     * Return a reference to this reentrant class.
     * 
     * @return a reference to this reentrant class
     */
    public static DefaultAdaptorMap<?, ?> getInstance() {
        return adaptorMap;
    }
}

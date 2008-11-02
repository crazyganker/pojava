package org.pojava.persistence.sql;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import org.pojava.datetime.DateTime;
import org.pojava.persistence.adaptor.ByteAdaptor;
import org.pojava.persistence.adaptor.CharAdaptor;
import org.pojava.persistence.adaptor.DateTimeSqlAdaptor;
import org.pojava.persistence.adaptor.DoubleAdaptor;
import org.pojava.persistence.adaptor.TimeAdaptor;
import org.pojava.persistence.adaptor.UtilDateSqlAdaptor;
import org.pojava.transformation.BindingAdaptor;
import org.pojava.transformation.DefaultAdaptor;

/**
 * BasicAdaptorMap is a rules engine for determining which BindingAdaptor to use
 * to translate data between a database field and a bean property.
 * 
 * @author John Pile
 * 
 */
public class DefaultAdaptorMap implements AdaptorMap {

	private static final DefaultAdaptor DEFAULT_ADAPTOR = new DefaultAdaptor();
	private static final CharAdaptor CHAR_ADAPTOR = new CharAdaptor();
	private static final DoubleAdaptor DOUBLE_ADAPTOR = new DoubleAdaptor();
	private static final ByteAdaptor BYTE_ADAPTOR = new ByteAdaptor();
	private static final TimeAdaptor TIME_ADAPTOR = new TimeAdaptor();
	private static final UtilDateSqlAdaptor UTILDATESQL_ADAPTOR = new UtilDateSqlAdaptor();
	private static final DateTimeSqlAdaptor DATETIMESQL_ADAPTOR = new DateTimeSqlAdaptor();

	private static final Map beanClassMethodMaps = new HashMap();

	private static final DefaultAdaptorMap adaptorMap = new DefaultAdaptorMap();

	public BindingAdaptor chooseAdaptor(Class beanClass, Method[] getters,
			Class columnClass) {
		BindingAdaptor adaptor;
		Method method = getters[getters.length - 1];
		// Default behavior is based solely on the property type
		Class returnType = method.getReturnType();
		if (returnType.equals(char.class) || returnType.equals(Character.class)) {
			adaptor = CHAR_ADAPTOR;
		} else if (returnType.equals(double.class)) {
			adaptor = DOUBLE_ADAPTOR;
		} else if (returnType.equals(byte.class)) {
			adaptor = BYTE_ADAPTOR;
		} else if (returnType.equals(Time.class)) {
			adaptor = TIME_ADAPTOR;
		} else if (java.util.Date.class.isAssignableFrom(returnType)) {
			adaptor = UTILDATESQL_ADAPTOR;
		} else if (java.sql.Date.class.equals(columnClass)
				&& returnType.equals(DateTime.class)) {
			adaptor = DATETIMESQL_ADAPTOR;
		} else {
			adaptor = DEFAULT_ADAPTOR;
		}
		// User-defined overrides provide access to custom adaptors
		if (beanClassMethodMaps.containsKey(beanClass)) {
			Map methodMap = (Map) beanClassMethodMaps.get(beanClass);
			if (methodMap != null) {
				if (methodMap.containsKey(method)) {
					adaptor = (BindingAdaptor) methodMap.get(method);
				}
			}
		}
		return adaptor;
	}

	public static DefaultAdaptorMap getInstance() {
		return adaptorMap;
	}
}

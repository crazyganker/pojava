package org.pojava.persistence.sql;

import java.lang.reflect.Method;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

import org.pojava.datetime.DateTime;
import org.pojava.persistence.processor.ByteAdaptor;
import org.pojava.persistence.processor.CharAdaptor;
import org.pojava.persistence.processor.DateTimeSqlAdaptor;
import org.pojava.persistence.processor.DoubleAdaptor;
import org.pojava.persistence.processor.TimeAdaptor;
import org.pojava.persistence.processor.UtilDateSqlAdaptor;
import org.pojava.transformation.BindingAdaptor;
import org.pojava.transformation.DefaultAdaptor;

/**
 * AdaptorMap serves as a rules engine for determining which BindingAdaptor
 * to use to translate data between a database field and a bean property.
 *   
 * @author John Pile
 *
 */
public class AdaptorMap {
	
	private static final DefaultAdaptor ADAPTOR = new DefaultAdaptor();
	private static final CharAdaptor CHAR_ADAPTOR = new CharAdaptor();
	private static final DoubleAdaptor DOUBLE_ADAPTOR = new DoubleAdaptor();
	private static final ByteAdaptor BYTE_ADAPTOR = new ByteAdaptor();
	private static final TimeAdaptor TIME_ADAPTOR = new TimeAdaptor();
	private static final UtilDateSqlAdaptor UTILDATESQL_ADAPTOR = new UtilDateSqlAdaptor();
	private static final DateTimeSqlAdaptor DATETIMESQL_ADAPTOR = new DateTimeSqlAdaptor();
	
	private static final Map beanClassMethodMaps=new HashMap();

	public static BindingAdaptor chooseAdaptor(Class beanClass, Method[] getters, String columnClassName) {
		BindingAdaptor adaptor;
		Method method=getters[getters.length-1];
		// Default behavior is based solely on the property type
		Class returnType=method.getReturnType();
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
		} else if ("java.sql.Date".equals(columnClassName) && returnType.equals(DateTime.class)) {
			adaptor = DATETIMESQL_ADAPTOR;
		} else {
			adaptor = ADAPTOR;
		}
		// User-defined overrides provide access to custom adaptors
		if (beanClassMethodMaps.containsKey(beanClass)) {
			Map methodMap=(Map)beanClassMethodMaps.get(beanClass);
			if (methodMap!=null) {
				if (methodMap.containsKey(method)) {
					adaptor = (BindingAdaptor) methodMap.get(method);
				}
			}
		}
		return adaptor;
	}
}

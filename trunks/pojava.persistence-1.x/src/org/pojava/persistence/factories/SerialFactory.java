package org.pojava.persistence.factories;

import java.util.Map;

public interface SerialFactory {

	Object construct(Class type, Object[] params);
	
	Object construct(Class type, Map params);
	
	String serialize(Object obj);
	
}

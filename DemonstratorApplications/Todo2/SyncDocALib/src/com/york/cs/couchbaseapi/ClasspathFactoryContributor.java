/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.HashMap;


/**
 * The Class ClasspathFactoryContributor.
 */
public class ClasspathFactoryContributor implements FactoryContributor {

	/** The class cache. */
	protected HashMap<String, Class<?>> classCache = new HashMap<String, Class<?>>();
	
	/* (non-Javadoc)
	 * @see com.york.cs.couchbaseapi.FactoryContributor#canCreate(java.lang.String)
	 */
	@Override
	public boolean canCreate(String className) {
		try {
			return classForName(className) != null;
		} catch (Exception e) {
			return false;
		} 
	} 

	/* (non-Javadoc)
	 * @see com.york.cs.couchbaseapi.FactoryContributor#create(java.lang.String)
	 */
	@Override
	public DBObject create(String className) throws Exception {
		return (DBObject) classForName(className).newInstance();
	}
	
	/**
	 * Class for name.
	 *
	 * @param className the class name
	 * @return a class instance
	 * @throws Exception is cannot not be found
	 */
	protected Class<?> classForName(String className) throws Exception {
		Class<?> clazz = classCache.get(className);
		if (clazz == null) {
			clazz = Class.forName(className);
			classCache.put(className, clazz);
		}
		return clazz;
	}
	
}

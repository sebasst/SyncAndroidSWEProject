/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;
//TODO not necessary
public interface FactoryContributor {
	
	public boolean canCreate(String className);
	
	public DBObject create(String className) throws Exception;
	
}

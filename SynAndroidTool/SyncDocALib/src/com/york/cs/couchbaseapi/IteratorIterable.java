/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.Iterator;

public class IteratorIterable<T> implements Iterable<T> {
	
	protected Iterator<T> iterator;
	
	public IteratorIterable(Iterator<T> iterator) {
		this.iterator = iterator;
	}
	
	@Override
	public Iterator<T> iterator() {
		return iterator;
	}

}

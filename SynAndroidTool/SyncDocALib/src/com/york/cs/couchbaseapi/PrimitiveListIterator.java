/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.Iterator;
 
public class PrimitiveListIterator<T> implements Iterator<T> {
	
	protected Iterator<?> iterator = null;
	
	public PrimitiveListIterator(Iterator<?> iterator) {
		this.iterator = iterator;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		return (T) iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}

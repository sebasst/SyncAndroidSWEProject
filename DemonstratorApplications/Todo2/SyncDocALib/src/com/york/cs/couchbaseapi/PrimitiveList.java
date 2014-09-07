/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PrimitiveList<T> implements List<T>{
	
	protected DBObject container = null;
	protected ArrayList<T> dbList=null;
	
	public PrimitiveList(DBObject container, ArrayList<T> dbList) {
		
		this.container = container;
		this.dbList = dbList;
	}

	
	
	
	public Iterator<T> iterator() {
		return new PrimitiveListIterator<T>(dbList.iterator());
	}

	
	protected T wrap(Object o) {
		return (T) o;
	}

	
	protected Object unwrap(Object o) {
		return o;
	}

	
	protected void added(T t) {
		
		container.notifyChanged();
	}
	
	
	protected void removed(Object o) {
		
		container.notifyChanged();
	}
	
	public boolean contains(Object o) {

		return dbList.contains(unwrap(o));
	}
	
	/* (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see java.util.List#toArray(java.lang.Object[])
	 */
	
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}




	@Override
	public void add(int location, T object) {
		throw new UnsupportedOperationException();
		
	}




	@Override
	public boolean add(T e) {
		if (e != null && !contains(e)) {
			dbList.add( (T) unwrap(e));
			added(e);
			return true;
		} else {
			return false;
		}
	}




	@Override
	public boolean addAll(int location, Collection<? extends T> collection) {
		throw new UnsupportedOperationException();
	}




	@Override
	public boolean addAll(Collection<? extends T> collection) {
		boolean result = collection.size() > 0;
		for (T o : collection) {
			result = result && add(o);
		}
		return result;
	}



	@Override
	public void clear() {
		dbList.clear();
		container.notifyChanged();
		
	}




	@Override
	public boolean containsAll(Collection<?> collection) {
		return false;
	}




	@Override
	public T get(int index) {
		return (T) wrap(dbList.get(index));
	}




	@Override
	public int indexOf(Object object) {
		throw new UnsupportedOperationException();
	}




	@Override
	public boolean isEmpty() {
		return dbList.isEmpty();
	}




	@Override
	public int lastIndexOf(Object object) {
		return indexOf(object);
	}




	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}




	@Override
	public ListIterator<T> listIterator(int location) {
		throw new UnsupportedOperationException();
	}




	@Override
	public T remove(int location) {
		throw new UnsupportedOperationException();
	}




	@Override
	public boolean remove(Object object) {
		boolean result = dbList.remove(unwrap(object));
		removed(object);
		return result;
	}




	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean result = collection.size() > 0;
		for (Object o : collection) {
			result = result && remove(o);
		}
		return result;
	}




	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}




	@Override
	public T set(int location, T object) {
		throw new UnsupportedOperationException();
	}




	@Override
	public int size() {
		return dbList.size();
	}




	@Override
	public List<T> subList(int start, int end) {
		throw new UnsupportedOperationException();
	}

	
}

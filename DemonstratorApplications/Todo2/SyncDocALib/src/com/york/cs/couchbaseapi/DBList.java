/*
 * 
 * @author Sebastian Salazar
 * email sebassalazart@hotmail.com
 * 
 * Credits: Code based on Pongo-Java POJO generator for MongoDB, Dr. Dimitris Kolovos
 */
package com.york.cs.couchbaseapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DBList<T extends DBObject> extends BasicDBListWrapper<T> {

	protected DBObject container;
	protected String containingFeature;
	protected boolean containment = false;

	public DBList(DBObject container, String feature, boolean containment) {
		super((ArrayList<HashMap<String, Object>>) container.getDbObject().get(
				feature));
		this.container = container;
		this.containingFeature = feature;
		this.containment = containment;
	}

	@Override
	public Iterator<T> iterator() {
		return new DBListIterator<T>(this);
	}

	protected void added(T t) {
		container.notifyChanged();
		if (containment) {
			t.setContainer(container);
			t.setContainingFeature(containingFeature);
		}else{
			t.addReferencedBy(container.getId());
		}
		
		
		
		
	};

	@Override
	protected void removed(Object o) {
		container.notifyChanged();
		if (containment) {
			((DBObject) o).setContainer(null);
		}else{
			((DBObject) o).removeReferencedBy(container.getId());
		}
	}

	@Override
	protected T wrap(Object o) {
		
		if (containment) {

			DBObject dbObject = ClassDBFactory.getInstance().createDBObject(
					(Map<String, Object>) o);
			//dbObject.container = container;

			return (T) dbObject;

		} else {

			return (T) ClassDBFactory.getInstance().resolveReference(o);
		}
	}

	@Override
	protected Object unwrap(Object o) {
		
		if (containment) {
			return ((DBObject) o).getDbObject();
		} else {
			return ((DBObject) o).createDBRef();
		}
	}

}

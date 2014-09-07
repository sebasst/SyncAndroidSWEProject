package com.york.cs.couchbaseapi.queryBuilder;

public class QueryProducer {

	protected String owningType = null;
	protected String address = null;
	protected String reducedAddress = null;
	protected boolean repeatedEntries = false;

	public QueryProducer(String address, String owningType) {
		this.address = address;
		setOwningType(owningType);
	}

	/**
	 * Destructive read.
	 * 
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	public String getOwningType() {
		return owningType;
	}

	public String getReducedAddress() {
		return reducedAddress;
	}

	public void setOwningType(String owningType) {
		this.owningType = owningType;
		String regex="\\.";
		String[] parts = owningType.split(regex);
		
		reducedAddress=address.substring(owningType.length() -(parts[parts.length-1]).length());
		
		
	}

	public boolean isRepeatedEntries() {
		return repeatedEntries;
	}

	public void setRepeatedEntries(boolean repeatedEntries) {
		this.repeatedEntries = repeatedEntries;
	}

}

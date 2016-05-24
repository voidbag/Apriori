package com.gachon.swdm.datamining.apriori.tskim;

import java.util.ArrayList;


/**
 * Wrapper class for item set(ArrayList).
 * @author KTS
 *
 * @param <T>
 */
public class ItemSet<T> {
	private ArrayList<T> itemSet;
	
	/**
	 * Default constructor.
	 */
	public ItemSet(){
		itemSet = new ArrayList<T>();
	}
	
	/**
	 * The constructor for itemSet with length is one.
	 * @param item
	 */
	public ItemSet(T item)
	{
		this();
		this.itemSet.add(item);
	}
	/**
	 * Simple getter of item set.
	 * @return the item set.
	 */
	public ArrayList<T> getItemSet(){
		
		return itemSet;
	}
	/**
	 * Simple setter of item set
	 * @param itemSet
	 */
	public void setItemSet(ArrayList<T> itemSet)
	{
		this.itemSet = itemSet;
	}
	
	

	/**
	 * Hash code is overridden.
	 */
	public int hashCode(){
		return itemSet.hashCode();
	}

	/**
	 * Equals method is overridden.
	 */
	public boolean equals(Object object){
		
		if(object instanceof ItemSetWithSupport)
		{
			ItemSetWithSupport<Integer> itemSet;
			itemSet = (ItemSetWithSupport<Integer>)object;
			return this.itemSet.equals(itemSet.getItemSet());
		}
		else if(object instanceof ArrayList)
		{
			return this.itemSet.equals(object);
		}
		else
			return false;
		
	}

	/**
	 * Overridden.
	 */
	public String toString(){
		return itemSet.toString();
	}

	/**
	 * @return the size of {@link #itemSet.}
	 */
	public int size(){
		return itemSet.size();
	}
	
	
}

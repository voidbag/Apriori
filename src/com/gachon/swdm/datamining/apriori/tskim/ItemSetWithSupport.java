package com.gachon.swdm.datamining.apriori.tskim;

import java.io.IOException;
import java.io.PrintWriter;



/**
 * Wrapper class for item set(ArrayList) whose parent is {@link #ItemSet}
 * @author KTS
 *
 * @param <T>
 */
public class ItemSetWithSupport<T> extends ItemSet<T>{
	
	private int support;
	public ItemSetWithSupport(){
		super();
		support = 0;
		
	}
	public ItemSetWithSupport(T item){
		super(item);
		support = 0;
	}

	public int getSupport() {
		return support;
	}

	
	public void setSupport(int support) {
		this.support = support;
	}
	
	/**
	 * This method increments itemset's support.
	 * This method is called within Trie.
	 */
	public void incrementSupport(){
		support+=1;
	}
	
	/**
	 * This method prints the item set of this object.
	 */
	public void printSetInfo(){
		System.out.print("Support: "+support);
		System.out.println(" Set: "+getItemSet().toString());
		
	}
	
	
	/**
	 * This method writes the information of item set of this object.
	 */
	public void writeSetInfo(PrintWriter out) throws IOException{
		out.print("Support: "+support);
		out.println(" Set: "+getItemSet().toString());
	}
		
	
}

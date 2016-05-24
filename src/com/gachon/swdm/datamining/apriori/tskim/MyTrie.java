package com.gachon.swdm.datamining.apriori.tskim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This is hash tree (Trie).
 * The hash function that determines child node is hashmap.
 * @author KTS
 *
 */
public class MyTrie {

	private Node root;
	int itemLength;
	int minSupport;

	public MyTrie(int itemLength, int minSupport) {
		root = new Node();
		this.itemLength = itemLength;
		this.minSupport = minSupport;
	}

	/**
	 * This method inserts item set into trie.
	 * @param itemSet the itemSet to be inserted.
	 */
	public void insert(ArrayList<Integer> itemSet) {

		root.insert(itemSet, 1);
	}

	/**
	 * This method is called when database is scanned.
	 * @param transaction the transaction that will increment item sets' support of hash tree.
	 */
	public void searchAndIncrement(ArrayList<Integer> transaction) {
		root.searchAndIncrement(transaction, 0, 1);

	}

	/**
	 * This method gets large item set from hash tree (trie).
	 * @return the set of item set with support >= minimum support.
	 */
	public HashSet<ItemSetWithSupport<Integer>> getLargeSets() {
		HashSet<ItemSetWithSupport<Integer>> result = new HashSet<ItemSetWithSupport<Integer>>();
		root.getLargeSet(result);
		return result;
	}
	
	/**
	 * This method is for debugging.
	 * @return
	 */
	public HashSet<ItemSetWithSupport<Integer>> getEverySets() {
		HashSet<ItemSetWithSupport<Integer>> result = new HashSet<ItemSetWithSupport<Integer>>();
		root.getEverySet(result);
		return result;
	}


	class Node {
		Map<Integer, Object> childs;

		public Node() {
			childs = new HashMap<Integer, Object>();
		}

		/**
		 * This method inserts itemSet.
		 * @param itemSet the itemSet to be inserted into trie.
		 * @param depth the depth of insert. First depth is 1.
		 */
		public void insert(ArrayList<Integer> itemSet, int depth) {

			Object child = childs.get(itemSet.get(depth - 1));

			if (child == null) {
				ItemSetWithSupport<Integer> newItemSet;
				newItemSet = createItemSet(itemSet);
				childs.put(itemSet.get(depth - 1), newItemSet);
			} else if (child instanceof Node) {
				((Node) child).insert(itemSet, depth + 1);
				
			} else if (child instanceof ItemSetWithSupport) {
				
				ItemSetWithSupport<Integer> child1 = (ItemSetWithSupport<Integer>) child;
				ItemSetWithSupport<Integer> child2 = createItemSet(itemSet);
				child = new Node();
				childs.put(itemSet.get(depth-1), child);
				
				((Node) child).insertTwo(child1, child2, depth + 1);
			}		

		}

		/**
		 * This method is called when two item sets's position is same.
		 * It create empty inner node, and traverse to insert this two item set.
		 * @param child1 first item set.
		 * @param child2 second item set.
		 * @param depth
		 */
		private void insertTwo(ItemSetWithSupport<Integer> child1,
				ItemSetWithSupport<Integer> child2, int depth) {
			if ( (child1.getItemSet().get(depth - 1) != child2
							.getItemSet().get(depth - 1))) {
				childs.put(child1.getItemSet().get(depth - 1), child1);
				childs.put(child2.getItemSet().get(depth - 1), child2);
			} else {
				Node newNode = new Node();
				childs.put(child1.getItemSet().get(depth - 1), newNode);
				newNode.insertTwo(child1, child2, depth + 1);
			}
		}

		/**
		 * This method gets every ItemSet whose support is greater than or equal
		 * to minimum support.
		 * 
		 * @param result
		 *            The result ArrayList into which item set with large
		 *            support will be stored. The result HashSet must be
		 *            initialized.
		 */
		public void getLargeSet(HashSet<ItemSetWithSupport<Integer>> result) {

			HashMap<Integer, Object> map = (HashMap<Integer, Object>) childs;
			Collection<Object> collection = map.values();
			for (Object child : collection) {
				if (child instanceof Node) {
					((Node) child).getLargeSet(result);
					
				} else if (child instanceof ItemSetWithSupport) {
					ItemSetWithSupport<Integer> itemSet;
					itemSet = (ItemSetWithSupport<Integer>) child;
					
					if (itemSet.getSupport() >= minSupport)
						result.add(itemSet);
				}
			}
		}
		
		
		/**
		 * This method is for debugging.
		 * @param result
		 */
		public void getEverySet(HashSet<ItemSetWithSupport<Integer>> result) {

			Collection<Object> collection = childs.values();
			for (Object child : collection) {
				if (child instanceof Node) {
					((Node) child).getEverySet(result);
					
				} else if (child instanceof ItemSetWithSupport) {
					ItemSetWithSupport<Integer> itemSet;
					itemSet = (ItemSetWithSupport<Integer>) child;					
						result.add(itemSet);
				}
			}
		}
		
		

		/**
		 * This method increments the support of corresponding item set in hash tree (trie).
		 * It is called when database is scanned.
		 * @param transaction the input transaction.
		 * @param start the start position of the input transaction.
		 * @param depth the depth of increment method. First depth is 1.
		 */
		public void searchAndIncrement(ArrayList<Integer> transaction,
				int start, int depth) {
			Object child;
			int end = transaction.size() - (itemLength - depth + 1);

			for (int i = start; i <= end; i++) {
				child = childs.get(transaction.get(i));
				if (child == null)
					continue;
				if (child instanceof Node) {
					((Node) child).searchAndIncrement(transaction, i + 1,
							depth + 1);
				} else if (child instanceof ItemSetWithSupport) {
					List<Integer> subset = transaction.subList(i + 1,
							transaction.size());
					ItemSetWithSupport<Integer> itemSet = (ItemSetWithSupport<Integer>) child;
					if (subset.containsAll(itemSet.getItemSet().subList(depth,
							itemSet.size())) == true) {
						itemSet.incrementSupport();
					}
				}
			}
		}
	}

	
	/**
	 * This method is for injecting {@link #itemSetWithSupport} object.
	 * This method generates the {@link #itemSetWithSupport} object wrapping itemset ArrayList.
	 * @param itemSet to be wrapped.
	 * @return the itemSetWithSupport object wrapping input.
	 */
	private static ItemSetWithSupport<Integer> createItemSet(
			ArrayList<Integer> itemSet) {
		ItemSetWithSupport<Integer> wrappedItemSet = new ItemSetWithSupport<Integer>();
		wrappedItemSet.setItemSet(itemSet);

		return wrappedItemSet;
	}

}

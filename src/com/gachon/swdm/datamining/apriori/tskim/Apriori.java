package com.gachon.swdm.datamining.apriori.tskim;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * Data mining.
 * Apriori algorithm.
 * Taeksang Kim
 * 
 * @author KTS
 *
 */
public class Apriori {

	private String inFilePath;
	private String outFilePath;
	private List<HashSet<ItemSetWithSupport<Integer>>> largeSetList;
	private int minSupport;

	public Apriori() {
		largeSetList = new ArrayList<HashSet<ItemSetWithSupport<Integer>>>();
	}

	/**
	 * Simple getter of input file path.
	 * @return file input path.
	 */
	public String getInFilePath(){
		return this.inFilePath;
	}

	/**
	 * Simple getter of output file path
	 * @return file input path.
	 */
	public String getOutFilePath() {
		return this.outFilePath;
	}


	/**
	 * Simple setter of input file path.
	 * @param path the input file path
	 */
	public void setInFilePath(String path) {
		this.inFilePath = path;
	}

	/**
	 * Simple setter of output file path.
	 * @param path the output file path.
	 */
	public void setOutFilePath(String path) {
		this.outFilePath = path;
	}

	/**
	 * Simple getter of minimum support.
	 * @return {@link #minSupport}
	 */
	public int getMinSupport() {
		return minSupport;
	}

	/**
	 * Simple Setter of minium support.
	 * @param minSupport
	 */
	public void setMinSupport(int minSupport) {
		this.minSupport = minSupport;
	}
	

	
	public static void main(String[] args) throws IOException {
		//Apriori.doExecutionWithPrint("983.TXT",2);
		Apriori.doExecutionWithFile("983.TXT", "output-983-support_32.txt", 32);
		//Apriori.doExecutionWithFile("983.TXT", "output-983-support_2.txt", 2);
	}
	
	/**
	 * This method print every Ls to standard outuput stream.
	 */
	public void printAll() {
		HashSet<ItemSetWithSupport<Integer>> largeSet;

		for (int i = 0; i < largeSetList.size(); i++) {
			largeSet = largeSetList.get(i);
			if (largeSet.size() != 0) {
				System.out.println();
				System.out.println("====L" + (i + 1) + "====");
			}
			for (ItemSetWithSupport<Integer> itemSet : largeSet) {
				itemSet.printSetInfo();
			}
		}

	}


	/**
	 * This method writes every result into file.
	 * @param t1 is the start time when this algorithm started.
	 * @return The time when write operation is done.
	 * @throws IOException could be thrown by file I/O.
	 */
	public long writeAll(long t1) throws IOException {
		HashSet<ItemSetWithSupport<Integer>> largeSet;
		PrintWriter bOut = new PrintWriter(new BufferedWriter(new FileWriter(outFilePath)));
		bOut.println("Data mining");
		bOut.println("[Apriori algorithm]");
		bOut.println("Taeksang Kim");
		bOut.println("Input file: "+ inFilePath);
		bOut.println("Minimum support: "+minSupport);
		long t2;
		for (int i = 0; i < largeSetList.size(); i++) {
			largeSet = largeSetList.get(i);
			if (largeSet.size() != 0) {
				
				
				
				bOut.println();
				bOut.println("====L" + (i + 1) + "====");
			}
			for (ItemSetWithSupport<Integer> itemSet : largeSet) {
				itemSet.writeSetInfo(bOut);
			}
		}
		t2 = System.currentTimeMillis();
		bOut.println();
		bOut.println(" "+(t2-t1)+"(ms)");
		
		bOut.flush();
		bOut.close();
		return t2;

	}

	/**
	 * This method runs apriori algorithm.
	 * @throws FileNotFoundException is thrown when input file path is wrong.
	 */
	private void doApriori() throws FileNotFoundException {

		Scanner scanner;
		int k;
		HashSet<ItemSetWithSupport<Integer>> largeSet;
		HashSet<ItemSetWithSupport<Integer>> candidateSet;
		MyTrie trie;

		scanner = new Scanner(new BufferedInputStream(new FileInputStream(
				new File(inFilePath))));

		System.out.println("L1 is being generated...");
		largeSet = find_frequent_1_itemSet(scanner);
		scanner.close();
		System.out.println("L1 has been generated. Size: " + largeSet.size());
		System.out.println();
		largeSetList.add(largeSet);

		for (k = 1; largeSetList.get(k - 1).size() != 0; k++) {
			System.out.println("C" + (k + 1) + " is being generated... using L" + k
					+ " whose size is " + largeSetList.get(k - 1).size());

			candidateSet = apriori_gen(largeSetList.get(k - 1), k + 1);

			System.out.println("C" + (k + 1) + " has been generated. Size: "
					+ candidateSet.size());

			System.out.println("Trie is being built... using C" + (k + 1));
			trie = makeTrie(k + 1, candidateSet);
			System.out.println("Trie has been generated.");
			scanner = new Scanner(new BufferedInputStream(new FileInputStream(
					new File(inFilePath))));

			System.out.println("Database is being scanned...");
			incrementSupport(scanner, trie);
			System.out.println("Database scan is completed.");
			scanner.close();
			largeSetList.add(trie.getLargeSets());
			System.out.println("L" + (k + 1) + " has been generated. Size: "
					+ largeSetList.get(k).size());
			System.out.println();

		}
	}

	/**
	 * This method scans database and let each transaction increment its subset.
	 * @param scanner of input file.
	 * @param trie which was created by {@link #makeTrie(int, HashSet)}
	 */
	private void incrementSupport(Scanner scanner, MyTrie trie) {
		String line;
		Scanner lineParser;
		ArrayList<Integer> transaction;

		try {
			while (true) {
				line = scanner.nextLine();
				lineParser = new Scanner(line);
				transaction = new ArrayList<Integer>();
				try {
					while (true) {
						transaction.add(lineParser.nextInt());
					}
				} catch (InputMismatchException e) {
				} catch (NoSuchElementException e) {
				}
				Collections.sort(transaction);
				trie.searchAndIncrement(transaction);
			}
		} catch (InputMismatchException e) {
		} catch (NoSuchElementException e) {
		}

	}

	/**
	 * This method makes trie.
	 * @param itemLength the item length by which trie is build.
	 * @param candidateSet will be stored into the trie.
	 * @return The trie where candidate set is.
	 */
	private MyTrie makeTrie(int itemLength,
			HashSet<ItemSetWithSupport<Integer>> candidateSet) {
		MyTrie trie = new MyTrie(itemLength, minSupport);

		for (ItemSetWithSupport<Integer> itemSet : candidateSet) {
			trie.insert(itemSet.getItemSet());
		}
		return trie;
	}

	/**
	 * This method generates the k-th C.
	 * @param largeSet (k-1)-th L.
	 * @param k indicates larget set's numbering.
	 * @return k-th candidate set.
	 */
	private HashSet<ItemSetWithSupport<Integer>> apriori_gen(
			HashSet<ItemSetWithSupport<Integer>> largeSet, int k) {
		int i, j;
		int lastIndex;
		int numLastLarge;
		HashSet<ItemSetWithSupport<Integer>> candidate = new HashSet<ItemSetWithSupport<Integer>>();
		ArrayList<Integer> list1, list2;
		ArrayList<Integer> mergedSet;
	
		ArrayList<ItemSetWithSupport<Integer>> arrayList1 = new ArrayList<ItemSetWithSupport<Integer>>();
		arrayList1.addAll(largeSet);
		lastIndex = k - 2;

		numLastLarge = largeSet.size();
		for (i = 0; i < numLastLarge; i++) {
			for (j = i + 1; j < numLastLarge; j++) {
				list1 = arrayList1.get(i).getItemSet();
				list2 = arrayList1.get(j).getItemSet();

				if (lastIndex == 0
						|| (list1.get(lastIndex) != list2.get(lastIndex) && myEquals(
								list1, list2))) {

					mergedSet = joinSet(list1, list2);

					if (has_infrequent_subset(mergedSet, largeSet) == false) {
						ItemSetWithSupport<Integer> itemSet = new ItemSetWithSupport<Integer>();
						itemSet.setItemSet(mergedSet);
						candidate.add(itemSet);
					}
				}
			}
		}

		return candidate;
	}

	/**
	 * This method checks if given list is same except last element.<br/>
	 * This method is used, because the equals method of JAVA uses {@link #Iterator} to traverse.
	 * It is about <strong>2 times slower</strong> to use {@link #Iterator} than using for loop.
	 * @param list1 will be compared with list2.
	 * @param list2 will be compared with list1.
	 * @return <strong>true</strong> if list1 equals to list2 except last element.<br/><strong> false</strong>, otherwise.
	 */
	private boolean myEquals(ArrayList<Integer> list1, ArrayList<Integer> list2) {
		int i;
		int end = list1.size() - 1;
		for (i = 0; i < end; i++) {
			if (list1.get(i) != list2.get(i))
				return false;
		}
		return true;
	}

	/**
	 * This method does natural join operation with given two list.<br/>
	 * Natural join is done by ascending order.
	 * @param list1 the list to be joined.
	 * @param list2 another list to be joined.
	 * @return the merged list.
	 */
	private ArrayList<Integer> joinSet(ArrayList<Integer> list1,
			ArrayList<Integer> list2) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int lastIndex = list1.size() - 1;

		if (list1.get(lastIndex) > list2.get(lastIndex)) {
			result.addAll(list2);
			result.add(list1.get(list1.size() - 1));
		} else // if(list1.get(list1.size()-1) < list2.get(list2.size()-1))
		{
			result.addAll(list1);
			result.add(list2.get(list2.size() - 1));
		}
		return result;
	}

	
	/**
	 * This method checks if the previous large set has every subset of list.
	 * @param list whose subsets(nCn-1) will be checked.
	 * @param largeSet previous large set.
	 * @return <strong>true</strong><br/>, if previous large set doesn't contain every subset of list.
	 * Otherwise, <strong>false</strong>
	 */
	private boolean has_infrequent_subset(ArrayList<Integer> list,
			HashSet<ItemSetWithSupport<Integer>> largeSet) {
		ArrayList<Integer> searchSet = new ArrayList<Integer>();
		ItemSetWithSupport<Integer> searchItemSet = new ItemSetWithSupport<Integer>();

		searchSet.addAll(list);// ÀÇ½É
		searchItemSet.setItemSet(searchSet);

		for (int i = 0; i < list.size(); i++) {
			searchSet.remove(i);
			if (largeSet.contains(searchItemSet) != true) {
				return true;
			}
			searchSet.clear();
			searchSet.addAll(list);
		}

		return false;

	}

	/**
	 * This method finds 1-length frequent item set from input file.
	 * @param scanner of input file.
	 * @return the set of 1-length item set whose support is more than {@link #minSupport}.
	 */
	private HashSet<ItemSetWithSupport<Integer>> find_frequent_1_itemSet(
			Scanner scanner) {
		Integer item;
		ItemSetWithSupport<Integer> itemSet;
		HashMap<Integer, ItemSetWithSupport<Integer>> map = new HashMap<Integer, ItemSetWithSupport<Integer>>();
		HashSet<ItemSetWithSupport<Integer>> result;

		try {
			while (true) {
				item = scanner.nextInt();
				itemSet = map.get(item);
				if (itemSet == null) {
					itemSet = new ItemSetWithSupport<Integer>(item);
					map.put(item, itemSet);
				}
				itemSet.incrementSupport();
			}
		} catch (InputMismatchException e) {
		} catch (NoSuchElementException e) {
		}

		result = new HashSet<ItemSetWithSupport<Integer>>();

		for (ItemSetWithSupport<Integer> candidate : map.values()) {
			if (candidate.getSupport() >= minSupport) {
				result.add(candidate);
			}
		}

		return result;

	}

	
	/**
	 * This method simulates apriori algorithm by printing result to stanrdard output stream.
	 * @param filePath input file path.
	 * @param support minimum support.
	 * @throws FileNotFoundException is thrown when there isn't the file whose name is filePath.
	 */ 
	public static void doExecutionWithPrint(String filePath, int support) throws FileNotFoundException {
		System.out.println("Data mining");
		System.out.println("[Apriori algorithm]");
		System.out.println("201133188 Kim Taek Sang");
		System.out.println("Input file: "+filePath);
		System.out.println("Minimum support: "+support);
		long t1, t2;
		t1 = System.currentTimeMillis();
		Apriori apriori = new Apriori();
		apriori.setInFilePath(filePath);
		apriori.setMinSupport(support);
		apriori.doApriori();

		apriori.printAll();
		t2 = System.currentTimeMillis();

		System.out.println();
		System.out.println((t2 - t1) + "(ms)");

	}
	
	/**
	 * This method does simulates apriori algorithm, by writing the result into file whose file path is outPath.
	 * @param inPath input file path.
	 * @param outPath output file path.
	 * @param support minimum support
	 * @throws IOException is thrown when I/O operation is abnormal.
	 */
	public static void doExecutionWithFile(String inPath, String outPath, int support) throws IOException {
		
		long t1, t2;
		
		t1 = System.currentTimeMillis();
		Apriori apriori = new Apriori();
		apriori.setInFilePath(inPath);
		apriori.setOutFilePath(outPath);
		
		apriori.setMinSupport(support);
		apriori.doApriori();

		t2= apriori.writeAll(t1);

		System.out.println();
		System.out.println((t2 - t1) + "(ms)");

	}

}

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class HuffmanCompressor {
	
	// for space saving calculations, counts number of bits
	private static int huffmanCodeSize = 0;
	private static int docLength = 0;
	
	// used to store the huffman table
	/*
	 * I used an array to store the values because it is easy to add and sort and remove, 
	 * and simply I am more comfortable with it. With ArrayList managing the size for me,
	 * there is no reason not to use it.
	 */
	static ArrayList<HuffmanCoded> code = new ArrayList<HuffmanCoded>();
	
	// to make huffman nodes sortable
	private static class HuffmanNodeComparator implements Comparator<HuffmanNode> {
		@Override
		public int compare(HuffmanNode n1,HuffmanNode n2){
    		return n1.compareTo(n2);
		}
	}
	
	// to store huffman table information while still using same comparator as above
	private static class HuffmanCoded extends HuffmanNode {
		private String s;
		public HuffmanCoded(Character chari, int frequency, String s) {
			super(chari, frequency, null, null);
			this.s = s;
		}
		
		public String getS() {
			return s;
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		
		// makes and traverses tree, adding huffman table values to ArrayList
		// prints error if file cannot be found
		try {
			HuffmanNode head = makeTree(generateList(args));
			traverseTree(head, "");
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find input file.");
			return;
		}
		
		// sorts huffman table values, most frequent first
		Collections.sort(code);
		Collections.reverse(code);
		// prints out huffman table values
		for (HuffmanCoded s : code) {
			System.out.println(s.getInChar() + ": " + s.getFrequency() + ": " + s.s);
		}
		// reads and writes coding files
		// prints error if file cannot be found
		try {
			produceOutput(args);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find encoding file.");
			return;
		} catch (NoSuchElementException e) {
			// if an character does not have a coded value
			System.out.println(e);
			return;
		}
		// states savings with 2 decimal places
		System.out.printf("Completed: Saved %.2f%%!", generateSavings());
	}
	
	// makes the arraylist of huffman nodes
	public static ArrayList<HuffmanNode> generateList(String[] args) throws IOException {
		FileReader fr = new FileReader(args[0]);
		BufferedReader br = new BufferedReader(fr);
		
		// holds characters in file
		ArrayList<Character> characters = new ArrayList<Character>();
		// holds nodes from characters in file
		ArrayList<HuffmanNode> nodes = new ArrayList<HuffmanNode>();
		
		// makes an array of all characters in file
		int s = br.read();
		while (s >= 0) {
			characters.add((char)s);
			s = br.read();
		}
		
		br.close();
		
		System.out.println("Sorting array...");
		
		// sorts characters into alphabetical order
		Collections.sort(characters);
		
		System.out.println("Array sorted.");
		
		// makes huffman nodes for characters
		// because the characters are already in order, code can simply run until it 
		// runs into a new character, and all the characters before it will be all the ones
		// in the document
		int currentCharCounter = 0;
		Character currentChar = null;
		for (Character c : characters) {
			// if the character hasnt changed
			if (c.equals(currentChar)) {
				currentCharCounter++;
			} else {
				// if the character has changed
				if (currentChar != null) {
					HuffmanNode newNode = new HuffmanNode(currentChar, currentCharCounter, null, null);
					nodes.add(newNode);
					currentChar = c;
					currentCharCounter = 1;
				} else {
					// only used for first character
					currentChar = c;
				}
			}
		}
		// have to add the last character too
		HuffmanNode newNode = new HuffmanNode(currentChar, currentCharCounter, null, null);
		nodes.add(newNode);
		
		// sorts nodes by frequency
		Collections.sort(nodes, new HuffmanNodeComparator());
		
		return nodes;
	}
	
	public static HuffmanNode combineNodes(HuffmanNode left, HuffmanNode right) {
		HuffmanNode newNode = new HuffmanNode(null, left.getFrequency() + right.getFrequency(), 
				left, right);
		return newNode;
	}
	
	// gets first two nodes, which are the smallest, because array is
	// sorted by frequency, combines them, adds it back into the array
	// and then resorts it
	public static HuffmanNode makeTree(ArrayList<HuffmanNode> nodes) {
		while (nodes.size() > 1) {
			HuffmanNode newNode = combineNodes(nodes.get(0), nodes.get(1));
			nodes.set(0, newNode);
			nodes.remove(1);
			Collections.sort(nodes, new HuffmanNodeComparator());
		}
		
		return nodes.get(0);
	}
	
	// runs through all nodes of trees, only adding the ones with character values 
	// to the array
	public static void traverseTree(HuffmanNode head, String s) {
		if (head == null) {
			
		} else if (head.getInChar() != null) {
			code.add(new HuffmanCoded(head.getInChar(), head.getFrequency(), s));
		} else {
			traverseTree(head.getLeft(), s + "0");
			traverseTree(head.getRight(), s + "1");
		}
	}
	
	// reads the file to be coded, then writes the file with the code
	public static void produceOutput(String[] files) throws IOException, NoSuchElementException {
		FileReader fr = new FileReader(files[1]);
		BufferedReader br = new BufferedReader(fr);
		
		PrintWriter pr = new PrintWriter(files[2]);
		
		int s = br.read();
		while (s >= 0) {
			// stores length of document to calculate space saving
			docLength ++;
			// writes file
			int i = 0;
			boolean found = false;
			// only runs until character is found, starting at most common characters
			while (!found) {
				if (i >= code.size()) {
					// if the character does not have a coded value
					throw new NoSuchElementException("Character " + (char)s + " not found.");
				} else if (code.get(i).getInChar().equals((char)s)) {
					// when the character is found in the array
					pr.print(code.get(i).getS());
					huffmanCodeSize += code.get(i).getS().length();
					found = true;
				} else {
					// character not found, moves on to next iteration
					i++;
				}
			}
			s = br.read();
		}
		
		br.close();
		pr.close();
		
	}
	
	// generates space savings
	public static double generateSavings() {
		double savings = ((double)(docLength * 8) - (double)huffmanCodeSize) / (docLength * 8);
		
		return savings * 100;
	}

}

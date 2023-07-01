import java.io.*;
import java.util.*;

public class Matching
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;

				command(input);
			}
			catch (IOException e)
			{
				System.out.println("Wrong input : " + e.toString());
			}
		}
	}

	private static void command(String input) {
		char operation = input.charAt(0);
		String inputData = input.substring(2);
		switch (operation) {
			case '<':
				readFile(inputData);
				break;
			case '@':
				int index = Integer.parseInt(inputData);
				printData(index);
				break;
			case '?':
				MyLinkedList<ListItem> result = searchPattern(inputData);
				System.out.println(result);
				break;
			case '/':
				System.out.println(deleteString(inputData));
				break;
			case '+':
				addLine(inputData);
				System.out.println(lineArray.size());
				break;
		}
	}

	private static final int LEN_STRING = 6;
	private static final ArrayList<String> lineArray = new ArrayList<>();
	private static HashTable<String, ListItem> table;


	private static void readFile(String fileName) {
		try {
			lineArray.clear();
			table = new HashTable<>();
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			while (true) {
				String line = br.readLine();
				if (line!=null) {
					addLine(line);
				} else break;
			}

		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private static void updateTableByLine(String line) {
		int lineNum = lineArray.size();
		for (int i=0; i<=line.length()-LEN_STRING; i++) {
			String substring = line.substring(i, i+LEN_STRING);
			ListItem loc = new ListItem(lineNum, i+1);
			updateTableByString(substring, loc);
		}
	}

	private static void updateTableByString(String substring, ListItem loc) {
		table.insert(substring, loc);
	}

	private static void printData(int index) {
		AVLTree<String, ListItem> tree = table.getItem(index);
		System.out.println(tree);
	}

	private static MyLinkedList<ListItem> searchPattern(String pattern) {
		if (pattern.length() < 6)
			throw new IllegalArgumentException();
		else if (pattern.length() == 6) {
			return table.search(pattern);
		} else {
			MyLinkedList<ListItem> initialSearch = table.search(pattern.substring(0, LEN_STRING));
			MyLinkedList<ListItem> tmp = new MyLinkedList<>();
			for (ListItem item: initialSearch) {
				tmp.addLast(item.clone());
			}
			if (tmp.isEmpty())
				return tmp;
			else {
				MyLinkedList<ListItem> prevSearch = tmp.clone();
				tmp.clear();
				for (int i = 1; i <= pattern.length() - LEN_STRING; i++) {
					MyLinkedList<ListItem> searchResult = table.search(pattern.substring(i, i+LEN_STRING));
					if (searchResult.isEmpty()) {
						return searchResult;
					} else {
						for (ListItem currItem : searchResult) {
							for (ListItem prevItem: prevSearch) {
								if (currItem.isDistance(i, prevItem)) {
									tmp.addLast(prevItem);
									break;
								}
							}
						}
						prevSearch = tmp.clone();
						tmp.clear();
					}
				}
				return prevSearch;
			}
		}
	}

	private static int deleteString(String substring) {
		int numDeleted;
		MyLinkedList<ListItem> searchResult;
		Stack<String> modifiedLines = new Stack<>();
		MyLinkedList<ListItem> ranges;

		searchResult = table.search(substring);
		numDeleted = searchResult.size();

		modifiedLines = getModifiedLines(searchResult);
		ranges = getDeletionRanges(searchResult);
		for (ListItem deletionStart: ranges) {
			deleteListItemFromStartRange(deletionStart, substring);
		}
		for (int i=0; i<ranges.size(); i++) {
			int lineIndex = ranges.get(i).lineNum-1;
			lineArray.set(lineIndex, modifiedLines.pop());
		}
		for (ListItem hashingStart: ranges) {
			hashListItemFromStartRange(hashingStart);
		}
		return numDeleted;
	}

	private static Stack<String> getModifiedLines(MyLinkedList<ListItem> searchResult) {
		Stack<String> modifiedLines = new Stack<>();
		StringBuilder sb = new StringBuilder();
		int prevLineNum = -1;

		for (int i=searchResult.size()-1; i>=0; i--) {
			ListItem range = searchResult.get(i);
			if (range.lineNum != prevLineNum) {
				modifiedLines.add(sb.toString());
				sb = new StringBuilder(lineArray.get(range.lineNum-1));
			}
			sb.delete(range.index-1, Math.min(sb.length(), range.index-1+LEN_STRING));
			prevLineNum = range.lineNum;
		}

		if (!searchResult.isEmpty()) {
			modifiedLines.remove(0);
			modifiedLines.add(sb.toString());
		}
		return modifiedLines;
	}

	private static MyLinkedList<ListItem> getDeletionRanges(MyLinkedList<ListItem> searchResult) {
		// Works based on the assumption that the search result is already sorted
		MyLinkedList<ListItem> deletionRanges = new MyLinkedList<>();
		int prevLineNum = 0;

		for (ListItem range: searchResult) {
			if (range.lineNum != prevLineNum) {
				ListItem start = new ListItem(range.lineNum, Math.max(range.index-LEN_STRING+1, 1));
				deletionRanges.addLast(start);
			}
			prevLineNum = range.lineNum;
		}

		return deletionRanges;
	}


	private static void deleteListItemFromStartRange(ListItem deletionStart, String substring) {
		int lineIndex = deletionStart.lineNum-1;
		int startCharIndex = deletionStart.index-1;
		String lineToBeDeleted = lineArray.get(lineIndex);
		for (int i=startCharIndex; i<=lineToBeDeleted.length()-LEN_STRING; i++) {
			ListItem deletionPlace = new ListItem(deletionStart.lineNum, i+1);
			table.delete(lineToBeDeleted.substring(i, i+LEN_STRING), deletionPlace);
		}
		String processedLine = lineToBeDeleted.replace(substring, "");
		lineArray.set(lineIndex, processedLine);
	}

	private static void hashListItemFromStartRange(ListItem hashingStart) {
		int lineIndex = hashingStart.lineNum - 1;
		int startCharIndex = hashingStart.index - 1;
		String lineToBeHashed = lineArray.get(lineIndex);
		if (lineToBeHashed.length() >= LEN_STRING) {
			for (int i = startCharIndex; i <= lineToBeHashed.length() - LEN_STRING; i++) {
				ListItem place = new ListItem(hashingStart.lineNum, i + 1);
				updateTableByString(lineToBeHashed.substring(i, i + LEN_STRING), place);
			}
		}
	}

	private static void addLine(String line) {
		lineArray.add(line);
		updateTableByLine(line);
	}

}

class HashTable<T extends Comparable, L extends Comparable> {
	static final int TABLE_SIZE = 100;
	private AVLTree table[];

	public HashTable() {
		table = new AVLTree[TABLE_SIZE];
		for (int i=0; i<TABLE_SIZE; i++) {
			table[i] = new AVLTree<T, L>();
		}
	}

	private int hash(T object) {
		if (object instanceof String) {
			int hashCode = 0;
			for (int i=0; i<((String) object).length(); i++) {
				hashCode += (int) ((String) object).charAt(i);
			}
			return hashCode % HashTable.TABLE_SIZE;
		} else {
			return object.hashCode() % HashTable.TABLE_SIZE;
		}
	}

	public void insert(T treeItem, L listItem) {
		int slot = hash(treeItem);
		AVLTree tree = table[slot];
		if (tree==null) {
			table[slot] = new AVLTree();
		}
		table[slot].insert(treeItem, listItem);
	}

	public MyLinkedList<L> search(T treeItem) {
		int slot = hash(treeItem);
		MyLinkedList<L> searchResult;
		if (table[slot] == null) {
			searchResult = new MyLinkedList<>();
		} else {
			AVLNode<T, L> resultNode = table[slot].search(treeItem);
			if (resultNode == AVLTree.NIL)
				searchResult = new MyLinkedList<>();
			else
				searchResult = resultNode.list;
		}
		return searchResult;
	}

	public void delete(T treeItem, L listItem) {
		int slot = hash(treeItem);
		if (table[slot] != null)
			table[slot].delete(treeItem, listItem);
	}

	public AVLTree getItem(int i) {
		return table[i];
	}

}


class AVLTree<T extends Comparable, L extends Comparable> {
	private AVLNode root;
	static final AVLNode NIL = new AVLNode(null, null, null, 0, null);

	public AVLTree() {
		root = NIL;
	}

	@Override
	public String toString() {
		ArrayList<AVLNode<T, L>> allNodes = preTraverse();

		if (!allNodes.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (AVLNode<T, L> node : allNodes) {
				sb.append(node.toString());
				sb.append(" ");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}

		return "EMPTY";
	}

	private ArrayList<AVLNode<T,L>> preTraverse() {
		ArrayList<AVLNode<T, L>> nodeArrayList = new ArrayList<>();
		visit(root, nodeArrayList);
		return nodeArrayList;
	}

	private void visit(AVLNode<T, L> currNode, ArrayList<AVLNode<T,L>> nodeArrayList) {
		if (currNode != NIL) {
			nodeArrayList.add(currNode);
			visit(currNode.left, nodeArrayList);
			visit(currNode.right, nodeArrayList);
		}
	}


	public AVLNode<T, L> search(T item) {
		return searchItem(root, item);
	}
	private AVLNode<T, L> searchItem(AVLNode<T, L> currNode, T item) {
		if (currNode == NIL)
			return NIL;
		else if (item.equals(currNode.item))
			return currNode;
		else if (item.compareTo(currNode.item) < 0)
			return searchItem(currNode.left, item);
		else
			return searchItem(currNode.right, item);
	}

	public void insert(T treeItem, L listItem) {
		root = insertItem(root, treeItem, listItem);
	}
	private AVLNode<T, L> insertItem(AVLNode<T, L> currNode, T treeItem, L listItem) {
		if (currNode == NIL) {
			currNode = new AVLNode(treeItem, listItem);
		} else if (treeItem.compareTo(currNode.item) < 0) {
			currNode.left = insertItem(currNode.left, treeItem, listItem);
			currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
			int type = needBalance(currNode);
			if (type != NO_NEED)
				currNode = balanceAVL(currNode, type);
		} else if (treeItem.compareTo(currNode.item) > 0) {
			currNode.right = insertItem(currNode.right, treeItem, listItem);
			currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
			int type = needBalance(currNode);
			if (type != NO_NEED)
				currNode = balanceAVL(currNode, type);
		} else {
			currNode.list.add(listItem);
		}
		return currNode;
	}

	public void delete(T treeItem, L listItem) {
		root = deleteItem(root, treeItem, listItem);
	}

	private AVLNode<T, L> deleteItem(AVLNode<T, L> currNode, T treeItem, L listItem) {
		if (currNode == NIL)
			return NIL;
		else {
			if (treeItem.compareTo(currNode.item) == 0) {
				currNode.list.remove(listItem);
				if (currNode.list.isEmpty()) {
					currNode = deleteNode(currNode);
				}
			} else if (treeItem.compareTo(currNode.item) < 0) {
				currNode.left = deleteItem(currNode.left, treeItem, listItem);
				currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
				int type = needBalance(currNode);
				if (type != NO_NEED)
					currNode = balanceAVL(currNode, type);
			} else {
				currNode.right = deleteItem(currNode.right, treeItem, listItem);
				currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
				int type = needBalance(currNode);
				if (type != NO_NEED)
					currNode = balanceAVL(currNode, type);
			}
		}
		return currNode;
	}

	private AVLNode<T, L> deleteNode(AVLNode<T, L> currNode) {
		if ((currNode.left == NIL) && (currNode.right == NIL))
			return NIL;
		else if (currNode.left == NIL)
			return currNode.right;
		else if (currNode.right == NIL)
			return currNode.left;
		else {
			returnPair<T, L> rPair = deleteMinItem(currNode.right);
			currNode.item = rPair.item;
			currNode.list = (MyLinkedList<L>) rPair.list;
			currNode.right = rPair.node;
			currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
			int type = needBalance(currNode);
			if (type != NO_NEED)
				currNode = balanceAVL(currNode, type);
			return currNode;
		}
	}

	private returnPair<T, L> deleteMinItem(AVLNode<T, L> currNode) {
		int type;
		if (currNode.left == NIL) {
			return new returnPair(currNode.item, currNode.list, currNode.right);
		} else {
			returnPair rPair = deleteMinItem(currNode.left);
			currNode.left = rPair.node;
			currNode.height = 1 + Math.max(currNode.right.height, currNode.left.height);
			type = needBalance(currNode);
			if (type != NO_NEED)
				currNode = balanceAVL(currNode, type);
			rPair.node = currNode;
			return rPair;
		}

	}

	private class returnPair<T, L> {
		private T item;
		private T list;
		private AVLNode node;
		returnPair (T item, T list, AVLNode node) {
			this.item = item;
			this.list = list;
			this.node = node;
		}
	}

	private final int LL = 1, LR = 2, RR = 3, RL = 4, NO_NEED = 0, ILLEGAL = -1;
	private AVLNode<T, L> balanceAVL(AVLNode<T, L> currNode, int type) {
		AVLNode returnNode = NIL;
		switch (type) {
			case LL:
				returnNode = rightRotate(currNode);
				break;
			case LR:
				currNode.left = leftRotate(currNode.left);
				returnNode = rightRotate(currNode);
				break;
			case RR:
				returnNode = leftRotate(currNode);
				break;
			case RL:
				currNode.right = rightRotate(currNode.right);
				returnNode = leftRotate(currNode);
				break;
		}
		return returnNode;
	}

	private AVLNode<T, L> rightRotate(AVLNode<T, L> currNode) {
		AVLNode<T, L> LChild = currNode.left;
		AVLNode<T, L> LRChild = LChild.right;
		LChild.right = currNode;
		currNode.left = LRChild;
		currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
		LChild.height = 1 + Math.max(LChild.left.height, LChild.right.height);
		return LChild;
	}

	private AVLNode<T, L> leftRotate(AVLNode<T, L> currNode) {
		AVLNode<T, L> RChild = currNode.right;
		AVLNode<T, L> RLChild = RChild.left;
		RChild.left = currNode;
		currNode.right = RLChild;
		currNode.height = 1 + Math.max(currNode.left.height, currNode.right.height);
		RChild.height = 1 + Math.max(RChild.left.height, RChild.right.height);
		return RChild;
	}

	private int needBalance(AVLNode<T, L> currNode) {
		int type = ILLEGAL;
		if (currNode.left.height + 2 <= currNode.right.height) {
			if ((currNode.right.left.height) <= currNode.right.right.height) {
				type = RR;
			} else {
				type = RL;
			}
		} else if ((currNode.left.height) >= currNode.right.height + 2) {
			if ((currNode.left.left.height) >= currNode.left.right.height) {
				type = LL;
			} else {
				type = LR;
			}
		} else
			type = NO_NEED;
		return type;
	}

	public boolean isEmpty() {
		return root == NIL;
	}

	public void clear() {
		root = NIL;
	}
}

class AVLNode<T, L extends Comparable>{
	public T item;
	public AVLNode<T, L> left, right;
	public int height;
	public MyLinkedList<L> list;

	public AVLNode(T treeItem, L listItem) {
		this.item = treeItem;
		left = AVLTree.NIL;
		right = AVLTree.NIL;
		height = 1;
		list = new MyLinkedList<>(listItem);
	}

	public AVLNode(T treeItem, AVLNode left, AVLNode right, int height, L listItem) {
		this.item = treeItem;
		this.left = left;
		this.right = right;
		this.height = height;
		list = new MyLinkedList<>(listItem);
	}

	@Override
	public String toString() {
		return item.toString();
	}
}

class MyLinkedList<L extends Comparable> implements Iterable<L> {
	private ListNode<L> tail;
	private int size;
	public MyLinkedList() {
		size = 0;
		tail = new ListNode<>(null);
		tail.next = tail;
	};
	public MyLinkedList(L item) {
		size = 0;
		tail = new ListNode<>(null);
		tail.next = tail;
		addLast(item);
	}

	public void addLast(L newItem) {
		ListNode<L> prevNode = tail;
		ListNode<L> newNode = new ListNode<>(newItem, tail.next);
		prevNode.next = newNode;
		tail = newNode;
		size++;
	}

	public void add(L newItem) {
		boolean done = false;
		ListNode<L> currNode = tail.next;
		ListNode<L> prevNode;
		for (int i=0; i<size; i++) {
			prevNode = currNode;
			currNode = currNode.next;
			if (newItem.compareTo(currNode.item) < 0) {
				ListNode<L> newNode = new ListNode<>(newItem, prevNode.next);
				prevNode.next = newNode;
				size++;
				done = true;
				break;
			}
		}
		if (!done) {
			addLast(newItem);
		}
	}

	public void remove(L item) {
		if (!isEmpty()) {
			ListNode<L> currNode = tail.next;
			ListNode<L> prevNode;
			for (int i = 0; i < size; i++) {
				prevNode = currNode;
				currNode = currNode.next;
				if (currNode.item.equals(item)) {
					if (currNode == tail) {
						tail = prevNode;
					}
					prevNode.next = currNode.next;
					size--;
					break;
				}
			}
		}
	}

	public L get(int index) {
		if (index >= 0 && index < size) {
			return getNode(index).item;
		} else return null;
	}

	public void set(int index, L item) {
		if (index >= 0 && index < size) {
			getNode(index).item = item;
		} else {
			System.out.println("Not found");
		}
	}

	private ListNode<L> getNode(int index) {
		if (index >= -1 && index <= size) {
			ListNode<L> currNode = tail.next;
			for (int i=0; i<=index; i++) {
				currNode = currNode.next;
			}
			return currNode;
		} else return null;
	}

	public final int NOT_FOUND = -12345;
	public int indexOf(L item) {
		ListNode<L> currNode = tail.next;
		for (int i=0; i<size; i++) {
			currNode = currNode.next;
			if (currNode.item.equals(item))
				return i;
		}
		return NOT_FOUND;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size==0;
	}

	public void clear() {
		size = 0;
		tail = new ListNode(null);
		tail.next = tail;
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "(0, 0)";
		}

		StringBuilder sb = new StringBuilder();
		Iterator<L> iterator = iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString());
			sb.append(' ');
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public MyLinkedList<L> clone() {
		MyLinkedList<L> clone = new MyLinkedList<>();
		ListNode<L> currNode = tail.next;
		for (int i=0; i<size; i++) {
			currNode = currNode.next;
			clone.addLast(currNode.item);
		}
		return clone;
	}

	public final Iterator<L> iterator() {
		return new MyLinkedListIterator<L>(this);
	}

	private class MyLinkedListIterator<T extends Comparable> implements Iterator<T> {
		private MyLinkedList<T> list;
		private ListNode<T> curr;
		private ListNode<T> prev;

		public MyLinkedListIterator(MyLinkedList<T> list) {
			this.list = list;
			this.curr = list.tail.next;
			this.prev = list.tail;
		}

		@Override
		public boolean hasNext() {
			return curr != tail;
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();

			prev = curr;
			curr = curr.next;

			return curr.item;
		}

		@Override
		public void remove() {
			if (prev == null)
				throw new IllegalStateException("next() should be called first");
			if (curr == null)
				throw new NoSuchElementException();
			prev.next = prev.next.next;
			list.size--;
			curr = prev;
			prev = null;
		}
	}
}



class ListNode<E> {
	public E item;
	public ListNode<E> next;

	public ListNode(E item) {
		this.item = item;
		next = null;
	}

	public ListNode(E item, ListNode<E> next) {
		this.item = item;
		this.next = next;
	}
}

class ListItem implements Comparable<ListItem> {
	public int lineNum;
	public int index;

	public ListItem(int lineNum, int index) {
		this.lineNum = lineNum;
		this.index = index;
	}

	@Override
	public String toString() {
		String loc = "(" + lineNum + ", " + index + ")";
		return loc;
	}

	public boolean isNext(ListItem listItem) {
		return ((listItem.lineNum == this.lineNum) && (listItem.index-this.lineNum == 1));
	}

	public boolean isDistance(int distance, ListItem listItem) {
		return (isSameLine(listItem) && (getDistance(listItem) == distance));
	}

	private boolean isSameLine(ListItem listItem) {
		return (this.lineNum==listItem.lineNum);
	}

	private int getDistance(ListItem listItem) {
		if (this.lineNum == listItem.lineNum) {
			return (this.index - listItem.index);
		}
		return 0;
	}

	@Override
	public int compareTo(ListItem listItem) {
		if (this.lineNum != listItem.lineNum) {
			return this.lineNum - listItem.lineNum;
		}
		return this.index - listItem.index;
	}

	@Override
	public boolean equals (Object obj) {
		if (obj instanceof ListItem) {
			return ((this.lineNum==((ListItem) obj).lineNum) && (this.index==((ListItem) obj).index));
		}
		return false;
	}

	public ListItem clone() {
		return new ListItem(this.lineNum, this.index);
	}
}

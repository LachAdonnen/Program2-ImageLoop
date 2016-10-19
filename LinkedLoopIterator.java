import java.util.Iterator;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            LinkedLoopIterator.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
///////////////////////////////////////////////////////////////////////////////

/**
 * This class manages an iterator for the LinkedLoop data structure.
 * @author Alex McClain
 * @param <E> Data types stored in the linked loop.
 */
public class LinkedLoopIterator<E> implements Iterator<E> {
	
	// Stores the current node for the iterator
	private DblListnode<E> currentNode;
	// Stores the first node that was returned by the iterator
	private DblListnode<E> initialNode;
	
	/**
	 * Creates a new iterator that begins at the given node.
	 * @param startNode The first node to be returned by the iterator.
	 */
	protected LinkedLoopIterator(DblListnode<E> startNode) {
		currentNode = startNode;
	}

	@Override
	/**
	 * Returns whether there is another link node to be returned.
	 * Note: this method intentionally does not use .equals as we only stop
	 * once we reach the same Object reference.
	 * @return Whether there is another node to be returned
	 */
	public boolean hasNext() {
		if (currentNode == null) { return false; }
		return currentNode != initialNode;
	}

	@Override
	/**
	 * Returns the data for the current node and moves on to the next. If we
	 * haven't yet returned any data, populate the initial node to establish a
	 * quit condition.
	 */
	public E next() { 
		if (initialNode == null) { initialNode = currentNode; }
		DblListnode<E> nodeToReturn = currentNode;
		currentNode = currentNode.getNext();
		return nodeToReturn.getData();
	}
	
	@Override
	public void remove() { throw new UnsupportedOperationException(); }
}

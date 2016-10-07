import java.util.Iterator;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            ImageLoopEditor.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
// Lab Section:      LAB ###
///////////////////////////////////////////////////////////////////////////////

public class LinkedLoopIterator<E> implements Iterator<E> {
	
	private DblListnode<E> currentNode;
	private DblListnode<E> initialNode;
	
	protected LinkedLoopIterator(DblListnode<E> startNode) {
		initialNode = startNode;
		currentNode = startNode;
	}

	/**
	 * Returns whether there is another link node to be returned.
	 * Note: this method intentionally does not use .equals as we only stop
	 * once we reach the same Object reference.
	 * @return Whether there is another node to be returned
	 */
	@Override
	public boolean hasNext() {
		if (currentNode == null) { return false; }
		return currentNode.getNext() == initialNode;
	}

	@Override
	public E next() { return currentNode.getNext().getData(); }
	
	@Override
	public void remove() { throw new UnsupportedOperationException(); }
}

import java.util.Iterator;

///////////////////////////////////////////////////////////////////////////////
// Title:            Prog2-ImageLoop
// Files:            LinkedLoop.java
// Semester:         Fall 2016
//
// Author:           Alex McClain, gamcclain@wisc.edu
// CS Login:         gamcclain@wisc.edu
// Lecturer's Name:  Charles Fischer
// Lab Section:      LAB ###
///////////////////////////////////////////////////////////////////////////////

public class LinkedLoop<E> implements LoopADT<E> {
	
	private DblListnode<E> currentNode;
	private int numNodes;
	
	public LinkedLoop() {
		currentNode = null;
		numNodes = 0;
	}
	
	@Override
	public void add(E item) {
		if (item != null) {
			if (currentNode == null) { 
				currentNode = new DblListnode<E>(item);
				currentNode.setNext(currentNode);
				currentNode.setPrev(currentNode);
			}
			else {
				DblListnode<E> prevNode = currentNode.getPrev();
				DblListnode<E> newNode = new DblListnode<E>(prevNode, item, 
						currentNode);
				prevNode.setNext(newNode);
				currentNode.setPrev(newNode);
				currentNode = newNode;
			}
			numNodes++;
		}
	}
	
	@Override
	public E getCurrent() throws EmptyLoopException {
		if (numNodes == 0) { throw new EmptyLoopException(); }
		return currentNode.getData();
	}
	
	@Override
	public boolean isEmpty() { return numNodes == 0; }
	
	@Override
	public Iterator<E> iterator() {
		return new LinkedLoopIterator<>(currentNode);
	}
	
	@Override
	public void next() { 
		if (currentNode != null) { currentNode = currentNode.getNext(); }
	}
	
	@Override
	public void previous() {
		if (currentNode != null) { currentNode = currentNode.getPrev(); }
	}
	
	@Override
	public E removeCurrent() throws EmptyLoopException {
		if (numNodes == 0) { throw new EmptyLoopException(); }
		else if (numNodes == 1) {
			currentNode = null;
			numNodes--;
			return null;
		}
		else {
			DblListnode<E> removedNode = currentNode;
			DblListnode<E> prevNode = currentNode.getPrev();
			DblListnode<E> nextNode = currentNode.getNext();
			
			prevNode.setNext(nextNode);
			nextNode.setPrev(prevNode);
			currentNode = nextNode;
			numNodes--;
			return removedNode.getData();
		}
	}
	
	@Override
	public int size() { return numNodes; }
	
	public void swap(int pos1, int pos2) {
		// Verify that we need to take some action
		if (pos1 == pos2) { return; }
		if (numNodes < 2) { return; }
		if (pos1 < 0 || pos2 < 0) { return; }
		if (pos1 >= numNodes || pos2 >= numNodes) { return; }
		
		// Determine the order of the two positions
		int min = pos1, max = pos2;
		if (pos2 < pos1) {
			min = pos2;
			max = pos1;
		}
		
		// Traverse the list to find the two target nodes
		DblListnode<E> minNode = null;
		DblListnode<E> maxNode = null;
		DblListnode<E> iteratorNode = currentNode;
		for (int i = 0; i <= max; i++) {
			if (i == min) { minNode = iteratorNode; }
			if (i == max) { maxNode = iteratorNode; }
			iteratorNode = iteratorNode.getNext();
		}
		
		// The minNode is guaranteed to have a next since the list has at least 3 items
		DblListnode<E> minNextNode = minNode.getNext();
		// The maxNode is guaranteed to have a previous node since the list has at least 3 items
		DblListnode<E> maxPrevNode = maxNode.getPrev();

		DblListnode<E> minPrevNode;
		if (min > 0) { // Not switching the first node, so just grab the existing previous node
			minPrevNode = minNode.getPrev();
			maxNode.setPrev(minPrevNode);
			minPrevNode.setNext(maxNode);
		}
		else { // Switching the first element, so place the maxNode after the header node
			maxNode.setPrev(null);
		}

		DblListnode<E> maxNextNode;
		if (max < numNodes - 1) { // Not switching the last node, so just grab the existing next node
			maxNextNode = maxNode.getNext();
			minNode.setNext(maxNextNode);
			maxNextNode.setPrev(minNode);
		}
		else { minNode.setNext(null); } // Switching the last node, so there will be no next

		if (minNextNode == maxNode) { // Switching adjacent nodes, so reverse the previous/next pointers
			maxNode.setNext(minNode);
			minNode.setPrev(maxNode);
		}
		else { // There are nodes in between, so use the switch previous/next pointers
			maxNode.setNext(minNextNode);
			minNextNode.setPrev(maxNode);
			minNode.setPrev(maxPrevNode);
			maxPrevNode.setNext(minNode);
		}
	}
	
}

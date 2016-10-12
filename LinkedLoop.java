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
	
}

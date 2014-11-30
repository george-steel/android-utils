/*
* Copyright © 2014 George T. Steel
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.oshkimaadziig.george.androidutils;

import java.util.NoSuchElementException;

/**
 * Implementation of a min heap (of fixed capacity) holding items tagged with numeric priorities.
 * Useful for keeping a running top n list.
 * 
 * @author George T. Steel
 *
 * @param <T> The type of objects to hold.
 */
class PriorityHeap<T>{
	private final double[] _priHeap;
	private final T[] _dataHeap;
	private final int _capacity;
	private int _currentSize;
	
	/**
	 * Create a new empty heap holding it's data in the provided array.
	 * The heap's capacity will be equal to the array's size.
	 * 
	 * @param data_array The array used to hold the heaped items.
	 */
	PriorityHeap(T[] data_array){
		this._capacity = data_array.length;
		_priHeap = new double[_capacity];
		_dataHeap = data_array;
		_currentSize=0;
	}
	
	private void swap(int a, int b){
		double p = _priHeap[a];
		T d = _dataHeap[a];
		_priHeap[a] = _priHeap[b];
		_dataHeap[a] = _dataHeap[b];
		_priHeap[b] = p;
		_dataHeap[b] = d;
	}
	
	private boolean isLeaf(int pos) {
		return (pos >= _currentSize/2) && (pos < _currentSize);}

	private int leftchild(int pos) {
		return 2*pos + 1;}

	private int parent(int pos) {
		return (pos-1)/2;}
	
	private void siftdown(int pos) {
		while (!isLeaf(pos)) {
			int j = leftchild(pos);
			if (((j+1) < _currentSize) && (_priHeap[j] > _priHeap[j+1]))
				j++; // j is now index of child with lesser value
			if (_priHeap[pos] <= _priHeap[j])
				return;
			swap(pos, j);
			pos = j; // Move down
		}
	}
	
	/**
	 * Returns the current size of the heap.
	 * @return the number of elements currently in the heap.
	 */
	public int size(){
		return _currentSize;
	}
	
	/**
	 * Insert a new item into the heap, marked with a given priority.
	 * Throws if the heap is full.
	 * 
	 * @param priority The priority to mark this item with.
	 * @param item The item to insert.
	 */
	public void insert(double priority, T item) {
		if (_currentSize == _capacity) throw new IndexOutOfBoundsException("Heap is full");
		int curr = _currentSize++;
		_priHeap[curr]=priority;
		_dataHeap[curr]=item;
		// Now sift up until curr's parent's key > curr's key
		while ((curr != 0) && (_priHeap[curr] < _priHeap[parent(curr)])) {
			swap(curr, parent(curr));
			curr = parent(curr);
		}
	}
	
	/**
	 * Pop the item with minimum priority.
	 * 
	 * @return the item popped
	 */
	public T popMin() { // Remove minimum value
		if (_currentSize == 0) throw new NoSuchElementException("Removing from empty heap");
		
		swap(0, --_currentSize); // Swap minimum with last value
		if (_currentSize != 0) // Not on last element
			siftdown(0); // Put new heap root val in correct place
		return _dataHeap[_currentSize];
	}
	
	/**
	 * Get the item with minimum priority
	 */
	public T getMinItem(){ return _dataHeap[0]; }
	
	/**
	 * Get the minimum priority
	 * @return
	 */
	public double getMinPriority(){ return _priHeap[0]; }
	
	/**
	 * Inserts a new item replacing the item with the lowest priority tag.
	 * Does nothing if the object to be inserted has the lowest priority.
	 * 
	 * @param priority The priority to mark this item with.
	 * @param item The item to insert.
	 */
	public void replaceMin(double priority, T item){
		if (_currentSize == 0) throw new NoSuchElementException("Removing from empty heap");
		
		assert _currentSize > 0 : "Removing from empty heap";
		if (priority < _priHeap[0]) return;
		_priHeap[0] = priority;
		_dataHeap[0] = item;
		siftdown(0);
	}
	
	/**
	 * Insert a new item into the heap, marked with a given priority.
	 * Discards the item with lowest priority if the heap is full (good for a running list)
	 * 
	 * @param priority The priority to mark this item with.
	 * @param item The item to insert.
	 */
	public void insertOrReplace(double priority, T item){
		if (_currentSize == _capacity) replaceMin(priority, item);
		else insert(priority, item);
	}
}
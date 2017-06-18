package com.yunxi.phone.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * created by bond 2017/1/4
 * @param <E>
 */

public abstract class ArrayListAdapter<E> extends BaseAdapter implements
		List<E> {

	public final ArrayList<E> mArrayList = new ArrayList<E>();

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public E getItem(int position) {
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void add(int location, E object) {
		mArrayList.add(location, object);
	}

	@Override
	public boolean add(E object) {
		return mArrayList.add(object);
	}

	@Override
	public boolean addAll(int location, Collection<? extends E> collection) {
		return mArrayList.addAll(location, collection);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return mArrayList.addAll(collection);
	}

	@Override
	public void clear() {
		mArrayList.clear();
	}
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public boolean contains(Object object) {
		return mArrayList.contains(object);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return mArrayList.containsAll(collection);
	}

	@Override
	public E get(int location) {
		return mArrayList.get(location);
	}

	@Override
	public int indexOf(Object object) {
		return mArrayList.indexOf(object);
	}

	@Override
	public Iterator<E> iterator() {
		return mArrayList.iterator();
	}

	@Override
	public int lastIndexOf(Object object) {
		return mArrayList.lastIndexOf(object);
	}

	@Override
	public ListIterator<E> listIterator() {
		return mArrayList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int location) {
		return mArrayList.listIterator(location);
	}

	@Override
	public E remove(int location) {
		return mArrayList.remove(location);
	}

	@Override
	public boolean remove(Object object) {
		return mArrayList.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return mArrayList.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return mArrayList.retainAll(collection);
	}

	@Override
	public E set(int location, E object) {
		return mArrayList.set(location, object);
	}

	@Override
	public int size() {
		return mArrayList.size();
	}

	@Override
	public List<E> subList(int start, int end) {
		return mArrayList.subList(start, end);
	}

	@Override
	public Object[] toArray() {
		return mArrayList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return mArrayList.toArray(array);
	}

	public ArrayList<E> getArrayList() {
		return mArrayList;
	}

}

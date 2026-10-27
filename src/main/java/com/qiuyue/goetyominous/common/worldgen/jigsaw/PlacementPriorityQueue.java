package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public final class PlacementPriorityQueue<T> extends ArrayDeque<T> {

    private final TreeMap<Integer, ArrayDeque<T>> byPriority = new TreeMap<>();
    private final IntSupplier priorityOfNext;
    private int size;

    public PlacementPriorityQueue(IntSupplier priorityOfNext) {
        this.priorityOfNext = priorityOfNext;
    }

    public void add(T element, int priority) {
        this.byPriority.computeIfAbsent(priority, key -> new ArrayDeque<>()).addLast(element);
        this.size++;
    }

    @Override
    public void addLast(T element) {
        this.add(element, this.priorityOfNext.getAsInt());
    }

    @Override
    public boolean add(T element) {
        this.addLast(element);
        return true;
    }

    @Override
    public T removeFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        Map.Entry<Integer, ArrayDeque<T>> entry = this.byPriority.lastEntry();
        T element = entry.getValue().removeFirst();
        if (entry.getValue().isEmpty()) {
            this.byPriority.remove(entry.getKey());
        }
        this.size--;
        return element;
    }

    @Override
    public T pollFirst() {
        return this.size == 0 ? null : this.removeFirst();
    }

    @Override
    public T poll() {
        return this.pollFirst();
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        return "PlacementPriorityQueue" + this.byPriority;
    }

    private static UnsupportedOperationException unsupported(String operation) {
        return new UnsupportedOperationException("PlacementPriorityQueue does not support " + operation);
    }

    @Override
    public void addFirst(T element) {
        throw unsupported("addFirst");
    }

    @Override
    public boolean offerFirst(T element) {
        throw unsupported("offerFirst");
    }

    @Override
    public boolean offerLast(T element) {
        this.addLast(element);
        return true;
    }

    @Override
    public boolean offer(T element) {
        this.addLast(element);
        return true;
    }

    @Override
    public T removeLast() {
        throw unsupported("removeLast");
    }

    @Override
    public T pollLast() {
        throw unsupported("pollLast");
    }

    @Override
    public T getFirst() {
        throw unsupported("getFirst");
    }

    @Override
    public T getLast() {
        throw unsupported("getLast");
    }

    @Override
    public T peekFirst() {
        throw unsupported("peekFirst");
    }

    @Override
    public T peekLast() {
        throw unsupported("peekLast");
    }

    @Override
    public T peek() {
        throw unsupported("peek");
    }

    @Override
    public T element() {
        throw unsupported("element");
    }

    @Override
    public T remove() {
        return this.removeFirst();
    }

    @Override
    public void push(T element) {
        throw unsupported("push");
    }

    @Override
    public T pop() {
        throw unsupported("pop");
    }

    @Override
    public boolean remove(Object element) {
        throw unsupported("remove(Object)");
    }

    @Override
    public boolean removeFirstOccurrence(Object element) {
        throw unsupported("removeFirstOccurrence");
    }

    @Override
    public boolean removeLastOccurrence(Object element) {
        throw unsupported("removeLastOccurrence");
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) {
        throw unsupported("addAll");
    }

    @Override
    public void clear() {
        this.byPriority.clear();
        this.size = 0;
    }

    @Override
    public boolean contains(Object element) {
        throw unsupported("contains");
    }

    @Override
    public Iterator<T> iterator() {
        throw unsupported("iterator");
    }

    @Override
    public Iterator<T> descendingIterator() {
        throw unsupported("descendingIterator");
    }

    @Override
    public Object[] toArray() {
        throw unsupported("toArray");
    }

    @Override
    public <A> A[] toArray(A[] array) {
        throw unsupported("toArray");
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        throw unsupported("forEach");
    }

    @Override
    public Spliterator<T> spliterator() {
        throw unsupported("spliterator/stream");
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw unsupported("removeIf");
    }

    @Override
    public boolean removeAll(Collection<?> elements) {
        throw unsupported("removeAll");
    }

    @Override
    public boolean retainAll(Collection<?> elements) {
        throw unsupported("retainAll");
    }

    @Override
    public ArrayDeque<T> clone() {
        throw unsupported("clone");
    }
}

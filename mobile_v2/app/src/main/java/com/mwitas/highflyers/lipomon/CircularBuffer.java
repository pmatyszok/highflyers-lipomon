package com.mwitas.highflyers.lipomon;

/**
 * TODO - description
 *
 * @author Michał Witas
 * @version 1.0
 */
public class CircularBuffer<T> {
    private Object[] data;
    private int tail;

    public CircularBuffer(int capacity) {
        data = new Object[capacity];
        tail = 0;
    }

    public void push(T value) {
        data[tail++] = value;

        if (tail == data.length) {
            tail = 0;
        }
    }

    public T get(int i) {
        @SuppressWarnings("unchecked")
        final T e = (T) data[i];
        return e;
    }
}

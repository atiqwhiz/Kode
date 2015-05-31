package com.vaani.concurrency.counter;

public class UnsafeCounter {

    private long counter;

    public void reset() {
        counter = 0;
    }

    public void increment() {
        counter++;
    }

    public long getCounter() {
        return counter;
    }

}

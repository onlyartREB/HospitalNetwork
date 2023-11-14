package rampup;

import java.util.concurrent.locks.ReentrantLock;

public class SynchronizationLock {
    public static final Object lock = new ReentrantLock();
}
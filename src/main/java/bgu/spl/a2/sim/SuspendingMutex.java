package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * <p>
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex {

    protected ConcurrentLinkedQueue<Promise> promiseQueue;
    protected AtomicBoolean isFreeComputer;
    protected Computer myComputer;

    /**
     * Constructor
     *
     * @param computer
     */
    public SuspendingMutex(Computer computer) {
        promiseQueue = new ConcurrentLinkedQueue<>();
        myComputer = computer;
        isFreeComputer = new AtomicBoolean(true);
    }


    /**
     * Computer acquisition procedure
     * Note that this procedure is non-blocking and should return immediatly
     *
     * @return a promise for the requested computer
     */
    public Promise<Computer> down() {
        Promise<Computer> ret = new Promise<>();
        if (isFreeComputer.compareAndSet(true, false)) {
            ret.resolve(myComputer);
        } else {
            promiseQueue.add(ret);
        }
        return ret;
    }

    /**
     * Computer return procedure
     * releases a computer which becomes available in the warehouse upon completion
     */
    public void up() {
        if (!promiseQueue.isEmpty()) {
            promiseQueue.poll().resolve(myComputer);
        } else {
            isFreeComputer.set(true);
        }
    }
}
package bgu.spl.a2;


import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class BUT you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

    //fields
    protected Map<String, ConcurrentLinkedQueue<Action<?>>> actorsMapQueue;
    protected Map<String, PrivateState> actorsMapPrivateState;
    protected Map<String, AtomicBoolean> actorsMapIsOccupied;
    protected Thread[] threadArray;
    protected VersionMonitor monitor;
    private CountDownLatch latch;


    /**
     * creates a {@link ActorThreadPool} which has nthreads. Note, threads
     * should not get started until calling to the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this thread
     *                 pool
     */
    public ActorThreadPool(int nthreads) {
        monitor = new VersionMonitor();
        actorsMapQueue = new ConcurrentHashMap<>();
        actorsMapPrivateState = new ConcurrentHashMap<>();
        actorsMapIsOccupied = new ConcurrentHashMap<>();
        threadArray = new Thread[nthreads];
        latch = new CountDownLatch(nthreads);
        for (int i = 0; i < nthreads; i++) {
            threadArray[i] = new Thread() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        int currentVersion = monitor.getVersion();
                        for (Map.Entry<String, ConcurrentLinkedQueue<Action<?>>> entry : actorsMapQueue.entrySet()) {
                            String actorId = entry.getKey();
                            Queue<Action<?>> actorsQueue = entry.getValue();
                            if (!Thread.currentThread().isInterrupted() && !actorsMapIsOccupied.get(actorId).get() && !actorsQueue.isEmpty()) {
                                if (actorsMapIsOccupied.get(actorId).compareAndSet(false, true)) {
                                    Action<?> action = actorsQueue.poll();
                                    if (action != null) {
                                        action.handle(ActorThreadPool.this, actorId, actorsMapPrivateState.get(actorId));
                                    }
                                    actorsMapIsOccupied.get(actorId).compareAndSet(true, false);
                                    monitor.inc();
                                }
                            }
                        }
                        if (!Thread.currentThread().isInterrupted()) {
                            try {
                                monitor.await(currentVersion);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
                    }
                    latch.countDown();
                }
            };

        }
    }

    /**
     * getter for actors
     *
     * @return actors
     */
    public Map<String, PrivateState> getActors() {
        return actorsMapPrivateState;
    }

    /**
     * getter for actor's private state
     *
     * @param actorId actor's id
     * @return actor's private state
     */
    public PrivateState getPrivateStates(String actorId) {
        return actorsMapPrivateState.get(actorId);
    }

    /**
     * submits an action into an actor to be executed by a thread belongs to
     * this thread pool
     *
     * @param action     the action to execute
     * @param actorId    corresponding actor's id
     * @param actorState actor's private state (actor's information)
     */
    public synchronized void submit(Action<?> action, String actorId, PrivateState actorState) {
        if (actorsMapQueue.containsKey(actorId)) {
            actorsMapQueue.get(actorId).add(action);
            monitor.inc();
        } else {
            actorsMapIsOccupied.put(actorId, new AtomicBoolean(false));
            actorsMapQueue.put(actorId, new ConcurrentLinkedQueue<>());
            actorsMapPrivateState.put(actorId, actorState);
            actorsMapQueue.get(actorId).add(action);
            monitor.inc();
        }
    }

    /**
     * closes the thread pool - this method interrupts all the threads and waits
     * for them to stop - it is returns only when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is interrupted
     */
    public void shutdown() throws InterruptedException {
        for (Thread t : threadArray) {
            t.interrupt();
        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
        }
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (Thread t : threadArray) {
            t.start();
        }

    }

}
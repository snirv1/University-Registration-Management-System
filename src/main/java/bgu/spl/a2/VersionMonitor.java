package bgu.spl.a2;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 * <p>
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class BUT you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {

    //fields
    protected AtomicInteger fVersion;


    /**
     * a constructor
     */
    public VersionMonitor() {
        fVersion = new AtomicInteger(0);
    }


    public int getVersion() {
        return fVersion.get();
    }

    /**
     * this method increment the version of the monitor
     */
    public void inc() {
        int currVersion;
        do {
            currVersion = fVersion.get();

        } while (!fVersion.compareAndSet(currVersion, currVersion + 1));
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * await() with this version number in order to wait until this version number changes.
     * if the version is not the current version the method return immediately.
     *
     * @param version the version to wait for it to change.
     * @throws InterruptedException when a thread is interrupted while waiting.
     */
    public void await(int version) throws InterruptedException {
        if (version != fVersion.get()) {
            return;
        }
        while (version == fVersion.get()) {
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException ex) {
                throw new InterruptedException();
            }
        }


    }
}
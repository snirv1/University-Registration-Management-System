package bgu.spl.a2.sim;


import bgu.spl.a2.Promise;


import java.util.HashMap;
import java.util.List;


/**
 * represents a warehouse that holds a finite amount of computers
 * and their suspended mutexes.
 * releasing and acquiring should be blocking free.
 */
public class Warehouse {
    protected List<Computer> computersCollection;
    protected HashMap<String, SuspendingMutex> computersMap;

    /**
     * A constructor to the Warehouse class
     *
     * @param givenComputers List of Computers{@link bgu.spl.a2.sim.Computer}  given to the Warehouse to manage.
     */

    public Warehouse(List<Computer> givenComputers) {
        computersMap = new HashMap<>();
        this.computersCollection = givenComputers;
        for (Computer computer : givenComputers) {
            computersMap.put(computer.computerType, new SuspendingMutex(computer));
        }
    }

    /**
     * This method allocate a computer to use when the cumputer will be free.
     *
     * @param computerType the Type of the required.
     * @return a Promise{@link bgu.spl.a2.Promise} to the requested computer.
     */
    public Promise<Computer> acquire(String computerType) {
        return computersMap.get(computerType).down();
    }

    /**
     * This method deallocate a computer after its use.
     *
     * @param computerType the computer to deallocate
     */
    public void release(String computerType) {
        computersMap.get(computerType).up();
    }
}
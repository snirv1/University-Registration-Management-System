/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import bgu.spl.a2.sim.actions.*;
import com.google.gson.Gson;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.actions.OpenCourse;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {


    public static ActorThreadPool actorThreadPool;
    private static University university;
    private static Warehouse warehouse;
    private static List phase1;
    private static List phase2;
    private static List phase3;
    private static List<Computer> computers;


    /**
     * Begin the simulation Should not be called before attachActorThreadPool()
     */
    public static void start() {
        phase1 = university.getPhase1();
        phase2 = university.getPhase2();
        phase3 = university.getPhase3();
        JsonComputer[] computersNames = university.getComputers();
        CountDownLatch count1 = new CountDownLatch(phase1.size());
        CountDownLatch count2 = new CountDownLatch(phase2.size());
        CountDownLatch count3 = new CountDownLatch(phase3.size());
        computers = makeComputers(computersNames);
        warehouse = new Warehouse(computers);
        actorThreadPool.start();

        try {
            makeActions(phase1, count1);
            count1.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            makeActions(phase2, count2);
            count2.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        makeActions(phase3, count3);
        try {
            count3.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    /**
     * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
     *
     * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
     */
    public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
        actorThreadPool = myActorThreadPool;
    }

    /**
     * shut down the simulation
     * returns list of private states
     */
    public static HashMap<String, PrivateState> end() {
        try {
            actorThreadPool.shutdown();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        HashMap<String, PrivateState> ret = new HashMap<>();
        ret.putAll(actorThreadPool.getActors());
        return ret;

    }

    /**
     * This method takes a list of JsonActions{@link bgu.spl.a2.sim.JsonAction} with the required input fields taken from
     * the json input file and initialize an actual action according to the input , submit it and subscribe a callback
     * - to the actions promise to count down the latch when finished.
     *
     * @param actions a list of JsonAction with the required input fields to initialize of the actual actions that
     *                are submitted to the actorThreadPool{@link bgu.spl.a2.ActorThreadPool}
     * @param latch   a CountDownLatch with size of the current phase that count down until the phase is over.
     */

    public static void makeActions(List<JsonAction> actions, CountDownLatch latch) {
        for (JsonAction jsonAction : actions) {
            String actionName = jsonAction.getActionName();
            callback callback = () -> latch.countDown();
            if (actionName.equals("Open Course")) {
                OpenCourse action = new OpenCourse(jsonAction.getDepartment(), jsonAction.getCourse(), jsonAction.getSpaces(), jsonAction.getPrerequisites());
                actorThreadPool.submit(action, jsonAction.getDepartment(), new DepartmentPrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Add Student")) {
                AddStudent action = new AddStudent(jsonAction.getDepartment(), jsonAction.getStudent());
                actorThreadPool.submit(action, jsonAction.getDepartment(), new DepartmentPrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Participate In Course")) {
                ParticipateInCourse action = new ParticipateInCourse(jsonAction.getStudent(), jsonAction.getGrade());
                actorThreadPool.submit(action, jsonAction.getCourse(), new CoursePrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Add Spaces")) {
                AddSpaces action = new AddSpaces(jsonAction.getNumber());
                actorThreadPool.submit(action, jsonAction.getCourse(), new CoursePrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Register With Preferences")) {
                RegisterWithPreferences action = new RegisterWithPreferences(jsonAction.getStudent(), jsonAction.getPreferences(), jsonAction.getGrades());
                actorThreadPool.submit(action, jsonAction.getPreferences().get(0), new CoursePrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Unregister")) {
                Unregister action = new Unregister(jsonAction.getStudent(), jsonAction.getCourse());
                actorThreadPool.submit(action, jsonAction.getCourse(), new CoursePrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Close Course")) {
                CloseACourse action = new CloseACourse(jsonAction.getCourse());
                actorThreadPool.submit(action, jsonAction.getDepartment(), new DepartmentPrivateState());
                action.getResult().subscribe(callback);
            } else if (actionName.equals("Administrative Check")) {
                AdministrativeCheck action = new AdministrativeCheck(jsonAction.getStudents(), jsonAction.getConditions(), jsonAction.getComputer(), warehouse);
                actorThreadPool.submit(action, jsonAction.getDepartment(), new DepartmentPrivateState());
                action.getResult().subscribe(callback);
            }

        }
    }

    /**
     * this method gets a list of JsonComputers{@link bgu.spl.a2.sim.JsonComputer} received form the json file
     * and making a list of Computer {@link bgu.spl.a2.sim.Computer} with the same type and signatures.
     *
     * @param computers a list of JsonComputers as input
     * @return computerList: a list of Computers with fields initialized according to the json input
     */

    public static List<Computer> makeComputers(JsonComputer[] computers) {
        List<Computer> computerList = new LinkedList<>();
        for (JsonComputer jsonComputer : computers) {
            Computer computer = new Computer(jsonComputer.getType(), jsonComputer.getSigSuccess(), jsonComputer.getSigFail());
            computerList.add(computer);
        }
        return computerList;
    }


    public static void main(String[] args) {
            Gson gson = new Gson();
            try {
                FileReader fileReader = new FileReader(args[0]);
                university = gson.fromJson(fileReader, University.class);
            } catch (FileNotFoundException ex) {
            }
            int numOfThreads = university.getThreads();
            attachActorThreadPool(new ActorThreadPool(numOfThreads));
            start();
            HashMap<String, PrivateState> simulationResult;
            simulationResult = Simulator.end();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream("result.ser");
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
                    outputStream.writeObject(simulationResult);
                    outputStream.close();
                } catch (IOException ex) {
                }
            } catch (FileNotFoundException ex) {
            }
        }
}

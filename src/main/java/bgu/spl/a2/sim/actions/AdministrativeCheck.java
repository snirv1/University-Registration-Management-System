package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdministrativeCheck extends Action {

    protected List<String> listOfStudents;
    protected List<String> listOfConditions;
    protected String computerType;
    protected Warehouse warehouse;


    /**
     * @param listOfStudents   a list of students that should be check
     * @param listOfConditions list of courses that each student check himself by them
     * @param computerType     the type of the computer should ask from the warehouse
     * @param warehouse        the computers warehouse
     */
    public AdministrativeCheck(List<String> listOfStudents, List<String> listOfConditions, String computerType, Warehouse warehouse) {
        super();
        super.actionName = "Administrative Check";
        this.listOfStudents = listOfStudents;
        this.listOfConditions = listOfConditions;
        this.computerType = computerType;
        this.warehouse = warehouse;
    }


    /**
     * This action extend {@link bgu.spl.a2.Action} and it checks for each student
     * in listOfStudents (send him subAction {@link bgu.spl.a2.sim.actions.HelpAdministrativeCheckStudent}
     * if he meets some administrative obligations.
     * this made by a computer ask from the warehouse
     * generates a signature and save it in the private state of the students.
     * Actor: Must be initially submitted to the department's actor.
     */
    @Override
    protected void start() {
        DepartmentPrivateState departmentPS = ((DepartmentPrivateState) pool.getPrivateStates(actorId));
        Promise<Computer> computerPromise = warehouse.acquire(computerType);
        List<Action> subActionsArray = new ArrayList<>();
        HashMap<String, HelpAdministrativeCheckStudent> actionHashMap = new HashMap<>();
        for (String student : listOfStudents) {
            StudentPrivateState studentPS = (StudentPrivateState) pool.getPrivateStates(student);
            HelpAdministrativeCheckStudent administrativeCheckStudent = new HelpAdministrativeCheckStudent(listOfConditions, computerPromise);
            actionHashMap.put(student, administrativeCheckStudent);
            subActionsArray.add(administrativeCheckStudent);
        }
        then(subActionsArray, () -> {
            complete("Administrative Check Done: " + "listOfConditions: " + listOfConditions + "listOfStudents: " + listOfStudents);
            departmentPS.addRecord(actionName);
            warehouse.release(computerType);
        });

        for (String student : listOfStudents) {
            StudentPrivateState studentPS = (StudentPrivateState) pool.getPrivateStates(student);
            HelpAdministrativeCheckStudent administrativeCheckStudent = actionHashMap.get(student);
            computerPromise.subscribe(() -> {
                sendMessage(administrativeCheckStudent, student, studentPS);
            });
        }


    }
}
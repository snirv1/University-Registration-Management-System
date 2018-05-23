package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;


public class AddStudent extends Action {
    protected String studentName;

    /**
     * @param department  represent the department that added the student
     * @param studentName the student name that added
     */
    public AddStudent(String department, String studentName) {
        super();
        super.actionName = "Add Student";
        this.studentName = studentName;
        actorId = department;
    }


    /**
     * This action extend {@link bgu.spl.a2.Action} and it adds a new student
     * to a specified department.
     * initially submitted to the Department's actor.
     * send a sub action to the student in order to open actors fields (as describe
     * at ActorThreadPool {@link bgu.spl.a2.ActorThreadPool}
     */

    @Override
    protected void start() {
        DepartmentPrivateState departmentPS = ((DepartmentPrivateState) pool.getPrivateStates(actorId));
        Action<String> addNewStudent = new HelpEmptyAction();
        List<Action> actions = new ArrayList<>();
        actions.add(addNewStudent);
        then(actions, () -> {
            departmentPS.getStudentList().add(studentName);
            complete("Add Student Done " + "studentName :" + studentName);
            departmentPS.addRecord(actionName);
        });
        sendMessage(addNewStudent, studentName, new StudentPrivateState());
    }
}

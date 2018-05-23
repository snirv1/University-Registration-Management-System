package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;


public class Unregister extends Action {
    protected String studentId;

    /**
     * @param studentId the student name that will delete
     * @param courseId  represent the actorId
     */

    public Unregister(String studentId, String courseId) {
        super();
        super.actionName = "Unregister";
        actorId = courseId;
        this.studentId = studentId;

    }

    /**
     * This action extend {@link bgu.spl.a2.Action} and this action  unregister
     * the student. send a sub action to the student to unregister himself. then update the
     * course registered student list
     * initially submitted to the course's actor.
     */
    @Override
    protected void start() {
        StudentPrivateState studentPS = ((StudentPrivateState) pool.getPrivateStates(studentId));
        CoursePrivateState coursePS = (CoursePrivateState) pool.getPrivateStates(actorId);
        HelpUnregisterStudentAction deleteGrade = new HelpUnregisterStudentAction(actorId);
        List<Action> subActionsArray = new ArrayList<>();
        subActionsArray.add(deleteGrade);
        then(subActionsArray, () -> {
            if (subActionsArray.get(0).getResult().get().equals("true")) {
                coursePS.getRegStudents().remove(studentId);
                coursePS.setRegistered(coursePS.getRegistered() - 1);
                if (coursePS.getAvailableSpots() != (-1)) {
                    coursePS.setAvailableSpots(coursePS.getAvailableSpots() + 1);
                }
                complete("Unregister " + "unregister succeed");
            } else {
                Unregister thisAction = new Unregister(studentId, actorId);
                sendMessage(thisAction, actorId, coursePS);
                complete("Unregister " + "student is not registered");

            }
        });
        sendMessage(deleteGrade, studentId, studentPS);
        coursePS.addRecord(actionName);


    }
}

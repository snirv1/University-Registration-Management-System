package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;


public class HelpUnregisterStudentAction extends Action {

    protected String courseId;

    /**
     * @param courseId the course name should be delete from student grade map
     */
    public HelpUnregisterStudentAction(String courseId) {
        super();
        super.actionName = "Unregister Student Action";
        this.courseId = courseId;


    }

    /**
     * this action extend {@link bgu.spl.a2.Action} and it is a sub action that sent by  unrigister
     * {@link bgu.spl.a2.sim.actions.HelpUnregisterStudentAction}
     * submit to student in order to delete the grade from the grade map
     */
    @Override
    protected void start() {
        StudentPrivateState studentPS = ((StudentPrivateState) pool.getPrivateStates(actorId));
        if (studentPS.getGrades().containsKey(courseId)) {
            studentPS.getGrades().remove(courseId);
            complete("true");
        } else {
            complete("false");
        }

    }
}


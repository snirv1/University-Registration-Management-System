package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;


public class HelpRegisterStudentAction extends Action {
    protected String courseId;
    protected Integer grade;

    /**
     * @param courseId the should be added to the gardes map
     * @param grade    the grade of the course
     */
    public HelpRegisterStudentAction(String courseId, Integer grade) {
        super();
        super.actionName = "Register Student Action";
        this.courseId = courseId;
        this.grade = grade;
    }

    /**
     * this sub action extend {@link bgu.spl.a2.Action} and it is submitted to a
     * student by {@link bgu.spl.a2.sim.actions.ParticipateInCourse
     * that will add the {@param courseId to the student gardes map.
     */
    @Override
    protected void start() {
        StudentPrivateState studentPS = ((StudentPrivateState) pool.getPrivateStates(actorId));
        studentPS.getGrades().put(courseId, grade);
        complete("true");
    }
}

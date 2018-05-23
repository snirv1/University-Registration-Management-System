package bgu.spl.a2.sim.actions;


import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;


import java.util.ArrayList;
import java.util.List;


public class HelpCloseCourseByCourse extends Action {


    public HelpCloseCourseByCourse() {
        super();
        super.actionName = "HelpCloseCourseByCourse";
    }

    /**
     * this sub action extend {@link bgu.spl.a2.Action and its  sent by
     * {@link bgu.spl.a2.sim.actions.CloseACourse to the specific course.
     * at first the course is considered as closed
     * (as its available spots sets to -1)
     * then the course send massage for each student to unregister
     * (by unregister action)
     * that is registered. did not get params
     */
    @Override
    protected void start() {
        CoursePrivateState coursePS = (CoursePrivateState) pool.getPrivateStates(actorId);
        coursePS.setAvailableSpots(-1);
        List<Action> subActionsArray = new ArrayList<>();
        if (coursePS.getRegStudents().size() != 0) {
            for (String student : coursePS.getRegStudents()) {
                Unregister deleteGrade = new Unregister(student, actorId);
                sendMessage(deleteGrade, actorId, coursePS);
                subActionsArray.add(deleteGrade);
            }
            then(subActionsArray, () -> {
                coursePS.setRegistered(0);
                coursePS.getRegStudents().clear();
                complete("course " + actorId + " closed by himself");
            });
        } else {
            coursePS.setRegistered(0);
            coursePS.getRegStudents().clear();
            complete("course " + actorId + " closed by himself");
        }


    }
}
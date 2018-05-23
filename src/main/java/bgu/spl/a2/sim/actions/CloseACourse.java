package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;


import java.util.ArrayList;
import java.util.List;


public class CloseACourse extends Action {
    protected String courseId;

    /**
     * @param courseId the that closed
     */
    public CloseACourse(String courseId) {
        super();
        super.actionName = "Close A Course";
        this.courseId = courseId;
    }

    /**
     * This action extend {@link bgu.spl.a2.Action} and its send a sub action to
     * the course to close itself. when the course finish close itself- the department
     * delete it from its lists
     * <p>
     * initially submitted to the department
     */
    @Override
    protected void start() {
        CoursePrivateState coursePS = (CoursePrivateState) pool.getPrivateStates(courseId);
        DepartmentPrivateState departmentPS = (DepartmentPrivateState) pool.getPrivateStates(actorId);
        HelpCloseCourseByCourse deleteCourse = new HelpCloseCourseByCourse();
        List<Action> subActionsArray = new ArrayList<>();
        subActionsArray.add(deleteCourse);
        then(subActionsArray, () -> {
            departmentPS.getCourseList().remove(courseId);
            complete(("Close Course is done " + "The course: " + courseId + " is closed by the department " + actorId));
            departmentPS.addRecord(actionName);
        });
        sendMessage(deleteCourse, courseId, coursePS);
    }
}
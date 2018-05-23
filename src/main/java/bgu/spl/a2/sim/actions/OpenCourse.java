package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;


public class OpenCourse extends Action {

    protected String courseName;
    protected int availableSpaces;
    protected List<String> prerequisites;

    /**
     * @param courseName      the course name
     * @param availableSpaces available spots initially to the course
     * @param prerequisites   a list of courses name that set to the new course as prerequisites
     */
    public OpenCourse(String department, String courseName, int availableSpaces, List<String> prerequisites) {
        super();
        actionName = "Open Course";
        this.courseName = courseName;
        this.availableSpaces = availableSpaces;
        this.prerequisites = prerequisites;

    }

    /**
     * This action extend {@link bgu.spl.a2.Action} and it opens a new course in a specified department. The course has an initially
     * available spaces and a list of prerequisites.
     * this action initially submitted to the Department's actor.
     * this action send a sub action  {@link bgu.spl.a2.sim.actions.HelpEmptyAction
     * to the course for opening queue
     * after the the subaction completed, the department add the course to its courseList
     */
    @Override
    protected void start() {
        CoursePrivateState newCoursePS = new CoursePrivateState();
        newCoursePS.setAvailableSpots(availableSpaces);
        newCoursePS.setPrequisites(prerequisites);
        DepartmentPrivateState departmentPS = ((DepartmentPrivateState) pool.getPrivateStates(actorId));
        HelpEmptyAction createNewCourse = new HelpEmptyAction();
        List<Action> subActionsArray = new ArrayList<>();
        subActionsArray.add(createNewCourse);

        then(subActionsArray, () -> {
            departmentPS.getCourseList().add(courseName);
            complete("Course open is done " + "courseName: " + courseName);
            departmentPS.addRecord(actionName);
        });
        sendMessage(createNewCourse, courseName, newCoursePS);
    }
}

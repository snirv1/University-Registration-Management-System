package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;


public class RegisterWithPreferences extends Action {

    protected List<String> gradeList;
    protected List<String> coursesList;
    protected String studentId;

    /**
     * @param studentId   the student the trying to register
     * @param coursesList course list ordering by preference
     * @param gradeList   the garde list according to the course list
     */
    public RegisterWithPreferences(String studentId, List<String> coursesList, List<String> gradeList) {
        super();
        super.actionName = "Register With Preferences";
        this.coursesList = coursesList;
        this.gradeList = gradeList;
        this.studentId = studentId;

    }

    /**
     * This action extend {@link bgu.spl.a2.Action} and it try to register the student
     * the course List given . the action try the first if succeeded send massage to
     * the student {@link bgu.spl.a2.sim.actions.HelpRegisterStudentAction}
     * to update his records
     * if failed, do the action again recursively
     * exculded the first course. the student register to at most one course.
     */
    @Override
    protected void start() {
        StudentPrivateState studentPS = ((StudentPrivateState) pool.getPrivateStates(studentId));
        CoursePrivateState coursePS = (CoursePrivateState) pool.getPrivateStates(actorId);
        if (coursePS.getRegStudents().contains(studentId)) {
            complete("Register With Preferences is done " + "the student already registered");
        } else {
            String currentGrade = gradeList.get(0);
            List<String> prequisites = coursePS.getPrequisites();
            boolean studentMeetsPre = meetPrequisites(prequisites);
            List<Action> subActionsArray = new ArrayList<>();
            if (studentMeetsPre && coursePS.getAvailableSpots() > 0) {
                Integer gradeInt;
                if (currentGrade.equals("-")) {
                    gradeInt = new Integer(-1);
                } else {
                    gradeInt = new Integer(currentGrade);
                }
                coursePS.setAvailableSpots(coursePS.getAvailableSpots() - 1);
                HelpRegisterStudentAction registerStudentAction = new HelpRegisterStudentAction(actorId, gradeInt);
                subActionsArray.add(registerStudentAction);
                then(subActionsArray, () -> {
                    coursePS.getRegStudents().add(studentId);
                    coursePS.setRegistered(coursePS.getRegistered() + 1);
                    complete("Register With Preferences is done " + "register succeed");
                });
                sendMessage(registerStudentAction, studentId, studentPS);
                coursePS.addRecord(actionName);
            } else {
                coursesList.remove(0);
                gradeList.remove(0);
                if (!coursesList.isEmpty()) {
                    RegisterWithPreferences nextRegistration = new RegisterWithPreferences(studentId, coursesList, gradeList);
                    sendMessage(nextRegistration, coursesList.get(0), pool.getPrivateStates(coursesList.get(0)));
                    complete("Register With Preferences is done " + "didn't success to register-student didn't meet the pre - sent to next course");
                    coursePS.addRecord(actionName);
                } else {
                    complete("Register With Preferences is done " + "End of list");
                    coursePS.addRecord(actionName);
                }
            }
        }
    }

    /**
     * @param prequisites list of pre courses that the student should be
     *                    registered to them
     * @return true if the student meet the prerequisites else return false
     */
    private boolean meetPrequisites(List<String> prequisites) {
        boolean isMeetPrequisites = true;
        StudentPrivateState student = (StudentPrivateState) pool.getPrivateStates(studentId);
        for (String preCourse : prequisites) {
            if (student.getGrades().get(preCourse) == null) {
                isMeetPrequisites = false;
                break;
            }
        }
        if (isMeetPrequisites) {
            return true;
        } else {
            return false;
        }

    }
}

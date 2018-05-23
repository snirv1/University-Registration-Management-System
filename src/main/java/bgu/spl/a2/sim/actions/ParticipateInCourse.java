package bgu.spl.a2.sim.actions;


import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;


public class ParticipateInCourse extends Action {
    protected String studentId;
    protected String grade;

    /**
     * @param studentID represent the student should be added the the course
     * @param grade     the grade of the student if he success to register the course
     */


    public ParticipateInCourse(String studentID, String grade) {
        super();
        super.actionName = "Participate In Course";
        this.studentId = studentID;
        this.grade = grade;

    }

    /**
     * This action extend {@link bgu.spl.a2.Action} and it try to register
     * the student in the course, check if the student meets the prerequisites
     * for the course and the course has available spot the send massage {@link bgu.spl.a2.sim.actions.HelpRegisterStudentAction}
     * to the student
     * to update its lists
     * initially submitted to the course's actor.
     */

    @Override
    protected void start() {
        StudentPrivateState studentPS = ((StudentPrivateState) pool.getPrivateStates(studentId));
        CoursePrivateState coursePS = (CoursePrivateState) pool.getPrivateStates(actorId);
        if (coursePS.getRegStudents().contains(studentId)) {
            complete("Participate In Course is done " + "the student already registered");
        } else {
            List<String> prequisites = coursePS.getPrequisites();
            boolean studentMeetsPre = meetPrequisites(prequisites);
            List<Action> subActionsArray = new ArrayList<>();
            if (studentMeetsPre && coursePS.getAvailableSpots() > 0 && !coursePS.getRegStudents().contains(studentId)) {
                Integer gradeInt;
                if (grade.equals("-")) {
                    gradeInt = new Integer(-1);
                } else {
                    gradeInt = new Integer(grade);
                }
                coursePS.setAvailableSpots(coursePS.getAvailableSpots() - 1);
                HelpRegisterStudentAction registerStudentAction = new HelpRegisterStudentAction(actorId, gradeInt);
                subActionsArray.add(registerStudentAction);

                then(subActionsArray, () -> {
                    coursePS.getRegStudents().add(studentId);
                    coursePS.setRegistered(coursePS.getRegistered() + 1);
                    complete("Participate In Course is done " + "register succeed!");
                    coursePS.addRecord(actionName);

                });
                sendMessage(registerStudentAction, studentId, studentPS);
            } else {
                complete("Participate In Course is done " + "register failed");
                coursePS.addRecord(actionName);
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

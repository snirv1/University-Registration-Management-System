package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;


public class AddSpaces extends Action {

    protected int numOfSpacesToAdd;

    /**
     * @param numOfSpacesToAdd number of the available spots should be added to the course
     */
    public AddSpaces(int numOfSpacesToAdd) {
        super();
        super.actionName = "Add Spaces";
        this.numOfSpacesToAdd = numOfSpacesToAdd;

    }


    /**
     * This action extend {@link bgu.spl.a2.Action} and it increase the number of
     * available spaces for the course.
     * initially submitted to the course's actor.
     */
    @Override
    protected void start() {
        CoursePrivateState coursePS = (CoursePrivateState) pool.getPrivateStates(actorId);
        coursePS.setAvailableSpots(coursePS.getAvailableSpots() + numOfSpacesToAdd);
        coursePS.addRecord(actionName);
        complete("Add Spaces Done: " + numOfSpacesToAdd + " Spaces");
    }

}




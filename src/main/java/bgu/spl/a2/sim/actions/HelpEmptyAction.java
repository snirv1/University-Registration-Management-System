package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;


public class HelpEmptyAction extends Action {

    public HelpEmptyAction() {
        super();
        super.actionName = "Empty Action";
    }

    /**
     * This action extend {@link bgu.spl.a2.Action} . this empty action should
     * return immediately.kind of confirmation
     * its should prevent the situation that the action caller will
     * assume the action was done before its really done.
     * sent by {@link bgu.spl.a2.sim.actions.AddStudent and
     * {@link bgu.spl.a2.sim.actions.OpenCourse
     * help us to be sure that actor's queue was opened
     **/
    @Override
    protected void start() {
        complete("true");
    }
}



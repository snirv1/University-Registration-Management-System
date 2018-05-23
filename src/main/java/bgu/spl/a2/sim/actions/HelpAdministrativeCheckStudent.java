package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.SuspendingMutex;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;


public class HelpAdministrativeCheckStudent extends Action {
    protected Promise<Computer> computerPromise;
    protected List<String> listOfConditions;

    /**
     * @param listOfConditions is a list of courses that will be check with the student
     * @param computerPromise  a promise of computer that will check the student as it will be available
     */

    public HelpAdministrativeCheckStudent(List<String> listOfConditions, Promise<Computer> computerPromise) {
        super();
        super.actionName = "Administrative Check Student";
        this.computerPromise = computerPromise;
        this.listOfConditions = listOfConditions;
    }

    /**
     * this sub action extend {@link bgu.spl.a2.Action and its a subaction
     * from administrative  {@link bgu.spl.a2.sim.actions.AdministrativeCheck that sent to a student with list of
     * conditions (courses) and send them to a promised computer that will return a signature. this
     * signature will set int the student's signature.
     */
    @Override
    protected void start() {
        StudentPrivateState studentPS = (StudentPrivateState) pool.getPrivateStates(actorId);
        computerPromise.subscribe(() -> {
                    Long signature = computerPromise.get().checkAndSign(listOfConditions, studentPS.getGrades());
                    studentPS.setSignature(signature);
                    complete("signature: " + signature);
                }
        );
    }

}
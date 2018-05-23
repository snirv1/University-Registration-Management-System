package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;

public class Computer {

    String computerType;
    long failSig;
    long successSig;

    public Computer(String computerType, long successSig, long failSig) {

        this.computerType = computerType;
        this.successSig = successSig;
        this.failSig = failSig;
    }

    /**
     * this method checks if the courses' grades fulfill the conditions
     *
     * @param courses       courses that should be pass
     * @param coursesGrades courses' grade
     * @return a signature if couersesGrades grades meet the conditions
     */
    public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades) {
        boolean isFulfill = true;
        for (String course : courses) {
            Integer check = coursesGrades.get(course);
            if (check != null) {
                if (check < 56) {
                    isFulfill = false;
                    break;
                }
            } else {
                isFulfill = false;
                break;
            }
        }
        if (isFulfill) {
            return successSig;
        } else return failSig;

    }
}
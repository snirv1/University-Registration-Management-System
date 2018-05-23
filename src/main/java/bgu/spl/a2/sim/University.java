package bgu.spl.a2.sim;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * This class use to read the input from the Json file.
 */

public class University {
    public int threads;
    @SerializedName("Computers")
    public JsonComputer[] Computers;
    @SerializedName("Phase 1")
    public JsonAction[] Phase1;
    @SerializedName("Phase 2")
    public JsonAction[] Phase2;
    @SerializedName("Phase 3")
    public JsonAction[] Phase3;

    public int getThreads() {
        return threads;
    }

    public JsonComputer[] getComputers() {
        return Computers;
    }

    public List getPhase1() {
        return Arrays.asList(Phase1);
    }

    public List getPhase2() {
        return Arrays.asList(Phase2);
    }

    public List getPhase3() {
        return Arrays.asList(Phase3);
    }
}
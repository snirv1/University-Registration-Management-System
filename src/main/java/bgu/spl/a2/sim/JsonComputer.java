package bgu.spl.a2.sim;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class use to read the input from the Json file.
 */

public class JsonComputer {
    @SerializedName("Type")
    @Expose
    public String Type;
    @SerializedName("Sig Success")
    @Expose
    public String sigSuccess;
    @SerializedName("Sig Fail")
    @Expose
    public String sigFail;


    public String getType() {
        return Type;
    }

    public long getSigFail() {
        return Long.parseLong(sigFail);
    }

    public long getSigSuccess() {
        return Long.parseLong(sigSuccess);
    }
}
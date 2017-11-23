package hmm.decoding;
import hmm.State;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikol on 22/11/2017.
 */
public class OmegaSlice implements Serializable {

    private int index;
    private Map<State, BigDecimal> stateProbs = new HashMap<>();
    private OmegaMap omega;


    public OmegaSlice(OmegaMap omega, int i){
        this.omega = omega;
        index = i;

        for(State s : omega.getHMM().getStates())
            stateProbs.put(s, BigDecimal.ZERO);
    }

    public void setProbability(State k, BigDecimal prob){
        stateProbs.put(k, prob);
    }

    public BigDecimal getStateProbability(State k) {
        return stateProbs.get(k);
    }
}

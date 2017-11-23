package hmm.decoding;

import hmm.Emission;
import hmm.HMM;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hmm.etc.HMMUtil;
import hmm.State;
import hmm.observations.Observation;

/**
 * Created by nikol on 22/11/2017.
 */
public class OmegaMap implements Serializable {

    private HMM hmm;
    private Map<Integer, OmegaSlice> slices = new HashMap<>();
    private int N;
    private Collection<Emission> emissions;

    public OmegaMap(HMM hmm, Collection<Emission> emissions){
        this.hmm = hmm;
        this.emissions = emissions;
        N = emissions.size();
    }

    public void fill(){
        int n=0;

        System.out.println("Computing omega table...");
        System.out.print("|"+HMMUtil.repeat("-",100)+"|\n|");

        int delim = N / 100;

        for(Emission em : emissions){
            n++;

            if(n % delim == 0)
                System.out.print("-");

            for(State k : hmm.getStates())
                computeOmega(k, n, em);
        }
        System.out.println("|\n");
    }

    private void computeOmega(State k, int n, Emission xn){
        BigDecimal omega = BigDecimal.ZERO;

        if(n > 1)
            for(State j : hmm.getStates()){
                BigDecimal prob = BigDecimal.ONE;

                prob = prob.multiply(k.getProbability(xn), HMMUtil.context);     //Probability of emitting xn in state k
                prob = prob.multiply(getProb(j, n-1), HMMUtil.context);          //Probability of being in state j at n-1
                prob = prob.multiply(j.getProbability(k), HMMUtil.context);      //Probability of transitioning from j->k

                omega = omega.max(prob);
            }
        else {
            omega = BigDecimal.ONE;

            omega = omega.multiply(hmm.getStartProbability(k), HMMUtil.context); //Probability of starting in state k
            omega = omega.multiply(k.getProbability(xn), HMMUtil.context);       //Probability of emitting xn
        }

        setProb(k, n, omega);
    }

    private void setProb(State k, int n, BigDecimal prob){
        getSlice(n).setProbability(k, prob);
    }

    public BigDecimal getProb(State k, int n){
        return getSlice(n).getStateProbability(k);
    }

    private OmegaSlice getSlice(int i){
        if(slices.containsKey(i))
            return slices.get(i);

        OmegaSlice slice = new OmegaSlice(this, i);
        slices.put(i, slice);
        return slice;
    }

    public HMM getHMM(){
        return hmm;
    }

}

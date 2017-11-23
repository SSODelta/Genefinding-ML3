package hmm.decoding;

import hmm.Emission;
import hmm.HMM;
import hmm.State;
import hmm.etc.DecodingPrinter;
import hmm.etc.HMMUtil;
import hmm.observations.NucleotideStream;
import hmm.observations.Observation;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Created by nikol on 22/11/2017.
 */
public class ViterbiDecoder {

    private HMM hmm;

    public final void decode(int i) throws IOException {
        System.out.println("===DECODING GENOME "+i+"\n");
        NucleotideStream ns = new NucleotideStream(i);
        DecodingPrinter.outputAsFASTA(decode(ns.toList()), "true-ann"+i+".fa");
    }

    public ViterbiDecoder(HMM hmm){
        this.hmm = hmm;
    }

    public State[] decode(List<Emission> ems){
        OmegaMap map = new OmegaMap(hmm, ems);
        map.fill();
        return decode(ems, map);
    }

    private State[] decode(List<Emission> ems, OmegaMap map){

        System.out.println("Decoding...");
        System.out.print("|"+HMMUtil.repeat("-",100)+"|\n|");

        int N = ems.size();
        int delim = N/100;

        State[] Z = new State[N+1];
        BigDecimal[] probs = new BigDecimal[N+1];

        Z[0] = State.EMPTY;

        //Fill probs with 0
        for(int i=0; i<N+1; i++)
            probs[i] = BigDecimal.ZERO;

        //Start with N
        for(State k : hmm.getStates()){
            BigDecimal prob = map.getProb(k, N);
            if(prob.compareTo(probs[N]) > 0){
                probs[N] = prob;
                Z[N] = k;
            }
        }

        //N-1 to 1
        for(int n=N-1; n>=0; n--){
            if(n % delim == delim-1)
                System.out.print("-");
            for(State k : hmm.getStates()){
                BigDecimal prob = BigDecimal.ONE;

                prob = prob.multiply(map.getProb(k, n), HMMUtil.context);            //Probability of being in state k at step n
                prob = prob.multiply(                           //Probability of Z[n+1] emitting observable x[n+1]
                        Z[n+1].getProbability(
                                ems.get(n)), HMMUtil.context);
                prob = prob.multiply(k.getProbability(Z[n+1]), HMMUtil.context); //Probability of transitioning to Z[n+1]

                if(prob.compareTo(probs[n]) > 0){
                    Z[n] = k;
                    probs[n] = prob;
                }
            }
        }
        System.out.println("|\n");

        return Z;
    }

}

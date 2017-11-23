package hmm;

import hmm.etc.HMMUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nikol on 22/11/2017.
 */
public class State implements Comparable<State>, Serializable {
    private final String id;
    private final Coding coding;

    public static final State EMPTY = new State("",Coding.EMPTY);

    private Map<State, BigDecimal> transitionProbs  = new HashMap<>();
    private Map<Emission, BigDecimal> emissionProbs = new HashMap<>();

    public State(String id, Coding coding){
        this.id = id;
        this.coding = coding;
    }

    public boolean canTransition(State s){
        if(!transitionProbs.containsKey(s))
            return false;
        return transitionProbs.get(s).compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean canEmit(Emission e){
        if(!emissionProbs.containsKey(e))
            return false;
        return emissionProbs.get(e).compareTo(BigDecimal.ZERO) > 0;
    }

    ////// GETTERS
    public String getID() {
        return id;
    }

    public Coding getCoding() {
        return coding;
    }

    public Set<State> getTransitions(){
        return transitionProbs.keySet();
    }

    public Set<Emission> getEmissions(){
        return emissionProbs.keySet();
    }

    public BigDecimal getProbability(State s){
        if(transitionProbs.containsKey(s))
            return transitionProbs.get(s);

        return BigDecimal.ZERO;
    }

    public BigDecimal getProbability(Emission e){
        if(emissionProbs.containsKey(e))
            return emissionProbs.get(e);

        return BigDecimal.ZERO;
    }

    public String toString(){
        return "State "+id+", "+coding;
    }


    ////// SETTERS
    public void setTransitionProb(State s, BigDecimal d){
        transitionProbs.put(s, d);
    }

    public void setEmissionProb(Emission e, BigDecimal d){
        emissionProbs.put(e, d);
    }

    public void addTransition(State s, BigDecimal d){
        if(transitionProbs.containsKey(s))
            throw new TransitionAlreadyExistsException("A transition to "+s+" already exists in state "+id+".");
        transitionProbs.put(s, d);
    }

    public void addEmission(Emission e, BigDecimal d){
        if(emissionProbs.containsKey(e))
            throw new EmissionAlreadyExistsException("The emission "+e+" already exists in state "+id+".");
        emissionProbs.put(e, d);
    }

    public void verify(){
        HMMUtil.verifyMap(transitionProbs);
        HMMUtil.verifyMap(emissionProbs);
    }

    @Override
    public int compareTo(State o) {
        return id.compareTo(o.id);
    }

    public String getSimpleID() {
        return getID().replace(" ","").replace("[", "").replace("]", "");
    }

    class EmissionAlreadyExistsException extends RuntimeException {
        public EmissionAlreadyExistsException(String s){
            super(s);
        }
    }
    class TransitionAlreadyExistsException extends RuntimeException {
        public TransitionAlreadyExistsException(String s){
            super(s);
        }
    }
}

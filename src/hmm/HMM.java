package hmm;

import hmm.graphviz.GraphViz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by nikol on 22/11/2017.
 */
public class HMM implements Serializable {

    private Map<State, BigDecimal> startProbs = new HashMap<>();

    private State nonCodingState;

    private String name;

    public HMM(String name){
        this.name = name;
    }

    public BigDecimal getStartProbability(State k){
        return startProbs.get(k);
    }

    public void prune(){
        Set<State> isolated = new HashSet<>(getStates());

        for(State p : getStates()){
            for(State q : getStates()){
                if(p.canTransition(q))
                    isolated.remove(q);
            }
        }
        int n = startProbs.size();
        for(State t : isolated)
            startProbs.remove(t);
        if(n == startProbs.size())
            return;

        prune();
    }

    public BigDecimal getProbability(List<State> states, List<Emission> ems){
        if(states.size() != ems.size())
            throw new RuntimeException("The number of states ("+states.size()+") and emissions ("+ems.size()+") must be equal.");

        int N = states.size();

        BigDecimal p = startProbs.get(states.get(0));

        for(int n=0; n<N; n++){
            State s = states.get(n);
            Emission e = ems.get(n);

            p = p.multiply(s.getProbability(e));
            if(n>0) {
                State prev = states.get(n-1);
                p = p.multiply(prev.getProbability(s));
            }
        }

        return p;
    }

    public State getState(String id){
        for(State s : getStates())
            if(s.getID().equals(id))
                return s;

        return null;
    }

    public void verify(){
        for(State s : getStates())
            s.verify();
    }

    public void addState(State s){
        startProbs.put(s, BigDecimal.ZERO);
    }

    public Set<State> getStates() {
        return startProbs.keySet();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(name+"\n-------\n");


        List<State> states = new ArrayList<>(getStates());
        Collections.sort(states);

        for(State s : states) {
            sb.append(s.toString() + ":\n");
            for(State t : s.getTransitions()){
                sb.append("\t-> "+t+" with p="+s.getProbability(t)+"\n");
            }
            for(Emission e : s.getEmissions()){
                sb.append("\temits "+e+" with p="+s.getProbability(e)+"\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public String asGraphViz(){
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());

        for(State s : getStates()){
            for(State t : s.getTransitions()){
                gv.addln(s.getSimpleID() + "-> "+t.getSimpleID()+";");
            }
        }

        gv.addln(gv.end_graph());
        return gv.getDotSource();
    }

    public State getNonCodingState() {
        return nonCodingState;
    }

    public void setNonCodingState(State s){
        if(nonCodingState!=null)
            throw new MultipleNonCodingStateException("There is already a non-coding state: "+nonCodingState.toString()+", so cannot add "+s+".");
        nonCodingState = s;
        startProbs.put(s, BigDecimal.ONE);
    }

    class MultipleNonCodingStateException extends RuntimeException {
        public MultipleNonCodingStateException(String s){
            super(s);
        }
    }
}

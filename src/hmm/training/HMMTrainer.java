package hmm.training;

import hmm.Emission;
import hmm.HMM;
import hmm.etc.HMMUtil;
import hmm.State;
import hmm.observations.Observation;
import hmm.observations.StateStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nikol on 22/11/2017.
 */
public class HMMTrainer {

    private HMM hmm;
    private StateStream stream;
    private List<Observation> observed;

    public HMMTrainer(HMM hmm, StateStream stream){
        this.hmm = hmm;
        this.stream = stream;
        observed = new ArrayList<>();
    }

    public List<Observation> getObserved(){
        return observed;
    }

    public void train(int count) throws IOException {
        HMMCounter hmmc = new HMMCounter(stream.next().getLatentState());


        System.out.println("Training...");
        System.out.print("|"+HMMUtil.repeat("-",100)+"|\n|");

        int delim = count/100;

        for(int i=0; i<count; i++) {
            Observation.StateGiven state = stream.next();
            hmmc.add(state);
            observed.add(state);
            if(i%delim == delim-1)
                System.out.print("-");
        }
        System.out.println("|\n");

        //For every state
        for(State a : hmm.getStates()){

            //For every emission
            for(Emission em : Emission.values()){
                BigDecimal prob = hmmc.getProbability(a, em);
                //System.out.println(a+" emits "+em+":\t"+prob);
                a.setEmissionProb(em, prob);
            }

            //For every other state
            for(State b : hmm.getStates()){
                BigDecimal prob = hmmc.getProbability(a,b);
                //System.out.println(a+" -> "+b+":\t"+prob);
                a.setTransitionProb(b, prob);
            }
        }

        int p = hmm.getStates().size();
        hmm.prune();
        int q = hmm.getStates().size();
        //System.out.println("Removed "+(p-q)+" states");
        hmm.verify();
    }


    private class HMMCounter{

        Map<State, Map<State, BigDecimal>> transition_matrix = new HashMap<>();
        Map<State, BigDecimal> total_transitions = new HashMap<>();

        Map<State, Map<Emission, BigDecimal>> emission_matrix = new HashMap<>();
        Map<State, BigDecimal> total_emissions = new HashMap<>();

        State currentState;

        public void add(Observation.StateGiven obs){
            State newState = obs.getLatentState();
            Emission em = obs.getObservedEmission();

            addTransition(currentState, newState);
            addEmission(newState, em);

            currentState = newState;
        }

        public BigDecimal getProbability(State a, Emission e){
            BigDecimal seen = emission_matrix.get(a).get(e);
            BigDecimal total = total_emissions.get(a);

            if(total.compareTo(BigDecimal.ZERO) > 0)
                return seen.divide(total, HMMUtil.context);

            return BigDecimal.ZERO;
        }

        public BigDecimal getProbability(State a, State b){
            BigDecimal seen = transition_matrix.get(a).get(b);
            BigDecimal total = total_transitions.get(a);

            if(total.compareTo(BigDecimal.ZERO) > 0)
                return seen.divide(total, HMMUtil.context);

            return BigDecimal.ZERO;
        }

        public void addTransition(State a, State b){
            increment(total_transitions, a);
            increment(transition_matrix.get(a), b);
        }

        public void addEmission(State a, Emission e){
            increment(total_emissions, a);
            increment(emission_matrix.get(a), e);
        }

        public <T> void increment(Map<T, BigDecimal> map, T t){
            map.put(t, map.get(t).add(BigDecimal.ONE, HMMUtil.context));
        }

        public HMMCounter(State startState){
            currentState = startState;
            //For every state
            for(State a : hmm.getStates()){

                //Set total transitions, emissions to 1 (pseudocount)
                total_transitions.put(a, BigDecimal.ZERO);
                total_emissions.put(a, BigDecimal.ZERO);

                Map<State, BigDecimal>  a_transition_map = new HashMap<>();
                Map<Emission, BigDecimal> a_emission_map = new HashMap<>();

                //Initialize submaps
                transition_matrix.put(a, a_transition_map);
                emission_matrix.put(a, a_emission_map);

                //For every other state
                for(State b : hmm.getStates()){
                    a_transition_map.put(b, BigDecimal.ZERO);
                }

                //For every emission
                for(Emission e : Emission.values()){
                    a_emission_map.put(e, BigDecimal.ZERO);
                }

            }
        }

    }

}

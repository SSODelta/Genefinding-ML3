package hmm.observations;

import hmm.HMM;
import hmm.etc.HMMUtil;
import hmm.State;

import java.io.IOException;
import java.util.*;

/**
 * Created by nikol on 22/11/2017.
 */
public class StateStream extends AbstractStream<Observation.StateGiven> {

    private HMMStream<Observation.Annotated> as;
    private State currentState;
    private Queue<Observation.StateGiven> waiting;

    public StateStream(HMM hmm, int... data) throws IOException {
        as = new CompositionStream<>(HMMUtil.intsToStreams(data));

        currentState = hmm.getNonCodingState();

        waiting = new LinkedList<>();
    }

    public StateStream(HMM hmm, int i) throws IOException {
        this(hmm, "data/genome"+i+".fa", "data/true-ann"+i+".fa");
    }

    public StateStream(HMM hmm, String genome, String annotations) throws IOException {
        as = new AnnotatedStream(genome, annotations);

        currentState = hmm.getNonCodingState();

        waiting = new LinkedList<>();
    }

    public Observation.StateGiven next() throws IOException{
        if(waiting.isEmpty()){
            if(currentState == null)
                throw new EndOfStreamException("The stream was empty");
            List<Observation.StateGiven> list = possibleStates(new HashSet<>(Arrays.asList(new State[]{currentState})));
            for(int i=list.size()-1; i>=0; i--)
                waiting.add(list.get(i));
        }

        return current();
    }

    private Observation.StateGiven current(){
        Observation.StateGiven sg = waiting.poll();
        currentState = sg.getLatentState();
        if(currentState == null)
            throw new EndOfStreamException("The stream was empty");
        return sg;
    }


    private List<Observation.StateGiven> possibleStates(Set<State> startStates) throws IOException {
        return possibleStates(startStates, 0);
    }
    private static final int MAX_ITS = 1000;
    private List<Observation.StateGiven> possibleStates(Set<State> startStates, int i)throws IOException{
        if(i >= MAX_ITS)
            throw new RuntimeException("Unable to resolve ambiguous state transitions");

        //Possible next states
        Set<State> possible = new HashSet<>();

        //Get next observation from stream
        Observation.Annotated nextObservation = as.next();

        if(startStates.isEmpty()){
            throw new RuntimeException(""+nextObservation);
        }

        //For every possible start state
        for(State state : startStates) {
            //Check all possible states
            for (State possibleNextState : state.getTransitions()) {
                //ignore state if different coding type
                if (possibleNextState.getCoding() != nextObservation.getAnnotation())
                    continue;

                //ignore state if impossible to emit observation
                if (!possibleNextState.canEmit(nextObservation.getObservedEmission()))
                    continue;

                if(state.canTransition(possibleNextState)){
                    possible.add(possibleNextState);
                }
            }
        }

        //If only a single element, return it
        if(possible.size() == 1) {
            State s = null;
            for(State ss : possible) s = ss;
            return new ArrayList<>(Arrays.asList(
                    new Observation.StateGiven[]{
                            new Observation.StateGiven(nextObservation.getObservedEmission(), s)
                    }
            ));
        }

        List<Observation.StateGiven> recursion = null;
        try {
            recursion = possibleStates(possible, i + 1);
        } catch(RuntimeException e){
            throw new RuntimeException("we started at "+startStates+".\n it was determined that the next possible states were: "+possible+"\nthe next observable was "+e.getMessage());
        }

        State resolvedCurrent = null;
        State transitionTo = recursion.get(recursion.size()-1).getLatentState();
        int p = 0;

        for(State state : possible){
            if(state.canTransition(transitionTo)){
                if(1.0/++p >= Math.random())
                    resolvedCurrent = state;
                break;
            }
        }

        if(resolvedCurrent == null)
            throw new RuntimeException("unable to transition from "+resolvedCurrent+" to "+transitionTo+".");

        recursion.add(new Observation.StateGiven(nextObservation.getObservedEmission(), resolvedCurrent));

        return recursion;
    }

    class NoSuchTransitionException extends RuntimeException {
        public NoSuchTransitionException(String s){
            super(s);
        }
    }

    class AmbiguousTransitionException extends RuntimeException {
        public AmbiguousTransitionException(String s){
            super(s);
        }
    }

    class InvalidAnnotationException extends RuntimeException {
        public InvalidAnnotationException(String s){
            super(s);
        }
    }
}

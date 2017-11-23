package hmm.observations;

import hmm.Coding;
import hmm.Emission;
import hmm.State;

/**
 * Created by nikol on 22/11/2017.
 */

public class Observation {

    private Emission observation;

    public Observation(Emission observation){
        this.observation = observation;
    }

    public Emission getObservedEmission(){
        return observation;
    }

    public String toString(){
        return observation.toString();
    }

    //Given the coding/noncoding (raw input from FASTA)
    public static class Annotated extends Observation {

        private Coding annotation;

        public Annotated(Emission observation, Coding annotation) {
            super(observation);
            this.annotation = annotation;
        }

        public Coding getAnnotation() {
            return annotation;
        }

        public String toString(){
            return "("+super.toString()+", "+annotation.toString()+")";
        }
    }

    //Given the latent state z
    public static class StateGiven extends Observation {

        private State state;

         public StateGiven(Emission observation, State state) {
            super(observation);
             this.state = state;
        }

        public State getLatentState() {
            return state;
        }

        public String toString(){
            return "("+super.toString()+", ["+state.toString()+"])";
        }
    }

}
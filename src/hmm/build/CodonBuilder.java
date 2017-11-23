package hmm.build;

import hmm.Coding;
import hmm.Emission;
import hmm.HMM;
import hmm.State;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by nikol on 22/11/2017.
 */
public class CodonBuilder {

    private static final String[] START_CODONS_N = new String[]{
            "ATG", "ATC", "GTG", "ATT", "CTG", "GTT", "CTC", "TTA", "TTG"
    };
    private static final String[] STOP_CODONS_N = new String[]{
            "TAG","TGA","TAA"
    };

    private static final String[] START_CODONS_R = new String[]{
            "TAT", "ATG", "GAT", "CAT", "AAT", "TAC", "CAC", "CAA", "CAG"
    };
    private static final String[] STOP_CODONS_R = new String[]{
            "TTA", "CTA", "TCA"
    };

    public static HMM build() {
        HMM hmm = getBaseModel();

        for(String codon : START_CODONS_N)
            addStartCodonN(hmm, codon);
        for(String codon : STOP_CODONS_N)
            addStopCodonN(hmm, codon);

        for(String codon : START_CODONS_R)
            addStartCodonR(hmm, codon);
        for(String codon : STOP_CODONS_R)
            addStopCodonR(hmm, codon);

        return hmm;
    }

    private static State[] getCodonStates(HMM hmm, Coding coding, String id, String codon){
        int i=0;
        State[] states = new State[codon.length()];
        for(char base : codon.toCharArray())
            states[i++] = getNewState(hmm, id+i, base, coding);
        return states;
    }
    private static void addTransition(State a, State b){
        a.addTransition(b, NOT_ZERO);
    }

    private static void createChain(HMM hmm, Coding coding, State start, State[] ends, String id, String codon){
        State[] states = getCodonStates(hmm, coding, id,codon);

        addTransition(start, states[0]);

        for(int j=0; j<states.length-1; j++)
            addTransition(states[j], states[j+1]);

        for(State end : ends)
            addTransition(states[codon.length()-1], end);
    }
    private static void createChain(HMM hmm, Coding coding, State start, State end, String id, String codon){
        State[] states = getCodonStates(hmm, coding, id,codon);

        addTransition(start, states[0]);

        for(int j=0; j<states.length-1; j++)
            addTransition(states[j], states[j+1]);

        addTransition(states[codon.length()-1], end);
    }
    private static void addStopCodonN(HMM hmm, String codon){
        createChain(hmm, Coding.CODING_LEFT_TO_RIGHT, CODING_N_STOP, new State[]{NONCODING, CODING_N_START, CODING_R_START}, "StopCodonN", codon);
    }
    private static void addStartCodonN(HMM hmm, String codon){
        createChain(hmm, Coding.CODING_LEFT_TO_RIGHT, NONCODING, CODING_N_START, "StartCodonN", codon);
    }

    private static void addStartCodonR(HMM hmm, String codon){
        createChain(hmm, Coding.CODING_RIGHT_TO_LEFT, CODING_R_STOP, new State[]{NONCODING, CODING_N_START, CODING_R_START}, "StopCodonR", codon);
    }
    private static void addStopCodonR(HMM hmm, String codon){
        createChain(hmm, Coding.CODING_RIGHT_TO_LEFT, NONCODING, CODING_R_START, "StartCodonR", codon);
    }


    private static int i = 0;
    private static State getNewState(HMM hmm, String id, char base, Coding coding){
        State state = new State(id+" "+base+" ["+(++i)+"]", coding);
        hmm.addState(state);
        Emission e = Emission.getEmission(""+base);
        state.addEmission(e, BigDecimal.ONE);

        return state;
    }

    private static State CODING_N_START = null,
                         CODING_N_STOP  = null,
                         CODING_R_START = null,
                         CODING_R_STOP  = null,
                         NONCODING      = null;

    private static void allowAllEmissions(State s){
        s.addEmission(Emission.A, NOT_ZERO);
        s.addEmission(Emission.C, NOT_ZERO);
        s.addEmission(Emission.G, NOT_ZERO);
        s.addEmission(Emission.T, NOT_ZERO);
    }
    private static BigDecimal NOT_ZERO = BigDecimal.ONE.divide(BigDecimal.TEN);
    private static HMM getBaseModel(){
        HMM hmm = new HMM("Codon Model");

        State nc = new State("NonCoding", Coding.NONCODING);
        nc.addTransition(nc, NOT_ZERO);
        allowAllEmissions(nc);
        hmm.addState(nc);
        hmm.setNonCodingState(nc);
        NONCODING = nc;

        State codingN1 = new State("Coding 1", Coding.CODING_LEFT_TO_RIGHT);
        State codingN2 = new State("Coding 2", Coding.CODING_LEFT_TO_RIGHT);
        State codingN3 = new State("Coding 3", Coding.CODING_LEFT_TO_RIGHT);
        hmm.addState(codingN1);
        hmm.addState(codingN2);
        hmm.addState(codingN3);

        allowAllEmissions(codingN1);
        allowAllEmissions(codingN2);
        allowAllEmissions(codingN3);

        CODING_N_START = codingN1;
        CODING_N_STOP  = codingN3;

        State codingR1 = new State("Reverse Coding 1", Coding.CODING_RIGHT_TO_LEFT);
        State codingR2 = new State("Reverse Coding 2", Coding.CODING_RIGHT_TO_LEFT);
        State codingR3 = new State("Reverse Coding 3", Coding.CODING_RIGHT_TO_LEFT);
        hmm.addState(codingR1);
        hmm.addState(codingR2);
        hmm.addState(codingR3);

        allowAllEmissions(codingR1);
        allowAllEmissions(codingR2);
        allowAllEmissions(codingR3);

        CODING_R_START = codingR1;
        CODING_R_STOP  = codingR3;

        codingN1.addTransition(codingN2, NOT_ZERO);
        codingN2.addTransition(codingN3, NOT_ZERO);
        codingN3.addTransition(codingN1, NOT_ZERO);

        codingR1.addTransition(codingR2, NOT_ZERO);
        codingR2.addTransition(codingR3, NOT_ZERO);
        codingR3.addTransition(codingR1, NOT_ZERO);

        return hmm;
    }

}

package hmm.build;

import hmm.*;
import hmm.etc.HMMUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nikol on 22/11/2017.
 */
public class RawStateBuilder {

    private static final String STATE_STRING = "State ";

    public static HMM build(String path) throws IOException {
        return build(Files.readAllLines(Paths.get(path)));
    }

    private static HMM build(List<String> lines){

        //Find all states
        Set<State> states = getStates(lines);

        //Create HMM
        HMM hmm = new HMM(lines.get(0));
        for(State s : states) {
            hmm.addState(s);
            if(s.getCoding() == Coding.NONCODING)
                hmm.setNonCodingState(s);
        }

        addContent(hmm, lines);

        hmm.verify();

        return hmm;
    }


    private static void addContent(HMM hmm, List<String> lines){
        State s = null;

        int i = 0;
        for(String line : lines){
            ++i;
            if(i==1)continue;
            if(line.length()==0 || line.startsWith("//"))continue;

            if(line.startsWith(STATE_STRING)){
                String id = line.substring(STATE_STRING.length(), line.indexOf(","));
                s = hmm.getState(id);
                continue;
            } else if(s == null){
                throw new RuntimeException("No state was defined at this point (linenumber="+i+")");
            } else if(line.contains("->")){
                addStateTransitions(hmm, s, line);
            } else if(line.toLowerCase().contains("emits: ")){
                addEmissionTransitions(hmm, s, line);
            } else {
                throw new RuntimeException("Invalid HMM format for line "+i+": "+line);
            }
        }
    }

    private static void addEmissionTransitions(HMM hmm, State s, String line){
        line = line.substring(line.toLowerCase().indexOf("emits:")+6).replace(" ","");

        String[] emissions = line.split(",");
        if(emissions.length == 0)
            throw new RuntimeException("There are no emissions from state "+s.getID());

        BigDecimal prob = BigDecimal.ONE.divide(BigDecimal.valueOf(emissions.length), HMMUtil.context);

        for(String em : emissions)
            s.addEmission(Emission.getEmission(em), prob);
    }

    private static void addStateTransitions(HMM hmm, State s, String line){
        line = line.substring(line.indexOf("->")+2).replace(" ","");

        String[] transitions = line.split(",");
        if(transitions.length == 0)
            throw new RuntimeException("There are no transitions from state "+s.getID());

        BigDecimal prob = BigDecimal.ONE.divide(BigDecimal.valueOf(transitions.length), HMMUtil.context);

        for(String tr : transitions)
            s.addTransition(hmm.getState(tr), prob);
    }

    private static Set<State> getStates(List<String> lines){

        Set<State> states = new HashSet<>();
        int i =0;
        for(String line : lines){
            i++;
            if(i==1)continue;
            if(line.startsWith(STATE_STRING)){

                String stateInfo = line.substring(STATE_STRING.length()).replace(":","");
                String[] split = stateInfo.split(", ");

                if(split.length != 2)
                    throw new RuntimeException("Invalid HMM input format for line "+i+": "+line);

                String id = split[0];
                Coding coding = Coding.getCoding(split[1]);

                State state = new State(id, coding);

                states.add(state);

            }
        }

        return states;
    }

}

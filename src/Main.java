import hmm.HMM;
import hmm.build.CodonBuilder;
import hmm.decoding.ViterbiDecoder;
import hmm.etc.HMMUtil;
import hmm.etc.DecodingPrinter;
import hmm.decoding.OmegaMap;
import hmm.observations.NucleotideStream;
import hmm.observations.StateStream;
import hmm.training.HMMTrainer;

/**
 * Created by nikol on 22/11/2017.
 */
public class Main {

    public static void main(String[] args){
        try {

            //System.out.println("Machine Learning Hand-in 3");
            HMM hmm = CodonBuilder.build();
                    //RawStateBuilder.build("models/procaryote-bothways.hmm");
            //System.out.println(hmm.asGraphViz());

            StateStream stream = new StateStream(hmm, 1,2,3,4,5);

            HMMTrainer trainer = new HMMTrainer(hmm, stream);

            trainer.train(11045065);

            HMMUtil.saveHMM(hmm, "out.hmm");

            ViterbiDecoder v = new ViterbiDecoder(hmm);

            v.decode(0);
            v.decode(7);
            v.decode(8);
            v.decode(9);
            v.decode(10);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

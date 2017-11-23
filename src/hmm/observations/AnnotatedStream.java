package hmm.observations;

import hmm.Coding;
import hmm.Emission;

import java.io.File;
import java.io.IOException;

/**
 * Created by nikol on 22/11/2017.
 */
public class AnnotatedStream extends AbstractStream<Observation.Annotated> {

    private FASTAReader fr1, fr2;

    private Observation.Annotated x = null;

    public AnnotatedStream(String genome, String annotations) throws IOException {
        fr1 = new FASTAReader(new File(genome));
        fr2 = new FASTAReader(new File(annotations));

        saveNext();
    }

    private void saveNext(){
        try {
            String base = fr1.next(),
                    ann = fr2.next();

            x = new Observation.Annotated(
                      Emission.getEmission(base),
                      Coding.getCoding(ann));
        } catch(Exception e){
            x = null;
        }
    }

    public boolean hasNext(){
        return x!=null;
    }

    public Observation.Annotated next(){
        Observation.Annotated y = x;
        saveNext();
        return y;
    }

}

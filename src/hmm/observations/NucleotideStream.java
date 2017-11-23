package hmm.observations;

import hmm.Emission;

import java.io.File;
import java.io.IOException;

/**
 * Created by nikol on 23/11/2017.
 */
public class NucleotideStream extends AbstractStream<Emission> {

    private FASTAReader fr;

    public NucleotideStream(int i) throws IOException {
        this(new FASTAReader(new File("data/genome"+i+".fa")));
    }

    public NucleotideStream(FASTAReader fr){
        this.fr = fr;
        try{
            fr.next();
        } catch(IOException e){}
    }

    @Override
    public Emission next() throws IOException {
        return Emission.getEmission(fr.next());
    }
}

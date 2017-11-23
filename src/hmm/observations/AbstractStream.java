package hmm.observations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikol on 22/11/2017.
 */
public abstract class AbstractStream<T> implements HMMStream<T> {


    public List<T> toList() throws IOException {
        List<T> list = new ArrayList<T>();

        try {
            while (true)
                list.add(next());
        } catch(EndOfStreamException e){}

        return list;
    }
}

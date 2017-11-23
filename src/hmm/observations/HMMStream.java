package hmm.observations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikol on 22/11/2017.
 */
public interface HMMStream<T> {

    T next() throws IOException ;


    class EndOfStreamException extends RuntimeException{
        public EndOfStreamException(String s){
            super(s);
        }
    }

}

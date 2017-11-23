package hmm.observations;

import java.io.IOException;

/**
 * Created by nikol on 22/11/2017.
 */
public class CompositionStream<T> extends AbstractStream<T> {

    private HMMStream<T>[] streams;
    int i = 0;

    public CompositionStream(HMMStream<T>... streams){
        this.streams = streams;
    }

    @Override
    public T next() throws IOException {

        try{
            T t = streams[i].next();
            if(t == null){
                i++;
                return next();
            }
            return t;
        } catch(RuntimeException e){
            if(++i<streams.length){
                System.out.println("Switching to stream "+(i+1));
                return streams[++i].next();}
            throw new EndOfStreamException("The composition stream ran out of substreams");
        }
    }
}

package hmm;

import java.io.Serializable;

/**
 * Created by nikol on 22/11/2017.
 */
public enum Emission implements Serializable{
    A, C, G, T;

    public static Emission getEmission(String s){
        s = s.trim();
        if(s.equals("A"))
            return A;
        if(s.equals("C"))
            return C;
        if(s.equals("G"))
            return G;
        if(s.equals("T"))
            return T;

        throw new NoSuchEmissionException("No such emission: "+s);
    }

    static class NoSuchEmissionException extends RuntimeException{

        public NoSuchEmissionException(String s){
            super(s);
        }

    }
}

package hmm;

import java.io.Serializable;

/**
 * Created by nikol on 22/11/2017.
 */
public enum Coding implements Serializable {
    CODING_LEFT_TO_RIGHT('C'),
    CODING_RIGHT_TO_LEFT('R'),
    NONCODING('N'), EMPTY('-');

    private char id;

    Coding(char id){this.id = id;}

    @Override
    public String toString(){
        return ""+id;
    }

    public static final Coding getCoding(String s){
        s = s.trim();
        if(s.equals("N"))
            return NONCODING;
        if(s.equals("R"))
            return CODING_RIGHT_TO_LEFT;
        if(s.equals("C"))
            return CODING_LEFT_TO_RIGHT;

        throw new NoSuchCodingException("No such coding: "+s);
    }

    static class NoSuchCodingException extends RuntimeException{

        public NoSuchCodingException(String s){
            super(s);
        }

    }
}

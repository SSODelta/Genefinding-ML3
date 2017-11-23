package hmm.etc;

import hmm.State;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by nikol on 22/11/2017.
 */
public class DecodingPrinter {

    public static final void print(State[] states, boolean lineNumbers){

        int l = 1;
        for(int i=0; i<states.length; i++){

            if(i==0 && lineNumbers)
                System.out.print("0. N");
            if(i % 60 == 59)
                System.out.print("\n"+(lineNumbers?l+++". ":""));

            if(states[i] != State.EMPTY)
                System.out.print(states[i].getCoding());
            else
                System.out.print("N");
        }
    }

    public static final void outputAsFASTA(State[] states, String dest) throws IOException {

        //;NC_002737.1 gene annotation Streptococcus pyogenes M1 GAS, complete genome
        //>true-ann1

        PrintWriter writer = new PrintWriter(dest, "UTF-8");
        writer.println(";Machine Learning Handin 3 - Gene sequencing");
        writer.println(">output");

        for(int i=0; i<states.length; i++){
            writer.print(
                    states[i] == State.EMPTY ?
                            "N" : states[i].getCoding()
            );
            if(i % 60 == 59)writer.println();
        }
        writer.close();

    }
}

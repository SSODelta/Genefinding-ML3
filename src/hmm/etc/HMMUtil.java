package hmm.etc;

import hmm.HMM;
import hmm.observations.AnnotatedStream;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

/**
 * Created by nikol on 22/11/2017.
 */
public class HMMUtil {

    public static final MathContext context = MathContext.DECIMAL64;

    private static final BigDecimal THRESHOLD = new BigDecimal("0.01");

    public static <T> void verifyMap(Map<T, BigDecimal> map){
        BigDecimal sum = BigDecimal.ZERO;
        for(Map.Entry<T, BigDecimal> e : map.entrySet()){
            sum = sum.add(e.getValue());
        }

        if(sum.subtract(BigDecimal.ONE).abs().compareTo(THRESHOLD) > 0)
            throw new ValidationException("Unable to validate map. The sum was "+sum+".");
    }

    public static void saveHMM(HMM hmm, String output) throws IOException {
        FileOutputStream fout = new FileOutputStream(output);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(hmm);
    }

    public static HMM readHMM(String input) throws IOException, ClassNotFoundException {
        FileInputStream streamIn = new FileInputStream(input);
        ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
        return (HMM) objectinputstream.readObject();
    }

    public static AnnotatedStream[] intsToStreams(int[] data) throws IOException {
        AnnotatedStream[] as = new AnnotatedStream[data.length];

        for(int i=0; i<data.length; i++)
            as[i] = new AnnotatedStream("data/genome"+(i+1)+".fa", "data/true-ann"+(i+1)+".fa");

        return as;
    }

    public static String repeat(String s, int n){
        if(n==1)
            return s;
        return s + repeat(s,n-1);
    }
}

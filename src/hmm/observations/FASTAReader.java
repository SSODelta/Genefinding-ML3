package hmm.observations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by nikol on 22/11/2017.
 */
public class FASTAReader extends AbstractStream<String> {

    private File file;
    private FileReader fileReader;
    private BufferedReader bufferedReader;

    private String currentLine;

    public FASTAReader(File f) throws IOException{

        fileReader = new FileReader(f);
        bufferedReader = new BufferedReader(fileReader);
    }

    public String next() throws IOException {
        if(currentLine == null || currentLine.length() == 0){
            currentLine = bufferedReader.readLine();

            if(currentLine==null) {
                close();
                throw new EndOfStreamException("The file contained no more data.");
            }
            if(currentLine.startsWith(";") || currentLine.startsWith(">")){
                currentLine = null;
                return next();
            }
        }

        String chr = currentLine.substring(0,1);
        currentLine = currentLine.substring(1);
        return chr;
    }

    private void close() throws IOException {
        fileReader.close();
    }


}

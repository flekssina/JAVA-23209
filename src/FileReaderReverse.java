import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;





public class FileReaderReverse implements FileHandler{
    private final List<String> lines = new ArrayList<>();

    @Override
    public void readFile(String fileName) throws IOException{
        lines.clear();
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while((line = br.readLine()) != null){
                lines.add(line);
            }
        }
        Collections.reverse(lines);
    }
    @Override
    public List<String> getLines() {
        return lines;
    }
}

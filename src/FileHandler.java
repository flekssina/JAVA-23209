import java.io.IOException;
import java.util.List;



public interface FileHandler {
    void readFile(String filePath) throws IOException;
    List<String> getLines();
}

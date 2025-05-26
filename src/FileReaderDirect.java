import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReaderDirect implements FileHandler {
    private final List<String> lines = new ArrayList<>();

    @Override
    public void readFile(String filePath) throws IOException {
        lines.clear(); // Очищаем перед загрузкой нового файла
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
    }

    @Override
    public List<String> getLines() {
        return lines;
    }
}



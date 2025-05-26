import java.util.ArrayList;
import java.util.List;

public class Composer {
    private final List<String> directLines;
    private final List<String> reverseLines;
    private static final int COLUMN_WIDTH = 50;

    public Composer(FileHandler directReader, FileHandler reverseReader) {
        this.directLines = splitLongLines(directReader.getLines());
        this.reverseLines = splitLongLines(reverseReader.getLines());
    }

    public void printTwoColumns() {
        int maxLines = Math.max(this.directLines.size(), this.reverseLines.size());

        for (int i = 0; i < maxLines; i++) {
            String left = i < this.directLines.size() ? this.directLines.get(i) : "";
            String right = i < this.reverseLines.size() ? this.reverseLines.get(i) : "";

            System.out.printf("%-" + COLUMN_WIDTH + "s  %s%n", left, right);
        }
    }

    private List<String> splitLongLines(List<String> lines) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            while (line.length() > COLUMN_WIDTH) {
                result.add(line.substring(0, COLUMN_WIDTH));
                line = line.substring(COLUMN_WIDTH);
            }
            result.add(line);
        }
        return result;
    }
}

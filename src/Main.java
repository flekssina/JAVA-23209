import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FileHandler directReader = new FileReaderDirect();
            FileHandler reverseReader = new FileReaderReverse();

            directReader.readFile("src/example.txt");
            reverseReader.readFile("src/example.txt");

            Composer composer = new Composer(directReader, reverseReader);
            composer.printTwoColumns();

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}

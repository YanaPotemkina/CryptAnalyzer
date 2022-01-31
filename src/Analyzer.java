import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;


public class Analyzer {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ .,!:;?-%0123456789";
    private static final String INPUT = "Введите полный путь к файлу:";
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
    private static final String START_MESSAGE = """
            Введите число для выбора режима программы:
            1. Зашифровать текст с помощью ключа (метод Цезаря).
            2. Расшифровать текст с помощью ключа (метод Цезаря).
            3. Расшифровать текст автоматическим перебором ключа (брут-форс).
            4. Выйти из программы.
            """;

    public static void main(String[] args) throws IOException {
        getStart();
    }

    private static void getStart() throws IOException {
        System.out.println(START_MESSAGE);
        String choice = READER.readLine();
        while (!choice.equals("4")) {
            switch (choice) {
                case "1" -> encrypt();
                case "2" -> decrypt();
                case "3" -> brutForce();
                default -> System.out.println("Неккоректный ввод данных. Введите число от 1 до 4");
            }
            System.out.println(START_MESSAGE);
            choice = READER.readLine();
        }
        System.out.println("Выход из программы");
    }

    private static void encrypt() throws IOException {
        System.out.println(INPUT);
        String filePath = READER.readLine();
        String userText = getFile(filePath);
        System.out.println("Введите ключ:");
        int key = Integer.parseInt(READER.readLine());
        writeNewTextToFile(encryptWithKey(userText, key), filePath, "_encrypted");
    }

    private static StringBuilder encryptWithKey(String userText, int key) {
        StringBuilder encryptText = new StringBuilder();
        for (int i = 0; i < userText.length(); i++) {
            char charText = userText.charAt(i);
            int charIndex = ALPHABET.indexOf(charText);
            if (charIndex == -1) {
                encryptText.append(charText);
            } else {
                int newCharIndex = (charIndex + key) % ALPHABET.length();
                char encryptChar = ALPHABET.charAt(newCharIndex);
                encryptText.append(encryptChar);
            }
        }
        return encryptText;
    }

    private static void decrypt() throws IOException {
        System.out.println(INPUT);
        String filePath = READER.readLine();
        String userText = getFile(filePath);
        System.out.println("Введите ключ:");
        int key = Integer.parseInt(READER.readLine());
        writeNewTextToFile(decryptWithKey(userText, key), filePath, "_decrypted");
    }

    private static StringBuilder decryptWithKey(String userText, int key) {
        StringBuilder decryptText = new StringBuilder();
        for (int i = 0; i < userText.length(); i++) {
            char charText = userText.charAt(i);
            int charIndex = ALPHABET.indexOf(charText);
            if (charIndex == -1) {
                decryptText.append(charText);
            } else {
                int newCharIndex = Math.abs((charIndex - key) % ALPHABET.length());
                char encryptChar = ALPHABET.charAt(newCharIndex);
                decryptText.append(encryptChar);
            }
        }
        return decryptText;
    }

    private static void brutForce() throws IOException {
        System.out.println(INPUT);
        String filePath = READER.readLine();
        String userText = getFile(filePath);
        for (int key = 0; key < ALPHABET.length(); key++) {
            StringBuilder decryptedText = decryptWithKey(userText, key);
            boolean isValid = isValidText(decryptedText);
            if (isValid) {
                writeNewTextToFile(decryptedText, filePath, "_brut");
                break;
            }
        }
    }

    private static boolean isValidText(StringBuilder decryptedText) {
        char[] chars = decryptedText.toString().toCharArray();
        int points = 0;
        int comma = 0;
        int exclamation = 0;
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '.' && chars[i + 1] == ' ') {
                points++;
            } else if (chars[i] == ',' && chars[i + 1] == ' ') {
                comma++;
            } else if (chars[i] == '!' && chars[i + 1] == ' ') {
                exclamation++;
            } else if ((chars[i] == ' ' && chars[i + 1] == 'Ъ') || (chars[i] == ' ' && chars[i + 1] == 'Ь')) {
                return false;
            }
            if (points > 20 && comma > 30 && exclamation > 10) {
                return true;
            }
        }
        return false;
    }

    private static String getFile(String filePath) {
        Path path = Path.of(filePath);
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла.\n");
        }
        return filePath;
    }

    private static void writeNewTextToFile(StringBuilder encryptText, String filePath, String suffix) {
        try {
            int dotIndex = filePath.lastIndexOf(".");
            String fileBeforeDot = filePath.substring(0, dotIndex);
            String fileAfterDot = filePath.substring(dotIndex);
            String newFile = fileBeforeDot + suffix + fileAfterDot;
            Files.writeString(Path.of(newFile), encryptText);
            System.out.println("Программа выполнена успешна!\nПуть к новому файлу: " + fileBeforeDot + suffix + fileAfterDot + "\n");
        } catch (IOException e) {
            System.out.println("Ошибка записи файла.\n");
        }
    }
}



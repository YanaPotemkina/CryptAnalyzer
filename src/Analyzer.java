import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;


public class Analyzer {

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ .,!:;?%0123456789";
    public static final ArrayList<Character> CRYPTO_CHARACTERS = new ArrayList<>();
    public static final ArrayList<Character> CRYPTO_CHARACTERS_WITH_KEY = new ArrayList<>();
    private static final String INPUT = "Введите полный путь к файлу:";
    public static final ArrayList<Character> NEW_CRYPTO_TEXT = new ArrayList<>();
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
        createAlphabet();
        System.out.println(START_MESSAGE);
        String choice = READER.readLine();
        while (!choice.equals("4")) {
            switch (choice) {
                case "1" -> cryptoTextWithKey();
                case "2" -> unCryptoTextWithKey();
                case "3" -> brutForce();
                default -> System.out.println("Неккоректный ввод данных. Введите число от 1 до 4.\n");
            }
            System.out.println(START_MESSAGE);
            choice = READER.readLine();
        }
        System.out.println("Выход из программы");
    }

    public static void cryptoTextWithKey() throws IOException {
        createCryptoAlphabet();
        System.out.println(INPUT);
        String filePath = READER.readLine();
        System.out.println("Введите ключ:");
        int key = Integer.parseInt(READER.readLine());
        Collections.rotate(CRYPTO_CHARACTERS_WITH_KEY, -key);
        StringBuilder cryptText = crypt(filePath);
        writeNewTextToFile(cryptText, filePath, "_encrypted");
        CRYPTO_CHARACTERS_WITH_KEY.clear();
    }


    public static void unCryptoTextWithKey() throws IOException {
        createCryptoAlphabet();
        System.out.println(INPUT);
        String filePath = READER.readLine();
        System.out.println("Введите ключ:");
        int key = Integer.parseInt(READER.readLine());
        Collections.rotate(CRYPTO_CHARACTERS_WITH_KEY, key);
        StringBuilder cryptText = crypt(filePath);
        writeNewTextToFile(cryptText, filePath, "_decrypted");
        CRYPTO_CHARACTERS_WITH_KEY.clear();
    }


    public static StringBuilder crypt(String path) throws IOException {
        StringBuilder encryptText = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    int cryptoIndex = CRYPTO_CHARACTERS.indexOf(aChar);
                    if (cryptoIndex == -1) {
                        encryptText.append(aChar);
                    } else {
                        char messageIndex = CRYPTO_CHARACTERS_WITH_KEY.get(cryptoIndex);
                        encryptText.append(messageIndex);
                    }
                }
                encryptText.append("\n");
            }
        }
        return encryptText;
    }

    private static void brutForce() throws IOException {
        createCryptoAlphabet();
        System.out.println(INPUT);
        String filePath = READER.readLine();
        for (int key = 0; key < ALPHABET.length(); key++) {
            Collections.rotate(CRYPTO_CHARACTERS_WITH_KEY, 1);
            StringBuilder decryptedText = crypt(filePath);
            boolean isValid = isValidText(decryptedText);
            if (isValid) {
                writeNewTextToFile(decryptedText, filePath, "_brut");
                break;
            }
        }
        CRYPTO_CHARACTERS_WITH_KEY.clear();
    }

    private static boolean isValidText(StringBuilder decryptedText) {
        String[] strings = decryptedText.toString().split(" ");
        for (String string : strings) {
            if (string.length() > 40) {
                return false;
            }
        }
        return true;
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

    public static void createAlphabet() {
        for (int i = 0; i < ALPHABET.length(); i++) {
            CRYPTO_CHARACTERS.add(i, ALPHABET.charAt(i));
        }
    }

    public static void createCryptoAlphabet() {
        for (int i = 0; i < CRYPTO_CHARACTERS.size(); i++) {
            CRYPTO_CHARACTERS_WITH_KEY.add(i, CRYPTO_CHARACTERS.get(i));
        }
    }
}



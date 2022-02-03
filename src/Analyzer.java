import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Analyzer {
    public static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ .,!:;?%0123456789";
    public static final ArrayList<Character> ALPHABET = new ArrayList<>();
    public static final ArrayList<Character> CRYPTO_ALPHABET = new ArrayList<>();
    private static final String INPUT = "Введите полный путь к файлу:";
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
    private static final String START_MESSAGE = """
            Введите число для выбора режима программы:
            1. Зашифровать текст с помощью ключа (метод Цезаря).
            2. Расшифровать текст с помощью ключа (метод Цезаря).
            3. Расшифровать текст автоматическим перебором ключа (брут-форс).
            4. Расшифровать текст с помощью статистического анализа.
            5. Выйти из программы.
            """;

    public static void main(String[] args) throws IOException {
        getStart();
    }

    private static void getStart() throws IOException {
        createAlphabet();
        System.out.println(START_MESSAGE);
        String choice = READER.readLine();
        while (!choice.equals("5")) {
            switch (choice) {
                case "1" -> encryptTextWithKey();
                case "2" -> decryptTextWithKey();
                case "3" -> brutForce();
                case "4" -> statAnalysis();
                default -> System.out.println("Неккоректный ввод данных. Введите число от 1 до 5.\n");
            }
            System.out.println(START_MESSAGE);
            choice = READER.readLine();
        }
        System.out.println("Выход из программы");
    }

    private static void statAnalysis() throws IOException {
        System.out.println(INPUT);
        String filePath = READER.readLine();
        StringBuilder encryptText = getText(filePath);
        System.out.println("Для получения статистики нужен незашифрованный текст (желательно схожей стилистики). " + INPUT);
        String fileStatisticPath = READER.readLine();
        StringBuilder statisticText = getText(fileStatisticPath);
        LinkedHashMap<Character, Integer> userAlphabet = sortMap(getFileAlphabet(encryptText));
        LinkedHashMap<Character, Integer> statisticAlphabet = sortMap(getFileAlphabet(statisticText));
        writeNewTextToFile(decryptByStatAnalysis(userAlphabet, statisticAlphabet, encryptText), filePath, "_statAnalysis");
    }

    private static StringBuilder decryptByStatAnalysis(LinkedHashMap<Character, Integer> userAlphabet, LinkedHashMap<Character, Integer> statisticAlphabet, StringBuilder encryptText) {
        StringBuilder stringBuilder = new StringBuilder();
        LinkedHashMap<Character, Character> charToChar = new LinkedHashMap<>();
        LinkedList<Character> decList = new LinkedList<>(userAlphabet.keySet());
        LinkedList<Character> statList = new LinkedList<>(statisticAlphabet.keySet());

        int length = Math.min(userAlphabet.keySet().size(), statisticAlphabet.keySet().size());

        for (int i = 0; i < length; i++) {
            charToChar.put(decList.get(i), statList.get(i));
        }

        for (int i = 0; i < encryptText.length(); i++) {
            stringBuilder.append(charToChar.get(encryptText.charAt(i)));
        }
        return stringBuilder;
    }

    private static LinkedHashMap<Character, Integer> sortMap(LinkedHashMap<Character, Integer> map) {
        LinkedHashMap<Character, Integer> result = new LinkedHashMap<>();
        List<Integer> values = new ArrayList<>(map.values());
        Collections.sort(values);
        for (int value : values) {
            for (Character character : map.keySet()) {
                if (value == map.get(character)) {
                    result.put(character, value);
                    map.remove(character);
                    break;
                }
            }
        }
        return result;
    }

    private static LinkedHashMap<Character, Integer> getFileAlphabet(StringBuilder encryptText) {
        Map<Integer, Character> map = new HashMap<>();
        LinkedHashMap<Character, Integer> alphabet = new LinkedHashMap<>();
        for (int i = 0; i < encryptText.length(); i++) {
            map.put(i, encryptText.charAt(i));
        }
        for (int i = 0; i < encryptText.length(); i++) {
            alphabet.put(encryptText.charAt(i), countChar(encryptText.charAt(i), map));
        }
        return alphabet;
    }

    private static Integer countChar(char charAt, Map<Integer, Character> map) {
        int count = 0;
        for (char value : map.values()) {
            if (value == charAt) {
                ++count;
            }
        }
        return count;
    }

    public static void encryptTextWithKey() throws IOException {
        createCryptoAlphabet();
        System.out.println(INPUT);
        String filePath = READER.readLine();
        System.out.println("Введите ключ:");
        int key = Integer.parseInt(READER.readLine());
        Collections.rotate(CRYPTO_ALPHABET, -key);
        StringBuilder cryptText = writeNewText(filePath);
        writeNewTextToFile(cryptText, filePath, "_encrypted");
        CRYPTO_ALPHABET.clear();
    }

    public static void decryptTextWithKey() throws IOException {
        createCryptoAlphabet();
        System.out.println(INPUT);
        String filePath = READER.readLine();
        System.out.println("Введите ключ:");
        int key = Integer.parseInt(READER.readLine());
        Collections.rotate(CRYPTO_ALPHABET, key);
        StringBuilder cryptText = writeNewText(filePath);
        writeNewTextToFile(cryptText, filePath, "_decrypted");
        CRYPTO_ALPHABET.clear();
    }

    public static StringBuilder getText(String path) throws IOException {
        StringBuilder fileText = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    fileText.append(aChar);
                }
                fileText.append("\n");
            }
        }
        return fileText;
    }

    public static StringBuilder writeNewText(String path) throws IOException {
        StringBuilder encryptText = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    int cryptoIndex = ALPHABET.indexOf(aChar);
                    if (cryptoIndex == -1) {
                        encryptText.append(aChar);
                    } else {
                        char messageIndex = CRYPTO_ALPHABET.get(cryptoIndex);
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
        for (int key = 0; key < CHARS.length(); key++) {
            Collections.rotate(CRYPTO_ALPHABET, 1);
            StringBuilder decryptedText = writeNewText(filePath);
            boolean isValid = isValidText(decryptedText);
            if (isValid) {
                writeNewTextToFile(decryptedText, filePath, "_brutForce");
                break;
            }
        }
        CRYPTO_ALPHABET.clear();
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
            System.out.println("\nПрограмма выполнена успешна!\nПуть к новому файлу: " + fileBeforeDot + suffix + fileAfterDot + "\n");
        } catch (IOException e) {
            System.out.println("Ошибка записи файла.\n");
        }
    }

    public static void createAlphabet() {
        for (int i = 0; i < CHARS.length(); i++) {
            ALPHABET.add(i, CHARS.charAt(i));
        }
    }

    public static void createCryptoAlphabet() {
        for (int i = 0; i < ALPHABET.size(); i++) {
            CRYPTO_ALPHABET.add(i, ALPHABET.get(i));
        }
    }
}



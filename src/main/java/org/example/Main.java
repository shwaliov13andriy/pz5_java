package practice5;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class InvalidReadingException extends Exception {
    public InvalidReadingException(String message) {
        super(message);
    }
    public InvalidReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ДОСЛІД 1: Винятки та блоки try-catch-finally ===");

        try {
            FileReader reader = new FileReader("non_existent.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Перехоплено: Файл не знайдено.");
        } finally {
            System.out.println("Блок finally виконався (навіть після помилки)!");
        }


        System.out.println("\n=== ДОСЛІД 2: Власний виняток та стектрейси ===");

        String badSensorData = "abc";

        try {
            int value = Integer.parseInt(badSensorData);
        } catch (NumberFormatException e) {
            try {
                throw new InvalidReadingException(e.getMessage());
            } catch (InvalidReadingException ex) {
                System.out.println("--- Стектрейс БЕЗ причини: ---");
                ex.printStackTrace(System.out);
            }
        }

        System.out.println();

        try {
            int value = Integer.parseInt(badSensorData);
        } catch (NumberFormatException e) {
            try {
                throw new InvalidReadingException("Некоректне показання датчика: " + badSensorData, e);
            } catch (InvalidReadingException ex) {
                System.out.println("--- Стектрейс ЗІ збереженою причиною (Caused by): ---");
                ex.printStackTrace(System.out);
            }
        }

        System.out.println("\n=== ДОСЛІД 3: Файли, потоки та кодування ===");

        String fileName = "sensor_data.txt";
        String data = "Показання датчика: 25.5°C";
        try {
            BufferedWriter badWriter = new BufferedWriter(new FileWriter("bad_" + fileName));
            badWriter.write(data);
            System.out.println("Записали без close(). Перевірте файл bad_sensor_data.txt — він буде порожній, бо буфер не очистився.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter goodWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            goodWriter.write(data);
            System.out.println("Записали з try-with-resources. Файл sensor_data.txt успішно збережено.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nЧитання явно у UTF-8:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            System.out.println("Результат: " + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nЧитання як windows-1251:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("windows-1251")))) {
            System.out.println("Результат: " + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
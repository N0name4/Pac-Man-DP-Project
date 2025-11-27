package game;
import java.io.*;
import java.util.Properties;

public class SkinSelector {
    private static Properties props = new Properties();

    static {
        try (InputStream input = new FileInputStream("/config.properties")) {
            props.load(input);
        } catch (IOException e) {
            System.out.println("config.properties 파일을 찾을 수 없습니다.");
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        try (OutputStream output = new FileOutputStream("/config.properties")) {
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

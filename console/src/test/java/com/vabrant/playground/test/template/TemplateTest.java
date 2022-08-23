package com.vabrant.playground.test.template;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateTest {

    @Test
    public void readTemplateTest() {
        try (InputStream in = Files.newInputStream(new File("src/test/java/resources/template/JavaTemplate").toPath())) {
            byte[] buffer = in.readAllBytes();
            String txt = new String(buffer, "UTF-8");
//            System.out.println(txt);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Test
    public void readTemplateTest2() {
        try(BufferedReader r = Files.newBufferedReader(new File("src/test/java/resources/template/JavaTemplate").toPath())) {
            String line = r.readLine();

            line = line.replace("%package%", "com.vabrant.hello");
            Matcher m;
            Pattern p;


            System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Test
    public void copyAndReplaceTempleteTest() {
        try (InputStream in = Files.newInputStream(new File("src/test/java/resources/template/JavaTemplate").toPath())) {
            byte[] buffer = in.readAllBytes();
            String txt = new String(buffer, "UTF-8");


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}

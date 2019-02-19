package sample;

import java.io.*;

public class StringSplit {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\ASUS\\Desktop\\test.txt");
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(file));
            br.write("asdf@gmail.com<<qwer");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str1 = br.readLine();
            String[] split = str1.split("<<");
            System.out.println(split[0]);
            System.out.println(str1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

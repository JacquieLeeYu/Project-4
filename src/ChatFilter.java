import java.io.*;

/**
 * CS18000 Project 4 - Simple Server
 *
 *
 *
 * @author Jacquie Yu, Siddarth Pillai
 *
 *
 */

public class ChatFilter {

    public ChatFilter(String badWordsFileName) {
        File file = new File(badWordsFileName);
        try {FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        String text = "";

        while (line != null) {
            text = text + line + "\n";
            line = br.readLine();
        }
        String[] badWords = text.split("\n");



        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");

        } catch (IOException e) {
            System.out.println("IOException");

        }

    }

    public String filter(String msg) {
        return msg;
    }
    public static void main(String[] args) {
        new ChatFilter("/Users/siddharthpillai/Desktop/filterText.txt");
    }
}

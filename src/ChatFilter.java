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
    private String badWordsFileName;

    public ChatFilter(String badWordsFileName) {
        this.badWordsFileName = badWordsFileName;
    }
    public String filter(String msg) {
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
                String[] filteredWords = new String[badWords.length];
                for (int i = 0; i < badWords.length ; i++) {
                    String word = "";
                    for (int j = 0; j < badWords[i].length(); j++) {
                        word += '*';
                    }
                    filteredWords[i] = word;
                }

                for (int i = 0; i < badWords.length ; i++) {
                    msg = msg.replace(badWords[i],filteredWords[i]);
                }
                System.out.println(msg);



            } catch (FileNotFoundException e) {
                System.out.println("File Not Found");
                return null;

            } catch (IOException e) {
                System.out.println("IOException");
                return null;
            }


        return msg;
    }
    public static void main(String[] args) {
        ChatFilter chat =  new ChatFilter("/Users/siddharthpillai/Desktop/filterText.txt");
        chat.filter("I go to IU. It is a bad school");
    }
}

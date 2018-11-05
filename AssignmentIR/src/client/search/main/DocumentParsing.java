package client.search.main;

import java.io.*;
import java.util.ArrayList;


public class DocumentParsing {

    String INPUT_FILE_NAME = "MED.txt";
    String INPUT_FOLDER_LOC = ".." + File.separator + "input" + File.separator;
    String DOCUMENT_FOLDER_LOC = ".." + File.separator + "input" + File.separator + "Doc" + File.separator;

    public  static ArrayList<String> stopwordList = new ArrayList<String>();

    public void stopWordLoading(){

        File file = new File(INPUT_FOLDER_LOC + "StopWord.txt");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
               stopwordList.add(st) ;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void documentSeparation() {

        File file = new File(INPUT_FOLDER_LOC + INPUT_FILE_NAME);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            int linenumber = 0;
            while ((st = br.readLine()) != null) {

                if (st.contains(".I")) {
                    String[] spiltWords = st.split(".I");
                    String docNumber = (spiltWords[1]);
                    writingFile(linenumber, docNumber);
                    Stemmer.SteemerMain(DOCUMENT_FOLDER_LOC,docNumber+".txt");

                }
                linenumber++;


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writingFile(int lineNumber, String docName) {

        ArrayList <String> wordList = new ArrayList<String>();
        File file = new File(INPUT_FOLDER_LOC + INPUT_FILE_NAME);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            int i = 0;
            while ((st = br.readLine()) != null) {

                if(i>lineNumber){
                    StringBuilder sb = new StringBuilder();
                    String[] words = st.split(" ");
                    if (st.contains(".I"))
                        break;
                    for(String word:words){
                        if(!stopwordList.contains(word.toLowerCase())){
                            sb.append(word);
                            sb.append(" ");
                        }
                    }
                    wordList.add(sb.toString());
                    wordList.add("\n");
                }
            i++;
            }
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(DOCUMENT_FOLDER_LOC +docName+".txt"), "utf-8"))) {
                    for (String word: wordList)
                    writer.write(word);
                }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}



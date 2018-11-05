package client.search.main;

import sun.util.locale.provider.AvailableLanguageTags;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {
    String STEM_DOC_LOC =  ".."+ File.separator+"input"+File.separator+"StemDoc"+File.separator;
    String INDEX_LOC = ".." + File.separator + "Index" + File.separator;
    String INPUT_FOLDER_LOC = ".." + File.separator + "input" + File.separator;
    String RESULT_FILE_LOC = ".." + File.separator + "Result" + File.separator;


    Map<String,HashMap<String,Integer>> index = new HashMap<String,HashMap<String, Integer>>();
    int NUMBER_OF_DOCUMENTS;
    int NUMBER_OF_WORDS;
    int NUMBER_OF_UNIQUE_WORDS;
    int NUMBER_OF_POSTING_ENTRY;
    long ELAPSED_TIME;
    int MAX_POSTING_NUMBER;
    int MIN_POSTING_NUMBER;
    double AVG_POSTING_NUMBER;

    public void buildingIndex(){

        List<String> files = Util.getAbsoluteFilePathsFromFolder(STEM_DOC_LOC);

        NUMBER_OF_DOCUMENTS = 0;
        for(String file:files){

            NUMBER_OF_DOCUMENTS++;
            BufferedReader br = null;
            Path filePath = Paths.get(file);
            String fileName = filePath.getFileName().toString();
            try {
                br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null) {

                    if(index.containsKey(st)){
                        HashMap<String,Integer> previousRecordMap = new HashMap<String, Integer>();
                        previousRecordMap = index.get(st);


                        if(previousRecordMap.containsKey(fileName)){
                            int previousFreq = previousRecordMap.get(fileName);
                            previousFreq++;
                            previousRecordMap.put(fileName,previousFreq);
                            index.put(st,previousRecordMap);
                        }
                        else{
                            previousRecordMap.put(fileName,1);
                            index.put(st,previousRecordMap);
                        }
                    }
                    else{
                        HashMap<String,Integer> newRecord = new HashMap<String, Integer>();
                        newRecord.put(fileName,1);
                        index.put(st,newRecord);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        setDocumentNumber(NUMBER_OF_DOCUMENTS);
        setUniqueWordNumber(index.size());
        setPostingEntry(index.size());
        postingListInfo();

    }

    public void writeIndexFile() {

        HashMap<String,Integer> fileWordFreq = new HashMap<String, Integer>();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(INDEX_LOC + "Index.txt"), "utf-8"))) {


          for(Map.Entry<String,HashMap<String,Integer>> entry: index.entrySet()){
              writer.write(entry.getKey());
              writer.write("|.|");
              fileWordFreq = entry.getValue();


               for(String fileName: fileWordFreq.keySet()){
                   writer.write(fileName);
                   writer.write("|.|");
                   writer.write(String.valueOf(fileWordFreq.get(fileName)));
                   writer.write("|.|");
               }

               writer.write("\n");
          }



        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }


    public  int  getWordNumber(){

        NUMBER_OF_WORDS = 0;
        HashMap<String,Integer> fileWordFreq = new HashMap<String, Integer>();

         for(Map.Entry<String,HashMap<String,Integer>> entry: index.entrySet()){

             fileWordFreq = entry.getValue();

             for(String term: fileWordFreq.keySet()){
                 int freq = fileWordFreq.get(term);
                 NUMBER_OF_WORDS += freq;
             }
         }

         return NUMBER_OF_WORDS;
}



    public  String getFileSize(File file) {
        String modifiedFileSize = null;
        double fileSize = 0.0;
        if (file.isFile()) {
            fileSize = (double) file.length();//in Bytes

            if (fileSize < 1024) {
                modifiedFileSize = String.valueOf(fileSize).concat("B");
            } else if (fileSize > 1024 && fileSize < (1024 * 1024)) {
                modifiedFileSize = String.valueOf(Math.round((fileSize / 1024 * 100.0)) / 100.0).concat("KB");
            } else {
                modifiedFileSize = String.valueOf(Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0).concat("MB");
            }
        } else {
            modifiedFileSize = "Unknown";
        }

        return modifiedFileSize;
    }


    public String getRelativeByteSize(){
        File indexFile = new File(INDEX_LOC+"Index.txt");
        File inputFile = new File(INPUT_FOLDER_LOC+ "MED.txt");

        double indexSize;
        double inputSize;
        double gap;
        double percentage;
        String returnVal;

        indexSize=indexFile.length();
        inputSize = inputFile.length();

        gap = inputSize-indexSize;
        percentage = gap/inputSize;
        returnVal = Double.toString(percentage*100);

        return returnVal;
    }


    public void  setDocumentNumber(int docNumber){
        this.NUMBER_OF_DOCUMENTS = docNumber;
    }

    public int getDocumentNumber(){
        return NUMBER_OF_DOCUMENTS;
    }

    public void setWordNumber(int wordNumber){
        this.NUMBER_OF_WORDS = wordNumber;
    }

    public int getUniqueWordNumber(){
        return NUMBER_OF_UNIQUE_WORDS;
    }

    public void setUniqueWordNumber(int uniqueWordNumber){
        this.NUMBER_OF_UNIQUE_WORDS = uniqueWordNumber;
    }

    public  void setElapsedTime(long elapsedTime){
        this.ELAPSED_TIME = elapsedTime;
    }
    public String  getElapsedTime(){
        String time = Long.toString(ELAPSED_TIME);
        return time;
    }

    public  void setPostingEntry(int postingEntry){
        this.NUMBER_OF_POSTING_ENTRY = postingEntry;
    }

    public int getPostingEntry(){
        return NUMBER_OF_POSTING_ENTRY;
    }

    public void postingListInfo(){
        HashMap<String,Integer> fileWordFreq = new HashMap<String, Integer>();

        MAX_POSTING_NUMBER=0;
        MIN_POSTING_NUMBER = 1;
        AVG_POSTING_NUMBER =0;
        for(Map.Entry<String,HashMap<String,Integer>> entry: index.entrySet()){

            fileWordFreq = entry.getValue();
            int localPostingSize = fileWordFreq.size();

            if(localPostingSize>MAX_POSTING_NUMBER) {
                MAX_POSTING_NUMBER = localPostingSize;
            }
            if(localPostingSize<MIN_POSTING_NUMBER){
                MIN_POSTING_NUMBER = localPostingSize;
            }
            AVG_POSTING_NUMBER +=localPostingSize;
        }

        AVG_POSTING_NUMBER/=index.size();

        setMaxPosting(MAX_POSTING_NUMBER);
        setMinPosting(MIN_POSTING_NUMBER);
        setAvgPosting(AVG_POSTING_NUMBER);



    }

   public  void setMaxPosting(int maxPosting){
        this.MAX_POSTING_NUMBER = maxPosting;
   }

   public int getMaxPosting(){
        return MAX_POSTING_NUMBER;
   }

   public void setMinPosting(int minPosting){
        this.MIN_POSTING_NUMBER = minPosting;
   }

   public int getMinPosting(){
        return MIN_POSTING_NUMBER;
   }

   public void setAvgPosting(double avgPosting){
        this.AVG_POSTING_NUMBER = avgPosting;
   }

   public double getAvgPosting(){
        return  AVG_POSTING_NUMBER;
   }

    public void printResult(){

        Index indexObj = new Index();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(RESULT_FILE_LOC + "Result.txt"), "utf-8"))) {

            writer.write("Corpus Statistics:");
            writer.write("\n");
            writer.write("==================");
            writer.write("\n");
            writer.write("Number Of Documents are in the Collection");
            writer.write("\n");
            writer.write(String.valueOf(getDocumentNumber()));
            writer.write("\n");
            writer.write("Number of words are in the Collection");
            writer.write("\n");
            writer.write(String.valueOf(getWordNumber()));
            writer.write("\n");
            writer.write("Number of Unique Words In the indexing vocabulary of the colletion");
            writer.write("\n");
            writer.write(String.valueOf(getUniqueWordNumber()));
            writer.write("\n");
            writer.write("Number of Posting created for Index");
            writer.write("\n");
            writer.write(String.valueOf(getPostingEntry()));
            writer.write("\n");
            writer.write("Longest Posting  Length: " + getMaxPosting());
            writer.write(" Shortest Posting Length: " + getMinPosting());
            writer.write(" Avg Posting Length:" + getAvgPosting());
            writer.write("\n");



            writer.write("Inverted Index Statistics:");
            writer.write("\n");
            writer.write("==========================");
            writer.write("\n");
            writer.write("Time to take to Index the Collection:");
            writer.write("\n");
            writer.write(getElapsedTime() +"  MilliSeconds");
            writer.write("\n");
            writer.write("Disk Space consumed to store the Inverted Index:");
            writer.write(getFileSize(new File(INDEX_LOC + "Index.txt")));
            writer.write(" DataStructure(HashMap) Memory to store the Inverted Index:");
            writer.write(getFileSize(new File(INDEX_LOC + "Index.txt")));
            writer.write("\n");
            writer.write("Index Relative Size compare to the size of DataSet");
            writer.write("\n");
            writer.write(getRelativeByteSize()+" %");



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

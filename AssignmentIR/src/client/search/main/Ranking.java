package client.search.main;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ranking {



    HashMap<String,HashMap<String,Float>> documentScoreAgainsTermMap = new HashMap<String,HashMap<String, Float>>();

    String INPUT_FOLDER_LOC = ".." + File.separator + "input" + File.separator;
    String INDEX_LOC = ".." + File.separator + "Index" + File.separator;




    public  void queryTFIDF(){
        File queryfile = new File(INPUT_FOLDER_LOC + "ParsedQuery.txt");
        File indexFile = new File(INDEX_LOC+"IDF.txt");


        BufferedReader brQuery = null;
        BufferedReader brIndex = null;


        try {
            brQuery = new BufferedReader(new FileReader(queryfile));
            brIndex = new BufferedReader(new FileReader(indexFile));


            String stQuery,stIndex;

            while ((stQuery = brQuery.readLine()) != null) {

                String [] stQueryArr = stQuery.split("\\s+");
                float queryWeight = stQuery.length()-1;
                queryWeight = 1/queryWeight;
                for(String queryTerm: stQueryArr){
                    if(queryTerm.length()==0){
                        continue;
                    }
                    while ((stIndex = brIndex.readLine())!=null){
                        String [] stIndexArr = stIndex.split("\\|.\\|");
                        if(stIndexArr[0].equals(queryTerm)){
                            List<String> associatedFileList = new ArrayList<String>();
                            for(int i=1;i<stIndexArr.length;i++){
                                if(i%2!=0){
                                    associatedFileList.add(stIndexArr[i]);
                                }
                            }
                            docScoreAgainstQuery(stIndexArr[0],queryWeight,associatedFileList);
                        }

                    }
                }

         /*       for(String printResultforOneSearchQry: documentScoreAgainsTermMap.keySet()){
                    System.out.print( "Term:"+ printResultforOneSearchQry+  " DocumentScore" + documentScoreAgainsTermMap.get(printResultforOneSearchQry));
  double hashmap need to print result

                }
*/

            }
            brQuery.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public  void docScoreAgainstQuery(String term,Float queryWeight,List<String> assocaitedFileList){
       File docScoreFile = new File(INDEX_LOC + "DocumentScore.txt");
       BufferedReader brDocScore = null;
       try {
           brDocScore = new BufferedReader(new FileReader(docScoreFile));
           HashMap<String,Float> documentScoreMap = new HashMap<String, Float>();
           for(String fileName: assocaitedFileList){
               String stDocScore;
               while ((stDocScore=brDocScore.readLine())!=null){
                   String [] termArray = stDocScore.split("\\|.\\|");
                    for (int i=0;i<termArray.length;i++){
                        if(termArray[i].equals(term)){
                            float docScoreAgainstTerm = Float.valueOf(termArray[i+2]);
                            float tfScoreOFDoc = queryWeight* docScoreAgainstTerm;
                            documentScoreMap.put(termArray[i+1],tfScoreOFDoc);
                            documentScoreAgainsTermMap.put(term,documentScoreMap);
                            break;
                        }
                    }

               }
           }



       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }

   }



}

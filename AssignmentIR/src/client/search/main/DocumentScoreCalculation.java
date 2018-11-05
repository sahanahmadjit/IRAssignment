package client.search.main;

import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DocumentScoreCalculation {

    String INPUT_FOLDER_LOC = ".." + File.separator + "input" + File.separator;
    String INDEX_LOC = ".." + File.separator + "Index" + File.separator;
    String STEM_DOC_LOC =  ".."+ File.separator+"input"+File.separator+"StemDoc"+File.separator;
    String DOCUMENT_FOLDER_LOC = ".." + File.separator + "input" + File.separator + "Doc" + File.separator;


    HashMap<String,HashMap<String,Double>> tfMap = new HashMap<String, HashMap<String, Double>>();
    HashMap<String,Double> idfMap = new HashMap<String, Double>();
    HashMap<String,HashMap<String,Double>> tfIDFMap = new HashMap<String, HashMap<String, Double>>();
    List<String> queryList = new ArrayList<String>();
    HashMap <Integer,HashMap<String,Double>> queryBucketMap = new HashMap<Integer, HashMap<String, Double>>();
    ArrayList <Float> cosssineScoreList = new ArrayList<>();
    ArrayList <Float> jaccardScoreList = new ArrayList<>();
    long startTime, stopTime,elapsedTime;
    String time;

    public  void  tfValueCalCulation(){
        List<String> files = Util.getAbsoluteFilePathsFromFolder(STEM_DOC_LOC);
        for(String file:files){
            BufferedReader br = null;
            Path filePath = Paths.get(file);
            String fileName = filePath.getFileName().toString();
            HashMap<String,Double> termFeqMap = new HashMap<String, Double>();
            double MAX_FREQ = 0;
            try {
                br = new BufferedReader(new FileReader(file));
                String st;
                int numberOfTerm = 0;

                while ((st = br.readLine()) != null) {

                    if(!termFeqMap.containsKey(st)){
                        termFeqMap.put(st,Double.valueOf(1));
                    }
                    else {
                        double previousVal = termFeqMap.get(st);
                        previousVal += 1;
                        termFeqMap.put(st, previousVal);

                        if (previousVal > MAX_FREQ) {
                            MAX_FREQ = previousVal;
                        }
                    }
                }

                for (String term: termFeqMap.keySet()){
                    double normalizedVal = termFeqMap.get(term);
                    normalizedVal= normalizedVal-0;
                    double maxMinDiff = MAX_FREQ-1;
                    double tfScore = normalizedVal/maxMinDiff;
                    termFeqMap.put(term,tfScore);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            tfMap.put(fileName,termFeqMap);

        }

    }

    public void writeTFValue() {

        HashMap<String,Double> termDocScoreMap = new HashMap<String, Double>();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(INDEX_LOC + "TF.txt"), "utf-8"))) {

            for(Map.Entry<String,HashMap<String,Double>> entry: tfMap.entrySet()){
                writer.write(entry.getKey());
                writer.write("|.|");
                termDocScoreMap = entry.getValue();


                for(String termDocScore: termDocScoreMap.keySet()){
                    writer.write(termDocScore);
                    writer.write("|.|");
                    writer.write(String.valueOf(termDocScoreMap.get(termDocScore)));
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


    public  void idfCalculation(){
        BufferedReader br = null;
        HashMap<String,Float> termFeqMap = new HashMap<String, Float>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(INDEX_LOC+"Index.txt")));
            String st;
            int numberOfTerm = 0;

            while ((st = br.readLine()) != null) {
                String [] termName = st.split("\\|.\\|");
                double numberofFiles =(double) ((termName.length-1)/2);


                idfMap.put(termName[0],numberofFiles);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // writeIDF(idfMap);
    }



    public void writeIDF(){
        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INDEX_LOC+"IDF.txt")));
            for(String term:idfMap.keySet()){
                double n = (double) 1033/idfMap.get(term);
                double IDFval= Math.log(n)/Math.log(2);
                idfMap.put(term,IDFval);
                bw.write(term);
                bw.write("|.|");
                bw.write(String.valueOf(IDFval));
                bw.write("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void TF_IDFCalculation(){

        for(String fileName: tfMap.keySet()){
            HashMap<String,Double> termfrequencyHashMap = new HashMap<String, Double>();
            termfrequencyHashMap = tfMap.get(fileName);
            for(String term: termfrequencyHashMap.keySet()){

                    double TF_IDF_Val = termfrequencyHashMap.get(term)* idfMap.get(term);
                    termfrequencyHashMap.put(term,TF_IDF_Val);

            }

            tfIDFMap.put(fileName,termfrequencyHashMap);

        }

    }


    public  void writeTFIDF(){
        HashMap<String,Double> termDocScoreMap = new HashMap<String, Double>();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(INDEX_LOC + "TF_IDF.txt"), "utf-8"))) {

            for(Map.Entry<String,HashMap<String,Double>> entry: tfIDFMap.entrySet()){
                writer.write(entry.getKey());
                writer.write("|.|");
                termDocScoreMap = entry.getValue();


                for(String termDocScore: termDocScoreMap.keySet()){
                    writer.write(termDocScore);
                    writer.write("|.|");
                    writer.write(String.valueOf(termDocScoreMap.get(termDocScore)));
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


    public  void parseAndWriteQuery(){
        File file = new File(INPUT_FOLDER_LOC + "MEDQRY.txt");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            StringBuilder sb = new StringBuilder();
            boolean firstISkip = true;
            while ((st = br.readLine()) != null) {
                String [] stArray = st.split(" ");
                if (st.contains(".W")) {
                    continue;
                } else if (stArray[0].contains(".I")) {

                    if (sb.length() != 0) {
                        queryList.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    continue;
                }
                sb.append(st);
            }
            br.close();

        }
      catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        File queryfile = new File(INPUT_FOLDER_LOC + "ParsedQuery.txt");
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(queryfile));
            for(String query:queryList){
                bw.write(query);
                bw.write("\n");
            }
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public  void queryWithWeight(){
        File queryfile = new File(INPUT_FOLDER_LOC + "ParsedQuery.txt");
        File idfFile = new File(INDEX_LOC+"IDF.txt");
        BufferedReader brQuery = null;
        try {
            brQuery = new BufferedReader(new FileReader(queryfile));
            String stQuery;
            int queryNumber = 1;
            while ((stQuery = brQuery.readLine()) != null) {
                HashMap<String,Double>queryMap = new HashMap<String, Double>();

                String [] stQueryArr = stQuery.split("\\s+");
                double MAX_VALUE = 1;
                for(String queryTerm: stQueryArr){
                    if(queryTerm.length()==0){
                        continue;
                    }
                    if(queryMap.containsKey(queryTerm)){
                        double previousVal = queryMap.get(queryTerm);
                        previousVal++;
                        if(MAX_VALUE<previousVal){
                            MAX_VALUE = previousVal;
                        }
                        queryMap.put(queryTerm,previousVal);
                    }
                    else {
                        queryMap.put(queryTerm,(double)1);
                    }
                }

                for(String queryTerm: queryMap.keySet()){
                    double divideByMaxTerm = queryMap.get(queryTerm);
                    divideByMaxTerm/= MAX_VALUE;
                    queryMap.put(queryTerm,divideByMaxTerm);
                }

            queryBucketMap.put(queryNumber,queryMap);
                queryNumber++;
            }
            brQuery.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void queryTFIDF(){

        for(Integer queryNumber:queryBucketMap.keySet()){

            HashMap<String,Double> tempQueryMap = new HashMap<String, Double>();
            tempQueryMap = queryBucketMap.get(queryNumber);
            for(String query:tempQueryMap.keySet()){
                if(idfMap.containsKey(query)){
                    double idfVal = idfMap.get(query);
                    double queryWeight = tempQueryMap.get(query);
                    double queryIDFVal = queryWeight* idfVal;
                    tempQueryMap.put(query,queryIDFVal);
                }
            }

            queryBucketMap.put(queryNumber,tempQueryMap);
        }

     //   System.out.println("\n");
    }


   public  void rankingDocument(){
        Map<String,Float> docScoreJacacardMap;

        for(Integer queryNumber: queryBucketMap.keySet()){
             System.out.println("Query Number:" + queryNumber);
            docScoreJacacardMap = new HashMap<String, Float>();
             HashMap<String,Double> queryMap = new HashMap<String, Double>();
             queryMap = queryBucketMap.get(queryNumber);
             Map<String,Float> docScoreAgainstSingleQueryMap = new HashMap<String, Float>();
             float queryLength = 0;
             double summation = 0;
             //for measuring queryweight to send cossine funcion
            for(String queryW: queryMap.keySet()){
                double queryVal = queryMap.get(queryW);
                queryVal *= queryVal;
                summation +=queryVal;
                queryLength = (float) Math.sqrt(summation);
            }


             System.out.print("Query: ");
             for(String query: queryMap.keySet()){
                 startTime = System.currentTimeMillis();
                 System.out.print(query + " ");
                 HashMap<String,Float> tempHashMap = new HashMap<String ,Float>();
                 tempHashMap = CossineDocScore(query,queryMap.get(query),queryLength);
                     if(tempHashMap.size()!=0){
                         for(String fileName: tempHashMap.keySet()){
                             docScoreAgainstSingleQueryMap.put(fileName,tempHashMap.get(fileName));
                         }
                     }
             }

            docScoreJacacardMap = jaccardDocScore(queryNumber);
            docScoreJacacardMap = ValueSortHashMap.sortHashMap(docScoreJacacardMap,false);
            docScoreAgainstSingleQueryMap = ValueSortHashMap.sortHashMap(docScoreAgainstSingleQueryMap,false);
            int number = 0;
            System.out.print("\n");
            System.out.println("========Cossine Method=========");
            System.out.println("Matched File Number:" + docScoreAgainstSingleQueryMap.size());
             for(String fileName: docScoreAgainstSingleQueryMap.keySet()){
                 if(number == 0){
                     cosssineScoreList.add(docScoreAgainstSingleQueryMap.get(fileName));
                 }

                 if(number ==10){
                    break;
                }

                   System.out.print("File Name:" + fileName + " Ranking Score: "+ docScoreAgainstSingleQueryMap.get(fileName));
                   System.out.println(" Doc Snippet: " + docSnippet(fileName));
                   number ++;

            }
            System.out.println("========Jaccard Method=========");
            System.out.println("Matched File Number:" + docScoreJacacardMap.size());
            number = 0;
            for(String fileName: docScoreJacacardMap.keySet()){
               if(number == 0){
                   jaccardScoreList.add(docScoreJacacardMap.get(fileName));
               }

                if(number ==10){
                    break;
                }
                    System.out.print("File Name:" + fileName + " Ranking Score: "+ docScoreJacacardMap.get(fileName));
                    System.out.println(" Doc Snippet: " + docSnippet(fileName));
                    number ++;

            }

            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            time = Long.toString(elapsedTime);
            System.out.println("Total Time to Complete Search: " + time + " MilliSeconds");


        }
   }

   public  HashMap<String,Float> CossineDocScore(String query,Double queryWeight,float queryLenght){
        float docScoreByDefault = 0;
        HashMap<String,Double> tfIdfScoreInSingleFIleMap = new HashMap<String, Double>();
        HashMap<String,Float> docScoreMap = new HashMap<String, Float>();
        for(String fileName: tfIDFMap.keySet()){
            tfIdfScoreInSingleFIleMap = tfIDFMap.get(fileName);
            if(tfIdfScoreInSingleFIleMap.containsKey(query)){
                float totalTFIDFScore =(float) ( tfIdfScoreInSingleFIleMap.get(query)* queryWeight); // Cosine similarity
                double summation = 0;
                for(String queryOfSelectedFile:tfIdfScoreInSingleFIleMap.keySet()){
                    double termTFIDFVal =tfIdfScoreInSingleFIleMap.get(queryOfSelectedFile);  //length of File calculated here
                    summation += termTFIDFVal * termTFIDFVal;
                }

                float sqrtSummation = 0;
                sqrtSummation = (float) Math.sqrt(summation);
                docScoreByDefault = totalTFIDFScore/(sqrtSummation*queryLenght);
                docScoreMap.put(fileName,docScoreByDefault);
            }

        }

       return  docScoreMap;
   }


   public HashMap<String,Float> jaccardDocScore (Integer queryNumber){
          HashMap<String,Double> queryMap = new HashMap<String, Double>();
          HashMap<String,Float> docScoreJaccardMap = new HashMap<String, Float>();
          queryMap = queryBucketMap.get(queryNumber);

                for(String fileName: tfIDFMap.keySet()){
                    float union = 0;
                    float intersection =0;
                    float ranking = 0;
                    HashMap<String,Double> tfIdfScoreInSingleFIleMap = new HashMap<String, Double>();
                    tfIdfScoreInSingleFIleMap = tfIDFMap.get(fileName);
                        for(String query: queryMap.keySet()){
                               if(tfIdfScoreInSingleFIleMap.containsKey(query)){
                                   intersection ++;
                               }
                        }
                    if(intersection !=0){
                        union = queryMap.size()+ tfIdfScoreInSingleFIleMap.size() - intersection;
                        ranking = intersection/union;
                        docScoreJaccardMap.put(fileName,ranking);
                    }
                }

                return  docScoreJaccardMap;
    }


   public String docSnippet(String fileName){
       List<String> files = Util.getAbsoluteFilePathsFromFolder(DOCUMENT_FOLDER_LOC);
       File file = new File(DOCUMENT_FOLDER_LOC + fileName);
       String snippet="";
       BufferedReader br = null;
       try {
           br = new BufferedReader(new FileReader(file));
           br.readLine(); //First Line Empty so skip it.
           String st;
           snippet = br.readLine();
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
    return snippet;
   }


   public void  graphPlot(){


       final XYSeriesDemo demo = new XYSeriesDemo("Cossine and Jackard Similarity Result on Doc");
       demo.setGraphData(cosssineScoreList,jaccardScoreList);
       demo.pack();
       RefineryUtilities.centerFrameOnScreen(demo);
       demo.setVisible(true);

   }

  public  void  multipleGraphPlot(){
        MutipleLinesChart obj = new MutipleLinesChart();
        obj.setGraphData(cosssineScoreList,jaccardScoreList);
      SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
              new MutipleLinesChart().setVisible(true);
          }
      });
  }


}

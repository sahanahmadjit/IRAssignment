package client.search.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {


    public static void main(String[] args) {
	// write your code here

   /*     System.out.println("Document Separtation Begin:");
        DocumentParsing docObj = new DocumentParsing();
        Index indexObj = new Index();


        long startTime = System.currentTimeMillis();
        docObj.stopWordLoading();
        docObj.documentSeparation();
        System.out.println("Index Building Start:");
        indexObj.buildingIndex();
        indexObj.writeIndexFile();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        indexObj.setElapsedTime(elapsedTime);
        System.out.println("Index Building End:");
        indexObj.printResult();
        System.out.println("Result Printed!");

*/

        DocumentScoreCalculation docScoreObj = new DocumentScoreCalculation();
      //  docScoreObj.parseAndWriteQuery();
        docScoreObj.tfValueCalCulation();
        docScoreObj.writeTFValue();
        docScoreObj.idfCalculation();
        docScoreObj.writeIDF();
        docScoreObj.TF_IDFCalculation();
        docScoreObj.writeTFIDF();
        docScoreObj.queryWithWeight();
        docScoreObj.queryTFIDF();
        docScoreObj.rankingDocument();
        //docScoreObj.graphPlot();
        docScoreObj.multipleGraphPlot();

      //  Ranking rankObj = new Ranking();
     //   rankObj.docAssociatinWithQueryTerm();

    }
}

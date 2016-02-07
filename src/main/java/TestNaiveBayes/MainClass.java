package TestNaiveBayes;

import org.json.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by dmitry on 07.02.16.
 */
public class MainClass {
    public static void main(String[] args) throws FileNotFoundException{
        Classifier classifier = new Classifier();
        String tmp = "";

        Scanner scanner = new Scanner(new FileInputStream("./train.json"));
        while (scanner.hasNext()) tmp  += scanner.nextLine();
        JSONObject trainData = new JSONObject(tmp);
        JSONArray array = trainData.getJSONArray("root");
        Iterator<Object> it =  array.iterator();
        while(it.hasNext()) {
            JSONObject element = ((JSONObject) it.next()).getJSONObject("element");
            List<String> words = new ArrayList<String>();
            for (String word : element.getString("text").toLowerCase().split(" "))
                words.add(word);
            String classname = element.getString("label");
            classifier.train(words, classname);
        }

        scanner = new Scanner(new FileInputStream("./predict.json"));
        tmp = "";
        while (scanner.hasNext()) tmp += scanner.nextLine();
        JSONObject predictData = new JSONObject(tmp);

        scanner = new Scanner(new FileInputStream("./answers.json"));
        tmp = "";
        while (scanner.hasNext()) tmp += scanner.nextLine();
        JSONObject predictLabels = new JSONObject(tmp);
        JSONArray answers =  predictLabels.getJSONArray("root");
        Iterator<Object> ansIterator = answers.iterator();

        array = predictData.getJSONArray("root");
        Iterator<Object> predIterator = array.iterator();
        int trueAns = 0;
        int allAnswers = 0;
        while (predIterator.hasNext() && ansIterator.hasNext()) {
            allAnswers++;
            List<String> features = new ArrayList<String>();
            for (String feature : ((JSONObject)predIterator.next()).getString("text")
                    .toLowerCase().split(" ")) {
                features.add(feature);
            }

                String predict = classifier.predict(features);
            String answer = ((JSONObject)ansIterator.next()).getString("label");
            if (predict.equals(answer))
                trueAns++;
        }

        System.out.println("ACCURACY: " + (1.0 * trueAns / allAnswers));

    }
}

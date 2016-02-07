package TestNaiveBayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitry on 07.02.16.
 */
public class Classifier {
    private int dataCount;
    private Map<String, Map<String, Integer>> featuresCount;
    private Map<String, Integer> classesCount;

    Classifier(){
        this.featuresCount = new HashMap<String, Map<String, Integer>>();
        this.classesCount = new HashMap<String, Integer>();
        this.dataCount = 0;
    }

    private double getLogProbability(String classname, List<String> features){
        double result = 0.0;
        int classCount = classesCount.get(classname);
        result += Math.log(1.0 * classCount / dataCount);
        Map<String, Integer> classFeatures = featuresCount.get(classname);
        for (String feature : features) {
            result += classFeatures.containsKey(feature) ? 1.0 * classFeatures.get(feature) / classCount : 0;
        }
        return -result;
    }

    public void train(List<String> features, String classname){
        dataCount++;
        if (classesCount.containsKey(classname)){
            classesCount.put(classname, classesCount.get(classname) + 1);
        }
        else {
            classesCount.put(classname, 1);
            featuresCount.put(classname, new HashMap<String, Integer>());
        }
        Map<String, Integer> classFeatures = featuresCount.get(classname);
        for (String feat : features) {
            if (classFeatures.containsKey(feat))
                classFeatures.put(feat, classFeatures.get(feat) + 1);
            else
                classFeatures.put(feat, 1);
        }
    }

    public String predict(List<String> features){
        String result = null;
        double min = Double.MAX_VALUE;
        for (String classname : classesCount.keySet()) {
            double prob = getLogProbability(classname, features);
            if (prob < min) {
                min = prob;
                result = classname;
            }
        }
        return result;
    }
}

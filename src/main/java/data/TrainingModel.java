package data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 08.02.16.
 */
public class TrainingModel implements Serializable {
    public long modelVersion;
    public int dataCount;
    public Map<String, Map<String, Integer>> featuresCount;
    public Map<String, Integer> classesCount;

    public TrainingModel() {
        modelVersion = 0;
        dataCount = 0;
        featuresCount = new HashMap<String, Map<String, Integer>>();
        classesCount = new HashMap<String, Integer>();
    }
}

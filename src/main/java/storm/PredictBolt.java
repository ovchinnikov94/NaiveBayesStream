package storm;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import data.PredictClass;
import data.TrainingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitry on 08.02.16.
 * PredictBolt - element in Storm Topology, which makes a decision about PredictClass
 */
public class PredictBolt extends BaseBasicBolt {
    private TrainingModel model;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        model = new TrainingModel();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        if (tuple.getFields().contains("train-model")) {
            TrainingModel newModel = (TrainingModel) tuple.getValueByField("train-model");
            if (this.model.modelVersion < newModel.modelVersion)
                updateModel(newModel);
            return;
        }
        if (tuple.getFields().contains("predict-data")) {
            PredictClass predictClass = (PredictClass)tuple.getValueByField("predict-data");
            String result = null;
            double min = Double.MAX_VALUE;
            for (String label : model.classesCount.keySet()) {
                double prob = this.getLogProbability(label, predictClass.features);
                if (min > prob){
                    min = prob;
                    result = label;
                }
            }
            predictClass.label = result;
            basicOutputCollector.emit(new Values(predictClass));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("predicted-data"));
    }

    private void updateModel(TrainingModel newModel){
        model.modelVersion = newModel.modelVersion;
        model.dataCount = newModel.dataCount;
        model.classesCount = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : newModel.classesCount.entrySet())
            model.classesCount.put(entry.getKey(), entry.getValue());
        model.featuresCount = new HashMap<String, Map<String, Integer>>();
        for (Map.Entry<String, Map<String, Integer>> entry : newModel.featuresCount.entrySet()) {
            Map<String, Integer> classFeatures = new HashMap<String, Integer>();
            for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet())
                classFeatures.put(entry1.getKey(), entry1.getValue());
            model.featuresCount.put(entry.getKey(), classFeatures);
        }
    }

    private double getLogProbability(String classname, List<String> features){
        double result = 0.0;
        int classCount = model.classesCount.get(classname);
        result += Math.log(1.0 * classCount / model.dataCount);
        Map<String, Integer> classFeatures = model.featuresCount.get(classname);
        for (String feature : features) {
            result += classFeatures.containsKey(feature) ? 1.0 * classFeatures.get(feature) / classCount : 0;
        }
        return -result;
    }

}

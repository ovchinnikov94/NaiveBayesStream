package storm;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import data.TrainingModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmitry
 * Train Bolt - trains naive bayes classifier,
 * sends changes to Predict Bolts.
 */
public class TrainBolt extends BaseBasicBolt {
    private TrainingModel model;
    private int newTrainings;
    private static int UPDATE_FREQ = 3;
    private int targetWorkers;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        model = new TrainingModel();
        newTrainings = 0;
        targetWorkers = context.getThisTargets().size();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        if (!tuple.getFields().contains("train-data") ||
                !tuple.getFields().contains("label")) return;
        train((List<String>)tuple.getValueByField("train-data"), (String)tuple.getValueByField("label"));
        if (newTrainings >= UPDATE_FREQ) {
            newTrainings = 0;
            this.model.modelVersion++;
            for (int i = 0; i < targetWorkers; i++)
                basicOutputCollector.emit(new Values(this.model));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("train-model"));
    }

    private void train(List<String> features, String label){
        newTrainings++;
        model.dataCount++;
        if (model.classesCount.containsKey(label)){
            model.classesCount.put(label, model.classesCount.get(label) + 1);
        }
        else {
            model.classesCount.put(label, 1);
            model.featuresCount.put(label, new HashMap<String, Integer>());
        }
        Map<String, Integer> classFeatures = model.featuresCount.get(label);
        for (String feat : features) {
            if (classFeatures.containsKey(feat))
                classFeatures.put(feat, classFeatures.get(feat) + 1);
            else
                classFeatures.put(feat, 1);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //saving model to json file
    }
}

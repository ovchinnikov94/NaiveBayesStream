package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitry on 08.02.16.
 * PredctClass - Input class - is an object, which will be classified
 */
public class PredictClass implements Serializable {
    public long id;
    public List<String> features;
    public String label;

    PredictClass(){
        id = this.hashCode();
        label = "";
        features = new ArrayList<String>();
    }

}

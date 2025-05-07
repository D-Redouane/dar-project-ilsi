package sensor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SensorData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Double> environmentData;
    private final Map<String, Double> cropData;

    public SensorData() {
        this.environmentData = new HashMap<>();
        this.cropData = new HashMap<>();
    }

    public void addEnvironmentData(String key, Double value) {
        environmentData.put(key, value);
    }

    public void addCropData(String key, Double value) {
        cropData.put(key, value);
    }

    public Map<String, Double> getEnvironmentData() {
        return environmentData;
    }

    public Map<String, Double> getCropData() {
        return cropData;
    }
}

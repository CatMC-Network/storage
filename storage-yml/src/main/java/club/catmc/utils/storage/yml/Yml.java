package club.catmc.utils.storage.yml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Yml {

    private final String filePath;
    private final Yaml yaml;
    private Map<String, Object> data;

    public Yml(String filePath) {
        this.filePath = filePath;
        this.yaml = new Yaml();
    }

    public void load() throws FileNotFoundException {
        this.data = yaml.load(new FileInputStream(filePath));
    }

    public void save() throws IOException {
        yaml.dump(data, new FileWriter(filePath));
    }

    public Map<String, Object> getData() {
        return data;
    }
}
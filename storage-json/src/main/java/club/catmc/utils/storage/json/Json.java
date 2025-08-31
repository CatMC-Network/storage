package club.catmc.utils.storage.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Json<T> {

    private final String filePath;
    private final Gson gson;
    private final Class<T> type;
    private T data;

    public Json(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.type = type;
    }

    public void load() throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            this.data = gson.fromJson(reader, type);
        }
    }

    public void save() throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

    public T getData() {
        return data;
    }
}
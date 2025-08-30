package club.catmc.utils.storage.mongo;

import club.catmc.utils.storage.mongo.annotations.Document;
import club.catmc.utils.storage.mongo.annotations.Field;
import club.catmc.utils.storage.mongo.annotations.Indexed;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.Arrays;

import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;

public class Mongo {

    private final String connectionString;
    private final String databaseName;
    private MongoClient mongoClient;
    private MongoDatabase database;

    public Mongo(String connectionString, String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    public void init() {
        this.mongoClient = MongoClients.create(connectionString);
    }

    public void start() {
        PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .conventions(Arrays.asList(ANNOTATION_CONVENTION))
                .automatic(true)
                .build();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(pojoCodecProvider);
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        this.database = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry);
    }

    public void reload() {
        end();
        init();
        start();
    }

    public void end() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public <T> MongoCollection<T> getCollection(Class<T> clazz) {
        Document document = clazz.getAnnotation(Document.class);
        if (document == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @Document");
        }
        return database.getCollection(document.value(), clazz);
    }

    public void createIndexes(Class<?> clazz) {
        MongoCollection<?> collection = getCollection(clazz);
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Indexed.class)) {
                Indexed indexed = field.getAnnotation(Indexed.class);
                String fieldName = field.getName();
                if (field.isAnnotationPresent(Field.class)) {
                    fieldName = field.getAnnotation(Field.class).value();
                }
                IndexOptions indexOptions = new IndexOptions().unique(indexed.unique());
                collection.createIndex(Indexes.ascending(fieldName), indexOptions);
            }
        }
    }
}
package com.alysonsantos.aspect.storage;

import com.google.gson.*;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GsonStorageHandler {

    private final Gson gson;
    private final String name;

    private JsonObject jsonObject;
    private File file;

    public GsonStorageHandler(File file, String name) {
        this.file = file;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        this.name = name;
    }

    @SneakyThrows
    public void initialize() {
        if (!file.exists() && !file.mkdir())
            throw new RuntimeException("Could not create parent directory");

        this.file = new File(file.getAbsolutePath() + File.separator + name + ".json");
        if (!file.exists()) {
            if (!file.createNewFile()) throw new RuntimeException("Could not create file");
        }

        val fileReader = new FileReader(file);
        var parse = new JsonParser().parse(fileReader);

        fileReader.close();

        if (parse instanceof JsonNull)
            jsonObject = new JsonObject();
        else
            jsonObject = parse.getAsJsonObject();

    }

    public boolean contains(String key) {
        if (!key.contains("."))
            return jsonObject.has(key);

        val split = key.split("\\.");
        JsonElement currentElement = jsonObject;

        for (final String subPath : split) {
            if (!currentElement.isJsonObject()) {
                return false;
            } else if (!currentElement.getAsJsonObject().has(subPath)) {
                return false;
            }

            currentElement = currentElement.getAsJsonObject().get(subPath);
        }

        return true;
    }

    public GsonStorageHandler insert(String key, Object value) {
        if (!key.contains(".")) {
            set(jsonObject, key, value);
            return this;
        }

        String[] path = key.split("\\.");
        JsonObject currentElement = this.jsonObject;

        for (int i = 0; i < path.length; i++) {
            String subPath = path[i];

            if (i == path.length - 1) {
                set(currentElement, subPath, value);
                break;
            }

            if (!currentElement.has(subPath)) {
                currentElement.add(subPath, new JsonObject());
                currentElement = currentElement.get(subPath).getAsJsonObject();
                continue;
            }

            currentElement = currentElement.get(subPath).getAsJsonObject();
        }

        return this;
    }

    private boolean set(JsonObject jsonObject, String key, Object value) {
        if (value == null) {
            jsonObject.remove(key);
        } else {
            jsonObject.add(key, gson.toJsonTree(value));
        }

        return true;
    }

    public JsonElement get(String key) {
        if (!contains(key)) {
            return JsonNull.INSTANCE;
        } else if (!key.contains(".")) {
            return jsonObject.get(key);
        }

        val path = key.split("\\.");
        JsonElement currentElement = jsonObject;

        for (final String subPath : path) {
            if (!currentElement.isJsonObject()) {
                return JsonNull.INSTANCE;
            } else if (!currentElement.getAsJsonObject().has(subPath)) {
                return JsonNull.INSTANCE;
            }

            currentElement = currentElement.getAsJsonObject().get(subPath);
        }

        return currentElement;
    }

    public void save() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            gson.toJson(jsonObject, fileWriter);

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


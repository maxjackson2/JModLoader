package dev.jackson.modloader;

import dev.jackson.modloader.exceptions.InvalidDataFileException;
import org.json.JSONObject;

public class ModData {

    private String name, main, description, version, website;
    private String[] authors, dependentMods;

    public ModData(JSONObject object) throws InvalidDataFileException {
        loadJsonObject(object);
    }
    public void loadJsonObject(JSONObject object) throws InvalidDataFileException {

        if (!object.has("name"))
            throw new InvalidDataFileException("No name field was found in data file.");
        setName(object.getString("name"));
        if (!object.has("version"))
            throw new InvalidDataFileException("No version field was found in data file.");
        setVersion(object.getString("version"));
        if (!object.has("main"))
            throw new InvalidDataFileException("No main field was found in data file.");
        setMain(object.getString("main"));
        if (object.has("description"))
            setDescription(object.getString("description"));
        if (object.has("website"))
            setWebsite(object.getString("website"));
        if (object.has("authors"))
            setAuthors((String[]) object.getJSONArray("authors").toList().toArray(new Object[0]));
        if (object.has("depends"))
            setDependentMods((String[]) object.getJSONArray("depends").toList().toArray(new Object[0]));
    }

    public String[] getDependentMods() {
        return dependentMods;
    }

    public void setDependentMods(String[] dependentMods) {
        this.dependentMods = dependentMods;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMain() {
        return main;
    }

    public String getVersion() {
        return version;
    }

    public String getWebsite() {
        return website;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}

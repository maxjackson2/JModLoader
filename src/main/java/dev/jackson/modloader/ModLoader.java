package dev.jackson.modloader;

import dev.jackson.modloader.exceptions.InvalidDataFileException;
import dev.jackson.modloader.exceptions.InvalidModBaseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader {

    private final Map<String, ModBase> mods = new HashMap<>();
    private final File modDirectory;

    public ModLoader(File modDirectory) {
        this.modDirectory = modDirectory;
    }

    public File getModDirectory() {
        return modDirectory;
    }

    public void loadMods() throws InvalidDataFileException,
            InvalidModBaseException,
            IOException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException {
        for (File file : Objects.requireNonNull(this.modDirectory.listFiles())) {
            if (!file.getName().endsWith(".jar")) continue;

            ModBase base = this.loadMod(file);
            this.getMods().put(base.getData().getName(), base);
        }
    }

    public void enableAllMods() {
        for (Map.Entry<String, ModBase> modBaseEntry : this.getMods().entrySet()) {
            ModData data = modBaseEntry.getValue().getData();
            System.out.println("Enabling Module " + data.getName() + "@" + data.getVersion());
            modBaseEntry.getValue().onEnable();
            System.out.println("Enabled Module " + data.getName() + "@" + data.getVersion());
        }
    }

    public void disableAllMods() {
        for (Map.Entry<String, ModBase> modBaseEntry : this.getMods().entrySet()) {
            ModData data = modBaseEntry.getValue().getData();
            System.out.println("Disabling Module " + data.getName() + "@" + data.getVersion());
            modBaseEntry.getValue().onDisable();
            System.out.println("Disabling Module " + data.getName() + "@" + data.getVersion());
        }
    }

    public ModBase loadMod(File file) throws InvalidModBaseException, IOException, InvalidDataFileException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        File fullPath = new File(this.modDirectory, file.getName());
        if (!fullPath.exists())
            throw new InvalidModBaseException("The mod file doesn't exist. Please provide a file that exists within the same mod directory.");

        try (JarFile jarFile = new JarFile(fullPath)) {

            ModData data = this.getDataFile(fullPath);

            if (data.getDependentMods() != null) {
                for (String mod : data.getDependentMods()) {
                    if (!this.isModLoaded(mod)) {
                        throw new InvalidModBaseException("Cannot Load Module without this module \"" + mod + "\"");
                    }
                }
            }

            URLClassLoader loader = new URLClassLoader(
                    new URL[]{ fullPath.toURI().toURL() },
                    this.getClass().getClassLoader()
            );
            Class<ModBase> baseClass = (Class<ModBase>) Class.forName(data.getMain(), true, loader);
            System.out.println("Loading Module " + data.getName() + "@" + data.getVersion());
            ModBase instance = baseClass.newInstance();
            instance.init(this, data);
            instance.onLoad();
            System.out.println("Loaded Module " + data.getName() + "@" + data.getVersion());
            return instance;
        }
    }

    public boolean isModLoaded(String modName) {
        return this.mods.containsKey(modName);
    }

    public ModData getDataFile(File file) throws InvalidDataFileException, IOException {

        if (!file.exists())
            throw new InvalidDataFileException("File " + file.getName() + " does not exist.");

        try (JarFile jarFile = new JarFile(file)) {

            JarEntry entry = jarFile.getJarEntry("mod.json");
            if (entry == null)
                throw new InvalidDataFileException("Entry mod.json does not exist within the file " + file.getName());

            JSONTokener tokener = new JSONTokener(
                    jarFile.getInputStream(entry)
            );
            JSONObject object = new JSONObject(tokener);
            ModData data = new ModData(object);
            jarFile.close();
            return data;
        }
    }

    public void enableMod(String mod) {
        ModBase base = this.mods.get(mod);
        if (base != null)
            base.onEnable();
    }

    public void disableMod(String mod) {
        ModBase base = this.mods.get(mod);
        if (base != null)
            base.onDisable();
    }

    public ModBase getMod(String mod) {
        return this.mods.get(mod);
    }

    public Map<String, ModBase> getMods() {
        return mods;
    }
}

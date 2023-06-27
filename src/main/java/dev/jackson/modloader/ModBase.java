package dev.jackson.modloader;

public abstract class ModBase {

    private ModData data;
    private ModLoader loader;

    public abstract void onLoad();
    public abstract void onEnable();
    public abstract void onDisable();

    synchronized void init(ModLoader loader, ModData data) {
        this.data = data;
        this.loader = loader;
    }

    public ModData getData() {
        return data;
    }

    public ModLoader getLoader() {
        return loader;
    }
}

package dev.omarathon.redditcraft.data.engines;

public interface TableDataEngine {
    void create();
    void delete();
    boolean exists();
}

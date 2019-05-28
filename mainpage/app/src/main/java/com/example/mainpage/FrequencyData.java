package com.example.mainpage;

public class FrequencyData {
    long id;
    long targetId;
    int frequency;

    public FrequencyData(long id, long targetId, int frequency) {
        this.id = id;
        this.targetId = targetId;
        this.frequency = frequency;
    }

    public long getId() {
        return id;
    }

    public long getTargetId() {
        return targetId;
    }

    public int getFrequency() {
        return frequency;
    }
}

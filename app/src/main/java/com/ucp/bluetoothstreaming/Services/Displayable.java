package com.ucp.bluetoothstreaming.Services;

public interface Displayable {
    public abstract void handleTextReception(String textReceived);
    public abstract void playVideo(String textReceived);
    public abstract void updateProgressBar(int progress);

}

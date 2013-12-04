/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm;

/**
 *
 * @author usien
 */
public enum DownloadState {

    RUNNING("Downloading"), EXCEPTION("Error"), COMPLETE("Done"),
    CONNECTING("Connecting"), IDLE("Idle"), PAUSED("Paused"),
    RETRYING("Retrying"), REDIRECTING("Redirecting"), CONNECTED("Connected"),
    PAUSING("Pausing"), CONNECTION_EXCEPTION("Error: Connection error"), FAILED("Failed: File not found");
    
    private String title;

    DownloadState(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}

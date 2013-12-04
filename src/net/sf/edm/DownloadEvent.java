/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm;

/**
 *
 * @author usien
 */
public class DownloadEvent {

    private boolean dataEvnt = false;
    private boolean excEvnt = false;
    private boolean stEvnt = false;
    private Exception exc = null;
    private DownloadState st = null;
    private byte[] data = null;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isDataEvent() {
        return dataEvnt;
    }

    public void setDataEvent(boolean dataEvnt) {
        this.dataEvnt = dataEvnt;
    }

    public Exception getException() {
        return exc;
    }

    public void setException(Exception exc) {
        this.exc = exc;
    }

    public boolean isExceptionEvent() {
        return excEvnt;
    }

    public void setExceptionEvent(boolean excEvnt) {
        this.excEvnt = excEvnt;
    }

    public DownloadState getStatus() {
        return st;
    }

    public void setState(DownloadState st) {
        this.st = st;
    }

    public boolean isStateEvent() {
        return stEvnt;
    }

    public void setStateEvent(boolean stEvnt) {
        this.stEvnt = stEvnt;
    }
}

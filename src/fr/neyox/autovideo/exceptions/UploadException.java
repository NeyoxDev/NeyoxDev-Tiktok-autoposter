package fr.neyox.autovideo.exceptions;

public class UploadException extends Exception {

    public UploadException(Exception parent) {
        super(parent);
    }

    public UploadException(String name){
        super(name);
    }
}

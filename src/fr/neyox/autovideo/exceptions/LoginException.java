package fr.neyox.autovideo.exceptions;

public class LoginException extends Exception {
    public LoginException(Exception parent) {
        super(parent);
    }

    public LoginException(String name){
        super(name);
    }
}

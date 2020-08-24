package net.drabc.INSWatchDog.RconClient.Exceptions;

public class AuthFailureException extends RconClientException {
    public AuthFailureException() {
        super("Authentication failure");
    }
}

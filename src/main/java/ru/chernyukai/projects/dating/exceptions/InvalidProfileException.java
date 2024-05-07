package ru.chernyukai.projects.dating.exceptions;

public class InvalidProfileException extends RuntimeException{
    String msg;
    public InvalidProfileException(String msg){
        this.msg = msg;
    }

}

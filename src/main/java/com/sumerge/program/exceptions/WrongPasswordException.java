package com.sumerge.program.exceptions;

public class WrongPasswordException extends Exception
{
    public WrongPasswordException(String message)
    {
        super(message);
    }
}

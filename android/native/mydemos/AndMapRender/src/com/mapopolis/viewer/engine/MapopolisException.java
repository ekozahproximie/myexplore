package com.mapopolis.viewer.engine;

public class MapopolisException extends Exception

{

    String msg;

    public MapopolisException(String s)

    {
        msg = s;
    }

    public String getMessage()

    {
        return msg;
    }
}
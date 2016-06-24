
package com.trimble.agmantra.filecodec.shp;

public class Field

{
    public String name;

    public char type;

    public int length;

    public int start;
    

    Field(byte[] bytes, int start)

    {
        for (int i = 0; i < 11; ++i)
            if (bytes[i] == 0)
                bytes[i] = (byte)' ';

        name = new String(bytes, 0, 11);
        name = name.trim();

        type = (char)bytes[11];
        length = bytes[16];
        this.start = start;

        // Form1.out.println(name + " " + length + " " + iShapeType);
    }

    public String toString()

    {
        return name + " " + type + " " + length;
    }

    public String getString(byte[] bytes)

    {
        byte[] out = new byte[length];

        for (int i = 0; i < length; ++i) {
            byte b = bytes[i + start];
            if (b == 0)
                b = (byte)' ';
            out[i] = b;
        }

        return new String(out);
    }
}

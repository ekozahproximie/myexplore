
package com.mapopolis.viewer.core;

public class IO

{
    public static long get8(byte[] array, int index)

    {
        long r = 0;
        long n;

        for (int i = 0; i < 8; ++i) {
            n = array[index++];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static long get8r(byte[] array, int index)

    {
        long r = 0;
        long n;

        for (int i = 7; i >= 0; --i) {
            n = array[index + i];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get4(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 0; i < 4; ++i) {
            n = array[index++];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get4r(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 3; i >= 0; --i) {
            n = array[index + i];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get2(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 0; i < 2; ++i) {
            n = array[index++];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get1(byte[] array, int index)

    {
        int n = array[index];

        if (n < 0) n += 256;

        return n;
    }

    public static int get3(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 0; i < 3; ++i) {
            n = array[index++];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get2r(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 1; i >= 0; --i) {
            n = array[index + i];
            if (n < 0) n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int put1(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte) data;
        return offset;
    }

    public static int put2(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte) (data >> 8);
        bytes[offset++] = (byte) (data & 0xff);
        return offset;
    }

    public static int put4(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte) (data >> 24);
        bytes[offset++] = (byte) ((data >> 16) & 0xff);
        bytes[offset++] = (byte) ((data >> 8) & 0xff);
        bytes[offset++] = (byte) (data & 0xff);
        return offset;
    }

    public static int put3(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte) ((data >> 16) & 0xff);
        bytes[offset++] = (byte) ((data >> 8) & 0xff);
        bytes[offset++] = (byte) (data & 0xff);
        return offset;
    }
}
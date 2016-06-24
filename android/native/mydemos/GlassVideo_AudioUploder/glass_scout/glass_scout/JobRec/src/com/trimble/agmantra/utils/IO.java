
package com.trimble.agmantra.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
/**
 * Big endian(Network byte order) format reader and writer
 * 
 * @author senthil
 */
public class IO

{

    public static int get4l(byte[] array, int index) {
        int r = 0;
        int n;

        for (int i = 3; i >= 0; --i) {
            n = array[index + i];
            if (n < 0) {
                n += 256;
            }
            r = (r << 8) + n;
        }

        return r;
    }

    public static long get8l(byte[] array, int index) {
        long r = 0;
        long n;

        for (int i = 7; i >= 0; --i) {
            n = array[index + i];

            // Form1.msg("Byte = " + n);

            if (n < 0) {
                n += 256;
            }
            r = (r << 8) + n;
        }

        // Form1.msg("Long = " + r);

        return r;
    }

  
    public static long get8(byte[] array, int index)

    {
        long r = 0;
        long n;

        for (int i = 0; i < 8; ++i) {
            n = array[index++];
            if (n < 0)
                n += 256;
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
            if (n < 0)
                n += 256;
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
            if (n < 0)
                n += 256;
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
            if (n < 0)
                n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get3(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 0; i < 3; ++i) {
            n = array[index++];
            if (n < 0)
                n += 256;
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
            if (n < 0)
                n += 256;
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
            if (n < 0)
                n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int get1(byte[] array, int index)

    {
        int r = 0;
        int n;

        for (int i = 0; i < 1; ++i) {
            n = array[index++];
            if (n < 0)
                n += 256;
            r = (r << 8) + n;
        }

        return r;
    }

    public static int put1(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte)data;
        return offset;
    }

    public static int put2(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte)(data >> 8);
        bytes[offset++] = (byte)(data & 0xff);
        return offset;
    }

    public static int put3(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte)((data >> 16) & 0xff);
        bytes[offset++] = (byte)((data >> 8) & 0xff);
        bytes[offset++] = (byte)(data & 0xff);
        return offset;
    }

    public static int put4b(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte)(data >> 24);
        bytes[offset++] = (byte)((data >> 16) & 0xff);
        bytes[offset++] = (byte)((data >> 8) & 0xff);
        bytes[offset++] = (byte)(data & 0xff);
        return offset;
    }

    public static int put4(byte[] bytes, int offset, int data)

    {
        bytes[offset++] = (byte)(data & 0xff);
        bytes[offset++] = (byte)((data >> 8) & 0xff);
        bytes[offset++] = (byte)((data >> 16) & 0xff);
        bytes[offset++] = (byte)(data >> 24);
        return offset;
    }
    public static int put4r(byte[] bytes, int offset, int data)

    {
       bytes[offset++] = (byte) (data >> 24);
       bytes[offset++] = (byte) ((data >> 16) & 0xff);
       bytes[offset++] = (byte) ((data >> 8) & 0xff);
       bytes[offset++] = (byte) (data & 0xff);
       return offset;
    }
    public static int put5(byte[] bytes, int offset, long data)

    {
        bytes[offset++] = (byte)((data >> 32) & 0xff);
        bytes[offset++] = (byte)((data >> 24) & 0xff);
        bytes[offset++] = (byte)((data >> 16) & 0xff);
        bytes[offset++] = (byte)((data >> 8) & 0xff);
        bytes[offset++] = (byte)(data & 0xff);
        return offset;
    }

    public static int put8(byte[] bytes, int offset, long data)

    {
        bytes[offset++] = (byte)((data >> 56) & 0xff);
        bytes[offset++] = (byte)((data >> 48) & 0xff);
        bytes[offset++] = (byte)((data >> 40) & 0xff);
        bytes[offset++] = (byte)((data >> 32) & 0xff);
        bytes[offset++] = (byte)((data >> 24) & 0xff);
        bytes[offset++] = (byte)((data >> 16) & 0xff);
        bytes[offset++] = (byte)((data >> 8) & 0xff);
        bytes[offset++] = (byte)(data & 0xff);
        return offset;
    }
   public static int putdouble (byte[]bytes, int offset, double dData, boolean bReverse)
   
   {
      ByteArrayOutputStream byteArraystream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArraystream);
      try {
         dataOutputStream.writeDouble(dData);
         dataOutputStream.flush();
      } catch (Exception e)
      {
         e.printStackTrace();
      }
      
      byte[] rawDoubleBytes = byteArraystream.toByteArray();
      
      if (8 == rawDoubleBytes.length)
      {
         if (false == bReverse)
         {
            bytes[offset++] = rawDoubleBytes[0];
            bytes[offset++] = rawDoubleBytes[1];
            bytes[offset++] = rawDoubleBytes[2];
            bytes[offset++] = rawDoubleBytes[3];
            bytes[offset++] = rawDoubleBytes[4];
            bytes[offset++] = rawDoubleBytes[5];
            bytes[offset++] = rawDoubleBytes[6];
            bytes[offset++] = rawDoubleBytes[7];
         }
         else
         {
            bytes[offset++] = rawDoubleBytes[7];
            bytes[offset++] = rawDoubleBytes[6];
            bytes[offset++] = rawDoubleBytes[5];
            bytes[offset++] = rawDoubleBytes[4];
            bytes[offset++] = rawDoubleBytes[3];
            bytes[offset++] = rawDoubleBytes[2];
            bytes[offset++] = rawDoubleBytes[1];
            bytes[offset++] = rawDoubleBytes[0];
         }
      }
      
      try {
         if (null != dataOutputStream) {
            dataOutputStream.close();
            dataOutputStream = null;
         }
         
         if (null != byteArraystream) {
            byteArraystream.close();
            byteArraystream = null;
         }
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return offset;
   }

    public static byte[] put8dBig(double d) {
        long l = Double.doubleToRawLongBits(d);

        byte array[] = new byte[8];

        array[0] = (byte)((l >> 56) & 0xff);
        array[1] = (byte)((l >> 48) & 0xff);
        array[2] = (byte)((l >> 40) & 0xff);
        array[3] = (byte)((l >> 32) & 0xff);
        array[4] = (byte)((l >> 24) & 0xff);
        array[5] = (byte)((l >> 16) & 0xff);
        array[6] = (byte)((l >> 8) & 0xff);
        array[7] = (byte)((l >> 0) & 0xff);

        return array;
    }

    public static byte[] put8dLittle(double d) {
        long l = Double.doubleToRawLongBits(d);

        byte array[] = new byte[8];

        array[7] = (byte)((l >> 56) & 0xff);
        array[6] = (byte)((l >> 48) & 0xff);
        array[5] = (byte)((l >> 40) & 0xff);
        array[4] = (byte)((l >> 32) & 0xff);
        array[3] = (byte)((l >> 24) & 0xff);
        array[2] = (byte)((l >> 16) & 0xff);
        array[1] = (byte)((l >> 8) & 0xff);
        array[0] = (byte)((l >> 0) & 0xff);

        return array;
    }
}

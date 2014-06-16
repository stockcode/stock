package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IntUtils
{

    public IntUtils()
    {
    }

    public static int toInt(byte bRefArr[])
    {
        int iOutcome = 0;
        for(int i = 0; i < bRefArr.length; i++)
        {
            byte bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xff) << 8 * i;
        }

        return iOutcome;
    }

    public static int readInt32(RandomAccessFile file)
        throws IOException
    {
        byte buff[] = new byte[4];
        file.read(buff);
        return toInt(buff);
    }

    public static int readInt16(RandomAccessFile file)
        throws IOException
    {
        byte buff[] = new byte[2];
        file.read(buff);
        return toInt(buff);
    }

    public static double readDouble(RandomAccessFile file)
        throws IOException
    {
        byte inData[] = new byte[8];
        file.read(inData);
        int j = 0;
        int upper = ((inData[j + 7] & 0xff) << 24) + ((inData[j + 6] & 0xff) << 16) + ((inData[j + 5] & 0xff) << 8) + ((inData[j + 4] & 0xff) << 0);
        int lower = ((inData[j + 3] & 0xff) << 24) + ((inData[j + 2] & 0xff) << 16) + ((inData[j + 1] & 0xff) << 8) + ((inData[j] & 0xff) << 0);
        return Double.longBitsToDouble(((long)upper << 32) + ((long)lower & 0xffffffffL));
    }
}
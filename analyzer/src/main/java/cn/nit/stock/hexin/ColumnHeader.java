package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ColumnHeader
{

    public ColumnHeader()
    {
    }

    public static Boolean Read(ColumnHeader header, RandomAccessFile file)
    {
        if(file == null)
            return Boolean.valueOf(false);
        try
        {
            header.w1 = file.readByte();
            header.type = file.readByte();
            header.w2 = file.readByte();
            header.size = file.readByte();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }

    

    @Override
	public String toString() {
		return "ColumnHeader [w1=" + w1 + ", type=" + type + ", w2=" + w2
				+ ", size=" + size + "]";
	}



	private byte w1;
    private byte type;
    private byte w2;
    private byte size;
    public static int LENGTH = 4;

}
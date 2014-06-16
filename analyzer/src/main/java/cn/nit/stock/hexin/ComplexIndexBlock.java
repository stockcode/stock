package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ComplexIndexBlock
{

    public ComplexIndexBlock()
    {
    }

    public static Boolean Read(ComplexIndexBlock block, RandomAccessFile file)
        throws IOException
    {
        if(file == null)
            return Boolean.valueOf(false);
        long FPosition = file.getFilePointer();
        block.BlockSize = IntUtils.readInt16(file);
        block.IndexCount = IntUtils.readInt16(file);
        int FRecordLength = (block.BlockSize - 4) / block.IndexCount;
        block.RecordList = new ComplexIndexRecord[block.IndexCount];
        FPosition += 4L;
        for(int dwI = 0; dwI < block.IndexCount; dwI++)
        {
            block.RecordList[dwI] = new ComplexIndexRecord();
            file.seek(FPosition + (long)(dwI * FRecordLength));
            ComplexIndexRecord.Read(block.RecordList[dwI], file);
        }

        return Boolean.valueOf(true);
    }

    public int BlockSize;
    public int IndexCount;
    public ComplexIndexRecord RecordList[];
}

package com.haoyu.module.jcstruct.read;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class JDataInputLitter implements JDataInput
{
	private DataInputStream dataInputStream;
	private byte w[];

	public JDataInputLitter(byte[] data)
	{
		dataInputStream = new DataInputStream(new ByteArrayInputStream(data));
		w = new byte[8];
	}

	public final long readUnsignedInt() throws IOException
	{
		this.readFully(w, 0, 4);
		return getLong(w, 0);
	}

	public long getLong(byte buf[], int index)
	{
		int firstByte = (0x000000FF & ((int) buf[index]));

		int secondByte = (0x000000FF & ((int) buf[index + 1]));

		int thirdByte = (0x000000FF & ((int) buf[index + 2]));

		int fourthByte = (0x000000FF & ((int) buf[index + 3]));

		return ((long) (fourthByte << 24 | thirdByte << 16 | secondByte << 8 | firstByte)) & 0xFFFFFFFFL;

	}

	public final int readCharC(byte w[]) throws IOException
	{
		if (w[0] < 0) {
			return w[0] + 256;
		}
		return (w[0] & 0xff);
	}

	public void readBooleanArray(boolean buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readBoolean();
	}

	public void readByteArray(byte buffer[]) throws IOException
	{
		this.readFully(buffer);
	}

	public void readCharArray(char buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readChar();
	}

	public void readShortArray(short buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readShort();
	}

	public void readIntArray(int buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readInt();
	}

	public void readLongArray(long buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readLong();
	}

	public void readFloatArray(float buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readFloat();
	}

	public void readDoubleArray(double buffer[]) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readDouble();
	}

	public void readUnShortedArray(int[] buffer) throws IOException
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = readUnsignedShort();
	}

	@Override
	public void readFully(byte[] b) throws IOException
	{
		dataInputStream.readFully(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException
	{
		dataInputStream.readFully(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException
	{
		return dataInputStream.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException
	{
		return dataInputStream.readBoolean();
	}

	@Override
	public byte readByte() throws IOException
	{
		return dataInputStream.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException
	{
		return dataInputStream.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException
	{
		dataInputStream.readFully(w, 0, 2);
		return (short) ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	@Override
	public int readUnsignedShort() throws IOException
	{
		dataInputStream.readFully(w, 0, 2);
		return ((w[1] & 0xff) << 8 | (w[0] & 0xff));
	}

	@Override
	public char readChar() throws IOException
	{
		return dataInputStream.readChar();
	}

	@Override
	public int readInt() throws IOException
	{
		dataInputStream.readFully(w, 0, 4);
		return (w[3]) << 24 | (w[2] & 0xff) << 16 | (w[1] & 0xff) << 8 | (w[0] & 0xff);
	}

	@Override
	public long readLong() throws IOException
	{
		dataInputStream.readFully(w, 0, 8);
		return (long) (w[7]) << 56
				| /* long cast needed or shift done modulo 32 */
				(long) (w[6] & 0xff) << 48 | (long) (w[5] & 0xff) << 40 | (long) (w[4] & 0xff) << 32 | (long) (w[3] & 0xff) << 24 | (long) (w[2] & 0xff) << 16 | (long) (w[1] & 0xff) << 8 | (long) (w[0] & 0xff);
	}

	@Override
	public float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public String readLine() throws IOException
	{
		return dataInputStream.readLine();
	}

	@Override
	public String readUTF() throws IOException
	{
		return dataInputStream.readUTF();
	}

	@Override
	public int readCharC() throws IOException
	{
		readFully(w);
		if (w[0] < 0) {
			return w[0] + 256;
		}
		return (w[0] & 0xff);
	}

	@Override
	public void close() throws IOException
	{
		dataInputStream.close();
	}

}

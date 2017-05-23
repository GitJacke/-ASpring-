package com.haoyu.module.jcstruct.writer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class JDataOutputLitter implements JDataOutput
{
	private ByteArrayOutputStream bos;

	protected DataOutput dataOutput;

	private byte w[];

	public JDataOutputLitter()
	{

		bos = new ByteArrayOutputStream();

		dataOutput = new DataOutputStream(bos);

		w = new byte[8];
	}

	public byte[] pack()
	{
		return bos.toByteArray();
	}

	@Override
	public void write(int b) throws IOException
	{
		dataOutput.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		dataOutput.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		dataOutput.write(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException
	{
		dataOutput.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException
	{
		dataOutput.writeByte(v);
	}

	@Override
	public void writeShort(int v) throws IOException
	{
		w[0] = (byte) v;
		w[1] = (byte) (v >> 8);
		dataOutput.write(w, 0, 2);
	}

	@Override
	public void writeChar(int v) throws IOException
	{
		dataOutput.writeChar(v);
	}

	@Override
	public void writeInt(int v) throws IOException
	{
		w[0] = (byte) v;
		w[1] = (byte) (v >> 8);
		w[2] = (byte) (v >> 16);
		w[3] = (byte) (v >> 24);
		dataOutput.write(w, 0, 4);

	}

	@Override
	public void writeLong(long v) throws IOException
	{
		w[0] = (byte) v;
		w[1] = (byte) (v >> 8);
		w[2] = (byte) (v >> 16);
		w[3] = (byte) (v >> 24);
		w[4] = (byte) (v >> 32);
		w[5] = (byte) (v >> 40);
		w[6] = (byte) (v >> 48);
		w[7] = (byte) (v >> 56);
		dataOutput.write(w, 0, 8);
	}

	@Override
	public void writeFloat(float v) throws IOException
	{
		writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(double v) throws IOException
	{
		writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeBytes(String s) throws IOException
	{
		dataOutput.writeBytes(s);
	}

	@Override
	public void writeChars(String s) throws IOException
	{
		dataOutput.writeChars(s);
	}

	@Override
	public void writeUTF(String s) throws IOException
	{
		dataOutput.writeUTF(s);
	}

	@Override
	public void writeBooleanArray(boolean[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			dataOutput.writeBoolean(buffer[i]);

	}

	@Override
	public void writeByteArray(byte[] buffer, int len) throws IOException
	{
		if (len == 0) {
			return;
		}
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		dataOutput.write(buffer, 0, len);

	}

	@Override
	public void writeCharArray(char[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			dataOutput.writeChar(buffer[i]);

	}

	@Override
	public void writeShortArray(short[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			writeShort(buffer[i]);

	}

	@Override
	public void writeIntArray(int[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			dataOutput.writeInt(buffer[i]);

	}

	@Override
	public void writeLongArray(long[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			dataOutput.writeLong(buffer[i]);

	}

	@Override
	public void writeFloatArray(float[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			writeFloat(buffer[i]);

	}

	@Override
	public void writeDoubleArray(double[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			writeDouble(buffer[i]);

	}

	@Override
	public void writeUshortArray(int[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			writeShort((short) buffer[i]);

	}

	@Override
	public void writeUbyteArray(int[] buffer, int len) throws IOException
	{
		if (len == -1 || len > buffer.length)
			len = buffer.length;
		for (int i = 0; i < len; i++)
			writeByte((byte) buffer[i]);

	}

	@Override
	public void writeUbyte(int value) throws IOException
	{
		dataOutput.writeByte((byte) value);

	}

	@Override
	public void writeUshort(int value) throws IOException
	{
		writeShort((short) value);
	}

	@Override
	public void writeUint(long v) throws IOException
	{
		writeInt((int) v);

	}

	@Override
	public void writeByteArray(byte[] buffer) throws IOException
	{
		dataOutput.write(buffer, 0, buffer.length);
	}

	public void close() throws IOException
	{
		bos.close();
	}

}

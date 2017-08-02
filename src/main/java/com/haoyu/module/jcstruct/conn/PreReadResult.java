package com.haoyu.module.jcstruct.conn;

import java.util.List;

public class PreReadResult
{

	public List<byte[]> data;
	
	public boolean isSkip = true;

	public List<byte[]> getData()
	{
		return data;
	}

	public void setData(List<byte[]> data)
	{
		this.data = data;
	}

	public boolean isSkip()
	{
		return isSkip;
	}

	public void setSkip(boolean isSkip)
	{
		this.isSkip = isSkip;
	}
	
	
}

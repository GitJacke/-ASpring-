package com.haoyu.module.jcstruct.interceptor;

import com.alibaba.fastjson.JSONObject;

public final class MappedCheckInterceptor implements CheckInterceptor
{

	private final String[] includePatterns;

	private final CheckInterceptor checkInterceptor;

	public MappedCheckInterceptor(String[] includePatterns, CheckInterceptor checkInterceptor)
	{
		this.includePatterns = includePatterns;
		this.checkInterceptor = checkInterceptor;
	}

	public String[] getIncludePatterns()
	{
		return includePatterns;
	}

	public CheckInterceptor getCheckInterceptor()
	{
		return checkInterceptor;
	}

	@Override
	public boolean checkAfter(JSONObject message) throws Exception
	{
		return checkInterceptor.checkAfter(message);
	}

	@Override
	public byte[] checkAfterError(JSONObject message) throws Exception
	{
		return checkInterceptor.checkAfterError(message);
	}

	@Override
	public boolean checkBefore(byte[] orgData) throws Exception
	{
		return checkInterceptor.checkBefore(orgData);
	}

	@Override
	public byte[] checkBeforeError(byte[] orgData) throws Exception
	{
		return checkInterceptor.checkBeforeError(orgData);
	}

}

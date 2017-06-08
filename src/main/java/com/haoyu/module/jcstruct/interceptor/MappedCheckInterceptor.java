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
	public boolean check(JSONObject message) throws Exception
	{
		return checkInterceptor.check(message);
	}

	@Override
	public byte[] dealError(JSONObject message) throws Exception
	{
		return checkInterceptor.dealError(message);
	}

}

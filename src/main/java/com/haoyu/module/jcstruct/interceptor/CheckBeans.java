package com.haoyu.module.jcstruct.interceptor;

import java.util.Map;

public final class CheckBeans
{

	final private Map<String, CheckInterceptor[]> checkInterceptorMapping;
	final private CheckInterceptor[] globalCheck;
	
	public CheckBeans(CheckInterceptor[] globalCheck,Map<String, CheckInterceptor[]> checkInterceptorMapping){
		this.globalCheck = globalCheck;
		this.checkInterceptorMapping = checkInterceptorMapping;
	}

	public Map<String, CheckInterceptor[]> getCheckInterceptorMapping()
	{
		return checkInterceptorMapping;
	}

	public CheckInterceptor[] getGlobalCheck()
	{
		return globalCheck;
	}
	
}

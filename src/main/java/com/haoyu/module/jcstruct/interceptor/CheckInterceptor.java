package com.haoyu.module.jcstruct.interceptor;

import com.alibaba.fastjson.JSONObject;

public interface CheckInterceptor
{
	
	public boolean checkBefore(byte[] orgData) throws Exception;
	
	public byte[] checkBeforeError(byte[] orgData) throws Exception;
	
	
	public boolean checkAfter(JSONObject message) throws Exception;
	
	//处理失败的返回
	public byte[] checkAfterError(JSONObject message) throws Exception;
}

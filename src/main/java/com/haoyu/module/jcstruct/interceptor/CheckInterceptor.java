package com.haoyu.module.jcstruct.interceptor;

import com.alibaba.fastjson.JSONObject;

public interface CheckInterceptor
{
	public boolean check(JSONObject message) throws Exception;
	
	//处理失败的返回
	public byte[] dealError(JSONObject message) throws Exception;
}

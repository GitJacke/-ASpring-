package com.haoyu.module.jcstruct.test.handle;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.haoyu.module.jcstruct.interceptor.CheckInterceptor;
import com.haoyu.module.jcstruct.resolve.Resolve;

public class SgckBccCheck implements CheckInterceptor
{
	final Logger LOG = Logger.getLogger(this.getClass());

	@Autowired
	private Resolve resolve;

	private String[] excludeFileNames;

	public void setExcludeFileNames(String[] excludeFileNames)
	{
		this.excludeFileNames = excludeFileNames;
	}

	@Override
	public boolean check(JSONObject message) throws Exception
	{
		byte bcc = computeBcc(message);
		byte oldBcc = message.getByteValue("BCC");
		return bcc == oldBcc;
	}

	private byte computeBcc(JSONObject message)
	{
		Iterator<Entry<String, Object>> iterator = message.entrySet().iterator();
		Entry<String, Object> entry = null;
		byte bcc = 0;
		while (iterator.hasNext()) {
			entry = iterator.next();
			Object value = entry.getValue();
			if (isValid(excludeFileNames, entry.getKey())) {
				if (value instanceof Number) {
					// 数据处理
					bcc = dealLong(bcc, Long.parseLong(value.toString()));
				} else if (value instanceof byte[]) {
					// 数据处理
					byte[] wave = (byte[]) value;
					byte waveInt = dealByteArray(wave);
					bcc = (byte) (bcc ^ waveInt);
				}
			}
		}
		return bcc;
	}

	public byte dealByteArray(byte[] wave)
	{
		byte old = 0;
		for (byte b : wave) {
			old = (byte) (old ^ b);
		}
		return old;
	}

	public boolean isValid(String key)
	{
		return !("Constant_Up_Stop".equals(key) || "BCC".equals(key));
	}

	public boolean isValid(String[] excludeFileNames, String key)
	{
		if (null != excludeFileNames) {
			for (String fileName : excludeFileNames) {
				if (fileName.equals(key)) {
					return false;
				}
			}
		}
		return true;
	}

	public byte dealLong(byte bcc, long v)
	{

		bcc = (byte) (bcc ^ (byte) v);
		bcc = (byte) (bcc ^ (byte) (v >> 8));
		bcc = (byte) (bcc ^ (byte) (v >> 16));
		bcc = (byte) (bcc ^ (byte) (v >> 24));

		bcc = (byte) (bcc ^ (byte) (v >> 32));
		bcc = (byte) (bcc ^ (byte) (v >> 40));
		bcc = (byte) (bcc ^ (byte) (v >> 48));
		bcc = (byte) (bcc ^ (byte) (v >> 56));

		return bcc;
	}

	@Override
	public byte[] dealError(JSONObject message) throws Exception
	{
		LOG.error("校验BCC失败!当前解析内容:" + message);

		// 发送确认包
		JSONObject config = new JSONObject();

		if (!message.containsKey("Package_Number") || null == message.get("Package_Number")) {
			config.put("Package_Number", 0);
		} else {
			config.put("Package_Number", message.get("Package_Number"));
		}

		config.put("Gateway_Id", message.get("Gateway_Id"));

		config.put("command_properties", RtnCommandProperties.ERROR_NOT_RETRY.ID);

		return resolve.unresolve(RtnCodeType.Server_Gateway_ACK.ID, config);
	}

}

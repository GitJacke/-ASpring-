package com.haoyu.module.jcstruct.test.handle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.haoyu.module.jcstruct.interceptor.CheckInterceptor;
import com.haoyu.module.jcstruct.model.SimpleValueRule;
import com.haoyu.module.jcstruct.resolve.Resolve;
import com.haoyu.module.jcstruct.utils.MatchUtils;

/**
 * 重新计算值的拦截器
 * 
 * @author DELL
 *
 */
public class SgckReComputeValueInterceptor implements CheckInterceptor, InitializingBean
{
	final Logger LOG = Logger.getLogger(this.getClass());

	@Autowired
	private Resolve resolve;

	// 规则配置
	private Map<String, String> rules;

	public void setRules(Map<String, String> rules)
	{
		this.rules = rules;
	}

	private Map<String, SimpleValueRule> realRules;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		realRules = new HashMap<>();
		if (!CollectionUtils.isEmpty(rules)) {
			Iterator<Entry<String, String>> iterator = rules.entrySet().iterator();
			Entry<String, String> entry = null;
			while (iterator.hasNext()) {
				entry = iterator.next();
				SimpleValueRule rule = MatchUtils.matchValue(entry.getValue(), true);
				if (null != rule) {
					realRules.put(entry.getKey(), rule);
				}
			}
		}

	}

	@Override
	public boolean checkAfter(JSONObject message) throws Exception
	{
		// 只是为了改变值
		// realValueRule="{*}{0.01}"
		if (!CollectionUtils.isEmpty(realRules)) {
			Iterator<Entry<String, SimpleValueRule>> iterator = realRules.entrySet().iterator();
			Entry<String, SimpleValueRule> entry = null;
			while (iterator.hasNext()) {
				entry = iterator.next();
				if (message.containsKey(entry.getKey())) {
					Double old = message.getDoubleValue(entry.getKey());
					Number realValue = entry.getValue().computer(old);
					message.put(entry.getKey(), realValue);
				}
			}
		}

		return true;
	}

	@Override
	public byte[] checkAfterError(JSONObject message) throws Exception
	{
		LOG.error("温度值重新赋值失败!" + message);

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

	@Override
	public boolean checkBefore(byte[] orgData) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] checkBeforeError(byte[] orgData) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}

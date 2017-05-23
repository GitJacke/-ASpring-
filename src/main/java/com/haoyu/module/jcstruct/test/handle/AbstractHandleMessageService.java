package com.haoyu.module.jcstruct.test.handle;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.haoyu.module.jcstruct.model.DefaultResponseResult;
import com.haoyu.module.jcstruct.template.TemplateContainer;


public abstract class AbstractHandleMessageService
{

	protected int DEFAULT_PACKAGE_NUM = 0;
	
	private int MAX_PACKAGE_NUM = 65535;
	
	@Autowired
	private TemplateContainer templateContainer;
	
	public String getTcPrimaryKey(){
		return templateContainer.getTcHeader().getKey();
	}

	
	public void buildDefault(JSONObject message, DefaultResponseResult responseResult, RtnCommandProperties commandProperties)
	{
		// 发送确认包
		JSONObject config = new JSONObject();

		if (!message.containsKey("Package_Number") || null == message.get("Package_Number")) {
			config.put("Package_Number", DEFAULT_PACKAGE_NUM);
		} else {
			config.put("Package_Number", message.get("Package_Number"));
		}

		config.put("Gateway_Id", message.get("Gateway_Id"));

		config.put("command_properties", commandProperties.ID);

		responseResult.buildReturnData(config).buildId(RtnCodeType.Server_Gateway_ACK.ID);

	}

	public void buildDefaultSuccess(JSONObject message, DefaultResponseResult responseResult)
	{
		// 发送确认包
		buildDefault(message, responseResult, RtnCommandProperties.SUCCESS);

	}

	public void buildDefaultFail(JSONObject message, DefaultResponseResult responseResult)
	{

		buildDefault(message, responseResult, RtnCommandProperties.ERROR_NOT_RETRY);

	}

	public void buildDefaultRetry(JSONObject message, DefaultResponseResult responseResult)
	{
		buildDefault(message, responseResult, RtnCommandProperties.ERROR_AND_RETRY);

	}

}

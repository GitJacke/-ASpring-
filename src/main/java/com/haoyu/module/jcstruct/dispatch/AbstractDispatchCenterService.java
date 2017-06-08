package com.haoyu.module.jcstruct.dispatch;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.haoyu.module.jcstruct.annotation.HandleMessageProtocol;
import com.haoyu.module.jcstruct.handle.HandleMessageService;
import com.haoyu.module.jcstruct.handle.HandleMessageServiceProxy;
import com.haoyu.module.jcstruct.model.DefaultResponseResult;
import com.haoyu.module.jcstruct.model.ResponseResult;
import com.haoyu.module.jcstruct.refresh.RefreshService;

abstract public class AbstractDispatchCenterService implements DispatchCenterService,RefreshService
{

	private BiMap<String, HandleMessageService> handleMessageServiceMapping;
	
	//BiMap<String,String> weekNameMap = HashBiMap.create();

	protected HandleMessageService getHandleMessageService(String id)
	{
		return handleMessageServiceMapping.get(id);
	}

	protected ResponseResult handle(HandleMessageService service, JSONObject message)
	{
		DefaultResponseResult response = new DefaultResponseResult();
		service.handle(message,response);
		return response;
	}

	private synchronized void addHandleMessageService(String id, HandleMessageService bean)
	{
		if (handleMessageServiceMapping.containsKey(id)) {
			throw new IllegalArgumentException("协议号:" + id + ",被重复定义请确认!");
		}

		handleMessageServiceMapping.put(id, bean);
	}

	public void refresh(ApplicationContext applicationContext)
	{
		handleMessageServiceMapping = HashBiMap.create();

		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HandleMessageProtocol.class);

		for (Object bean : beans.values()) {

			if (!(bean instanceof HandleMessageService)) {
				continue;
			}

			HandleMessageProtocol tmp = null;

			tmp = bean.getClass().getAnnotation(HandleMessageProtocol.class);

			addHandleMessageService(tmp.id(), (HandleMessageService) bean);

		}
		
		//处理代理类
		Map<String,HandleMessageServiceProxy> proxys = applicationContext.getBeansOfType(HandleMessageServiceProxy.class);
		if(!CollectionUtils.isEmpty(proxys)){
			for (HandleMessageServiceProxy bean : proxys.values()) {
				//获取target类
				HandleMessageService target = bean.getTarget();
				String protocolId = handleMessageServiceMapping.inverse().get(target);
				if(null != protocolId){
					//做替换
					handleMessageServiceMapping.put(protocolId, bean);
				}
			}
		}
		
	}

}

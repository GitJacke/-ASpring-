package com.haoyu.module.jcstruct.test.handle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.haoyu.module.jcstruct.annotation.HandleMessageProtocol;
import com.haoyu.module.jcstruct.handle.HandleMessageService;
import com.haoyu.module.jcstruct.model.DefaultResponseResult;

/**
 * 处理心跳包
 * 
 * @author DELL
 *
 */
@Service("handleHeartBeatMessage")
@HandleMessageProtocol(id = GatewayServerCodeType.Gateway_Server_HeartBeat, response = true)
public class HandleHeartBeatMessage extends AbstractHandleMessageService implements HandleMessageService
{

	Logger LOG = Logger.getLogger(this.getClass());


	@Override
	public void handle(JSONObject message, DefaultResponseResult responseResult)
	{

		//LOG.info("接收心跳信息->" + message);
		System.out.println("接收心跳信息->" + message);
		// 首先获取当前要下发的信息
		buildDefaultSuccess(message, responseResult);

	}

}

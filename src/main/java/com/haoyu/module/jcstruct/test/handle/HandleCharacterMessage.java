package com.haoyu.module.jcstruct.test.handle;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.haoyu.module.jcstruct.annotation.HandleMessageProtocol;
import com.haoyu.module.jcstruct.handle.HandleMessageService;
import com.haoyu.module.jcstruct.model.DefaultResponseResult;

/**
 * 处理特征值显示
 * 
 * @author DELL
 *
 */
@Service("handleCharacterMessage")
@HandleMessageProtocol(id = GatewayServerCodeType.Gateway_Server_Character)
public class HandleCharacterMessage extends AbstractHandleMessageService implements HandleMessageService
{
	Logger LOG = Logger.getLogger(this.getClass());

	// @Autowired
	// private ReceiveDataService receiveDataService;

	@Override
	public void handle(JSONObject message, DefaultResponseResult responseResult)
	{
		//LOG.info("接收特征值信息:" + message);
		System.out.println("接收特征值信息:" + message);
		// 往趋势图加入数据
		try {
			String Sensor_Id = message.getLong("Sensor_Id").toString();

			// 获取温度
			float Temperature = message.getFloatValue("Temperature");

			// 获取电量
			int Battery = message.getIntValue("Battery");

			// 接收端数据= 数据/Data_coefficient
			float Data_coefficient = message.getIntValue("Data_coefficient");

			float Data_x_Rms = message.getIntValue("Data_x_Rms") / Data_coefficient;
			float Data_x_PP = message.getIntValue("Data_x_PP") / Data_coefficient;
			float Data_x_P = message.getIntValue("Data_x_P") / Data_coefficient;

			float Data_y_Rms = message.getIntValue("Data_y_Rms") / Data_coefficient;
			float Data_y_PP = message.getIntValue("Data_y_PP") / Data_coefficient;
			float Data_y_P = message.getIntValue("Data_y_P") / Data_coefficient;

			float Data_z_Rms = message.getIntValue("Data_z_Rms") / Data_coefficient;
			float Data_z_PP = message.getIntValue("Data_z_PP") / Data_coefficient;
			float Data_z_P = message.getIntValue("Data_z_P") / Data_coefficient;

			JSONObject receiveData = new JSONObject();

			receiveData.put("temperature", Temperature);

			receiveData.put("battery", Battery);

			receiveData.put("datatime", System.currentTimeMillis());

			LOG.info("特征值上传时间:mills:" + receiveData.get("datatime") + "->times:" + new Date());

			// 用于判断是否为速度、加速度
			int type = message.getIntValue("Character_Attribute");

			receiveData.put("dataType", type);

			if (type == 1) {
				// 加速度
				receiveData.put("xValue", Data_x_P);
				receiveData.put("yValue", Data_y_P);
				receiveData.put("zValue", Data_z_P);

			} else {
				// 速度

				receiveData.put("xValue", Data_x_Rms);
				receiveData.put("yValue", Data_y_Rms);
				receiveData.put("zValue", Data_z_Rms);
			}

			// 调用接口
			//receiveDataService.receiveFromSimulative(ReceiveDataService.HIS_DATA, Sensor_Id, receiveData);
			System.out.println("处理解析特征值上传信息成功，待上传的数据信息为" + receiveData);

			buildDefaultSuccess(message, responseResult);
		} catch (Exception e) {
			LOG.error("处理接收后的特征值信息接口异常:" + e.getMessage(), e);
			buildDefaultFail(message, responseResult);
		}

	}

}

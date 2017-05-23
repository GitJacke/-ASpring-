package com.haoyu.module.jcstruct.test.handle;

public final class GatewayServerCodeType
{

	public static final String Gateway_Server_ACK = "1";// "通信确认",
	public static final String Gateway_Server_Character = "2";// "特征值上传给服务器（0x02)",
	public static final String Gateway_Server_Sensor = "3";// "传感器组网信息上传（0X03）",
	public static final String Gateway_Server_Wave_Start = "4_4";// "申请波形上传",
	public static final String Gateway_Server_Wave_Next = "4_5";// "波形中间包",
	public static final String Gateway_Server_Wave_End = "4_6";// "波形结束包",
	public static final String Gateway_Server_HeartBeat = "5";// "心跳包（0X05）",

}

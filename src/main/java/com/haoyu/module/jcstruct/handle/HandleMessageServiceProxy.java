package com.haoyu.module.jcstruct.handle;

import com.haoyu.module.jcstruct.handle.HandleMessageService;

/**
 * 代理类，供子业务自己扩展实现
 * 如果一个协议处理同时存在代理类和一般类，则会让代理类生效
 * @author DELL
 *
 */
public abstract class HandleMessageServiceProxy implements HandleMessageService
{

	private HandleMessageService target;

	public void setTarget(HandleMessageService target)
	{
		this.target = target;
	}

	public HandleMessageService getTarget()
	{
		return target;
	}

}

package com.haoyu.module.jcstruct.conn;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.haoyu.module.jcstruct.common.SystemConsts;
import com.haoyu.module.jcstruct.dispatch.DispatchCenterService;
import com.haoyu.module.jcstruct.interceptor.CheckBeans;
import com.haoyu.module.jcstruct.resolve.DefaultResolve;

public class SocketConnectionImpl extends SocketConnection
{

	// 能将解析头之后的数据流发送到下一个环节

	public SocketConnectionImpl(Socket socket, DispatchCenterService dispatchCenterService, DefaultResolve defaultResolve, CheckBeans checkBeans)
	{
		super(socket, dispatchCenterService, defaultResolve, checkBeans);
	}

	public PreReadResult preRead() throws IOException
	{

		byte[] data = new byte[SystemConsts.max_read];

		int count = this.socket.getInputStream().read(data);

		if (count == -1) {
			throw new SocketException();
		}

		return setPreReadSuccess(data);
	}
	
	@Override
	public boolean checkServerClose()
	{
		return isStop;
	}

}

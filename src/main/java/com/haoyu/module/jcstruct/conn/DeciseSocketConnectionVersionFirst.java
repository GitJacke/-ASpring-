package com.haoyu.module.jcstruct.conn;

import java.net.Socket;

import com.haoyu.module.jcstruct.dispatch.DispatchCenterService;
import com.haoyu.module.jcstruct.resolve.DefaultResolve;

public class DeciseSocketConnectionVersionFirst extends DeciseSocketConnection
{

	@Override
	public Connection decise(Socket t, DispatchCenterService disCenterService, DefaultResolve resovle)
	{
		Connection connection = new SocketConnectionVersionFirst(t, disCenterService, resovle, getCheckBeans());
		return connection;
	}

}

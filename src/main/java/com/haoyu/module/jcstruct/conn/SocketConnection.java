package com.haoyu.module.jcstruct.conn;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.haoyu.module.jcstruct.common.SystemConsts;
import com.haoyu.module.jcstruct.dispatch.DispatchCenterService;
import com.haoyu.module.jcstruct.exception.DtuMessageException;
import com.haoyu.module.jcstruct.interceptor.CheckBeans;
import com.haoyu.module.jcstruct.interceptor.CheckInterceptor;
import com.haoyu.module.jcstruct.model.ResolveResult;
import com.haoyu.module.jcstruct.model.ResponseResult;
import com.haoyu.module.jcstruct.resolve.DefaultResolve;
import com.haoyu.module.jcstruct.utils.HexUtils;

public abstract class SocketConnection extends Thread implements Connection
{
	// 能将解析头之后的数据流发送到下一个环节
	final protected DefaultResolve defaultResolve;

	protected volatile boolean isStop;

	final protected Socket socket;

	final protected DispatchCenterService dispatchCenterService;

	final protected Logger LOG = Logger.getLogger(this.getClass());

	final private CheckBeans checkBeans;

	public abstract PreReadResult preRead() throws IOException;

	public abstract boolean existsMultiple(int len);

	protected long lastReceiveTime;// 上一次接受数据时间

	public SocketConnection(Socket socket, DispatchCenterService dispatchCenterService, DefaultResolve defaultResolve, CheckBeans checkBeans)
	{
		this.socket = socket;
		this.dispatchCenterService = dispatchCenterService;
		this.defaultResolve = defaultResolve;
		this.checkBeans = checkBeans;
		this.lastReceiveTime = System.currentTimeMillis();// 初始化接收时间
	}

	protected PreReadResult setPreReadSuccess(byte[] data)
	{
		LOG.info("原始包16进制:" + HexUtils.bytesToHexString(data));
		PreReadResult result = new PreReadResult();
		result.setSkip(false);
		result.setData(multiplePackages(data));
		return result;
	}

	private List<byte[]> multiplePackages(byte[] data)
	{
		if (!existsMultiple(data.length)) {
			return Lists.newArrayList(data);
		}
		// 解析多个包
		int len = data.length;
		int step = 1;
		int start = 0;
		List<byte[]> packages = new ArrayList<>();

		for (int i = 0; i < len - 1; i = i + step) {
			// 决定跳一格还是两格
			if (data[i] == SystemConsts.foot[0]) {
				// 找到一个
				if (data[i + 1] == SystemConsts.foot[1]) {
					// 找到完整的
					byte[] temp = new byte[i + 2 - start];
					System.arraycopy(data, start, temp, start, temp.length);
					start = i + 2;
					packages.add(temp);
				} else {
					// 看是否需要跳
					if (data[i + 1] != SystemConsts.foot[0]) {
						step = 2;// 跳两个格子
					}
				}
			} else {
				//
				if (data[i + 1] != SystemConsts.foot[0]) {
					step = 2;// 跳两个格子
				}

			}

		}

		return packages;

	}

	protected PreReadResult setPreReadSkip()
	{
		PreReadResult result = new PreReadResult();
		result.setSkip(true);
		return result;
	}

	@Override
	public void startWork()
	{
		this.start();
		LOG.info("客户机:" + getConnectionKey() + ",连接成功!");
	}

	@Override
	public void stopWork()
	{
		isStop = true;
	}

	@Override
	public void run()
	{
		lastReceiveTime = System.currentTimeMillis();// 第一次接入加入时间
		while (!isStop) {
			try {
				// 预读
				PreReadResult preReadResult = preRead();// 会有IO阻塞
				
				lastReceiveTime = System.currentTimeMillis();// 重置接收时间

				if (!preReadResult.isSkip()) {

					List<byte[]> packages = preReadResult.getData();
					if(null != packages){
						int size = packages.size();
						if(size > 1){
							LOG.info("本次存在"+size+"包完整数据！");
						}
						for(byte[] data:packages){
							consumeData(data);
						}
						
						packages = null;
					}

				}

			} catch (SocketException e) {
				// 断开连接释放资源
				LOG.error(e.getMessage(), e);
				breakConnection(e);
			} catch (DtuMessageException e) {
				LOG.error(e.getMessage(), e);
				if (e.isNotOnline()) {
					breakConnection(e);
				}
			} catch (EOFException e) {
				LOG.error(e.getMessage(), e);
				// breakConnection(e);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

	}

	private void consumeData(byte[] data) throws DtuMessageException, IOException, Exception
	{
		long start = System.currentTimeMillis();

		if (checkBefore(data)) {

			ResolveResult<JSONObject> resolveResult = defaultResolve.resolve(data, null);

			if (checkAfter(resolveResult)) {
				// 进行数据处理
				ResponseResult result = dispatchCenterService.handle(resolveResult.getId(), resolveResult.getResult());
				// 判断是否需要返回
				if (null != result.getData()) {
					// 返回
					sendMessage(result.getId(), result.getData());
				}

				LOG.info("本次处理协议:" + resolveResult.getId() + "成功！时间:" + (System.currentTimeMillis() - start) + "ms");
			}
		}
	}

	private boolean checkBefore(byte[] orgData) throws Exception
	{
		if (ArrayUtils.isNotEmpty(checkBeans.getGlobalCheck())) {
			if (!checkArrayBefore(checkBeans.getGlobalCheck(), orgData)) {
				return false;
			}
		}

		return true;
	}

	private boolean checkArrayBefore(CheckInterceptor[] checks, byte[] orgData) throws DtuMessageException, IOException, Exception
	{
		for (CheckInterceptor interceptor : checks) {
			if (!checkOneBefore(interceptor, orgData)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkOneBefore(CheckInterceptor interceptor, byte[] orgData) throws DtuMessageException, IOException, Exception
	{
		if (!interceptor.checkBefore(orgData)) {
			// 失败处理
			byte[] sendData = interceptor.checkBeforeError(orgData);
			sendMessage(sendData);
			return false;
		}
		return true;
	}

	private boolean checkAfter(ResolveResult<JSONObject> resolveResult) throws Exception
	{
		// List<CheckInterceptor> matchs = new ArrayList<>();

		if (ArrayUtils.isNotEmpty(checkBeans.getGlobalCheck())) {
			if (!checkArrayAfter(checkBeans.getGlobalCheck(), resolveResult)) {
				return false;
			}
		}

		if (checkBeans.getCheckInterceptorMapping().containsKey(resolveResult.getId())) {
			CheckInterceptor[] checks = checkBeans.getCheckInterceptorMapping().get(resolveResult.getId());
			if (ArrayUtils.isNotEmpty(checks)) {
				return checkArrayAfter(checks, resolveResult);
			}
		}

		return true;
	}

	private boolean checkArrayAfter(CheckInterceptor[] checks, ResolveResult<JSONObject> resolveResult) throws DtuMessageException, IOException, Exception
	{
		for (CheckInterceptor interceptor : checks) {
			if (!checkOneAfter(interceptor, resolveResult)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkOneAfter(CheckInterceptor interceptor, ResolveResult<JSONObject> resolveResult) throws DtuMessageException, IOException, Exception
	{
		if (!interceptor.checkAfter(resolveResult.getResult())) {
			// 失败处理
			byte[] sendData = interceptor.checkAfterError(resolveResult.getResult());
			sendMessage(sendData);
			return false;
		}
		return true;
	}

	private void breakConnection(Exception e)
	{
		LOG.info(getConnectionKey() + ",已经断开连接!");
		try {
			this.socket.close();
			stopWork();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String getConnectionKey()
	{
		return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
	}

	public void sendMessage(String messageId, JSONObject message) throws DtuMessageException, IOException
	{
		byte[] sendData = defaultResolve.unresolve(messageId, message);
		sendMessage(sendData);
	}

	public void sendMessage(byte[] bytes) throws DtuMessageException, IOException
	{
		if (SystemConsts.isDebug) {
			LOG.info("本次服务器回应网关信息:" + HexUtils.bytesToHexString(bytes));
		}
		this.socket.getOutputStream().write(bytes);
	}

	public boolean checkServerClose()
	{
		// try {
		// socket.sendUrgentData(0xFF);//
		// 发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
		// return false;
		// } catch (Exception se) {
		// stopWork();
		// return true;
		// }

		// 通过判断最后一次响应时间和现在时间做对比，检测不在线
		if (lastReceiveTime < System.currentTimeMillis() - SystemConsts.interval) {
			stopWork();
			return true;
		} else {
			return false;
		}
	}

}

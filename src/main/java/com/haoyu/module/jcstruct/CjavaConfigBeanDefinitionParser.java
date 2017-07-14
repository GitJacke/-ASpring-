package com.haoyu.module.jcstruct;

import java.nio.ByteOrder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.haoyu.module.jcstruct.common.SystemConsts;
import com.haoyu.module.jcstruct.utils.HexUtils;

/**
 * 解析标签config实现
 * 
 * @author DELL
 *
 */
public class CjavaConfigBeanDefinitionParser extends CjavaAbstractSingleBeanDefinitionParser
{

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder)
	{
		// 解析标签
		NamedNodeMap attrs = element.getAttributes();
		if (null != attrs) {
			int length = attrs.getLength();
			for (int i = 0; i < length; i++) {
				attrs.item(i).getNodeValue();
				// Element el = (Element) attrs.item(i);
				Node node = attrs.item(i);
				doAttr(node.getNodeName(), node.getNodeValue());
			}
		}

	}

	private void doAttr(String tag, String value)
	{

		if (tag.equals("isDebug")) {
			SystemConsts.isDebug = Boolean.parseBoolean(value);
		} else if (tag.equals("order")) {
			Integer orderInt = Integer.parseInt(value);
			if (orderInt.intValue() == 0) {
				SystemConsts.order = ByteOrder.LITTLE_ENDIAN;
			} else {
				SystemConsts.order = ByteOrder.BIG_ENDIAN;
			}
		} else if (tag.equals("port")) {
			Integer port = Integer.parseInt(value);
			SystemConsts.port = port.intValue();
		} else if (tag.equals("maxConnections")) {
			Integer maxConnections = Integer.parseInt(value);
			SystemConsts.maxConnections = maxConnections.intValue();
		} else if (tag.equals("headSign")) {
			SystemConsts.head = HexUtils.hexStringToBytes(value);
		} else if (tag.equals("footSign")) {
			SystemConsts.foot = HexUtils.hexStringToBytes(value);
		} else if (tag.equals("maxReadTemp")) {
			Integer maxReadTemp = Integer.parseInt(value);
			SystemConsts.max_read_version_one = maxReadTemp.intValue();
		} else if (tag.equals("maxRead")) {
			Integer maxRead = Integer.parseInt(value);
			SystemConsts.max_read = maxRead.intValue();
		} else if (tag.equals("maxRetryTimes")) {
			Integer maxRetryTimes = Integer.parseInt(value);
			SystemConsts.maxRetryTimes = maxRetryTimes.intValue();
		}else if (tag.equals("interval")) {
			Integer interval = Integer.parseInt(value);
			SystemConsts.interval = interval.intValue();
		}

	}

	private void doChildParse(Element element)
	{
		check(element);
		String key = element.getAttribute("key");
		String value = element.getAttribute("value");
		if (key.equals("isDebug")) {
			SystemConsts.isDebug = Boolean.parseBoolean(value);
		} else if (key.equals("order")) {
			Integer orderInt = Integer.parseInt(value);
			if (orderInt.intValue() == 0) {
				SystemConsts.order = ByteOrder.LITTLE_ENDIAN;
			} else {
				SystemConsts.order = ByteOrder.BIG_ENDIAN;
			}
		} else if (key.equals("port")) {
			Integer port = Integer.parseInt(value);
			SystemConsts.port = port.intValue();
		} else if (key.equals("maxConnections")) {
			Integer maxConnections = Integer.parseInt(value);
			SystemConsts.maxConnections = maxConnections.intValue();
		} else if (key.equals("headSign")) {
			SystemConsts.head = HexUtils.hexStringToBytes(value);
		} else if (key.equals("footSign")) {
			SystemConsts.foot = HexUtils.hexStringToBytes(value);
		}
	}

	private void check(Element element)
	{

		String key = element.getAttribute("key");
		String value = element.getAttribute("value");
		if (!StringUtils.hasText(key)) {
			throw new IllegalArgumentException("子标签" + element.getNodeName() + "属性key不能为空");
		}
		if (!StringUtils.hasText(value)) {
			throw new IllegalArgumentException("子标签" + element.getNodeName() + "属性value不能为空");
		}
	}
}

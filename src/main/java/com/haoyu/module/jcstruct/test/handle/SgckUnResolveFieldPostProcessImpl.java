package com.haoyu.module.jcstruct.test.handle;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.haoyu.module.jcstruct.model.Field;
import com.haoyu.module.jcstruct.resolve.UnResolveFieldPostProcess;
import com.haoyu.module.jcstruct.writer.JDataOutput;

@Service
public class SgckUnResolveFieldPostProcessImpl implements UnResolveFieldPostProcess
{

	@Override
	public void postProcess(Field currentField, JSONObject message, JDataOutput dataOutput)
	{
		if (!message.containsKey("dealedBcc")) {
			int bcc = 0;
			if (message.containsKey("BCC")) {
				bcc = message.getIntValue("BCC");
			}

			if (currentField.getName().equals("BCC")) {
				// 做默认处理
				message.put("dealedBcc", true);
			} else {
				// 否则必须要有
				Number value = getValue(currentField, message);
				
				bcc = dealBcc(bcc, currentField, value);
				
			}

			message.put("BCC", bcc);
		}

	}

	private int dealBcc(int bcc, Field currentField, Number value)
	{
		switch (currentField.getType())
		{
		case BYTE:
			bcc = bcc ^ value.byteValue();
			break;
		case CHAR:
			bcc = bcc ^ value.byteValue();
			break;
		case SHORT:
			bcc = dealShort(bcc, value.shortValue());
			break;
		case USHORT:
			bcc = dealUShort(bcc, value.intValue());
			break;
		case INT:
			bcc = dealInt(bcc, value.intValue());
			break;
		case UINT:
			bcc = dealInt(bcc, value.intValue());
			break;
		case FLOAT:
			bcc = dealFloat(bcc, value.floatValue());
			break;
		case DOUBLE:
			bcc = dealDouble(bcc, value.doubleValue());
			break;
		default:
			break;
		}

		return bcc;

	}

	private Number getNumberValue(Field field, JSONObject content)
	{
		String fieldName = field.getName();
		Number value = (Number) content.get(fieldName);
		value = (null == value) ? (Number) field.getDefaultValue() : value;
		Assert.notNull(value, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
		return field.getRealValue(value);
	}

	protected Number getValue(Field field, JSONObject content)
	{
		String fieldName = field.getName();
		Object value = content.get(fieldName);
		value = (null == value) ? field.getDefaultValue() : value;
		Assert.notNull(value, "the protocol 's field[ " + fieldName + "] not allow null,please set default value or set current message value");
		return Double.parseDouble(value.toString());
	}

	private int dealDouble(int bcc, double v)
	{
		return dealLong(bcc, Double.doubleToLongBits(v));
	}

	private int dealFloat(int bcc, float v)
	{
		return dealInt(bcc, Float.floatToIntBits(v));
	}

	private int dealLong(int bcc, long v)
	{

		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		bcc = bcc ^ (byte) (v >> 16);
		bcc = bcc ^ (byte) (v >> 24);

		bcc = bcc ^ (byte) (v >> 32);
		bcc = bcc ^ (byte) (v >> 40);
		bcc = bcc ^ (byte) (v >> 48);
		bcc = bcc ^ (byte) (v >> 56);

		return bcc;
	}

	private int dealInt(int bcc, int v)
	{

		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		bcc = bcc ^ (byte) (v >> 16);
		bcc = bcc ^ (byte) (v >> 24);

		return bcc;
	}

	private int dealShort(int bcc, short v)
	{
		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		return bcc;
	}

	private int dealUShort(int bcc, int v)
	{
		bcc = bcc ^ (byte) v;
		bcc = bcc ^ (byte) (v >> 8);
		return bcc;
	}

}

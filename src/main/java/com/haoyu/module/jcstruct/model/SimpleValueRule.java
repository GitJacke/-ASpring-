package com.haoyu.module.jcstruct.model;

public class SimpleValueRule extends SimpleRule
{

	private Number value;

	public Number getValue()
	{
		return value;
	}

	public void setValue(Number value)
	{
		this.value = value;
	}

	public Number computer(Number old)
	{
		return this.getRule().getDoubleValue(old, value);
	}
}

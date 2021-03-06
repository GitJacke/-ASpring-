package com.haoyu.module.jcstruct.rule;

public class SimpleDivideRuleForReceive implements SimpleComputerRule
{

	@Override
	public Number getIntValue(Number old, Number add)
	{
		return (float)(Math.round(old.floatValue() / add.floatValue() * 100))/100;
	}

	@Override
	public Number getLongValue(Number old, Number add)
	{
		return (double)(Math.round(old.floatValue() / add.floatValue() * 100))/100;
	}

	@Override
	public float getFloatValue(Number old, Number add)
	{
		return (float)(Math.round(old.floatValue() / add.floatValue() * 100))/100;
	}

	@Override
	public double getDoubleValue(Number old, Number add)
	{
		return (double)(Math.round(old.floatValue() / add.floatValue() * 100))/100;
	}

}

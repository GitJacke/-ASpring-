package com.haoyu.module.jcstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.haoyu.module.jcstruct.common.SystemConsts;
import com.haoyu.module.jcstruct.utils.HexUtils;

public class TestMatch
{

	public static void main(String[] args)
	{
		List<String> list = getTeacherList("{Package_length}{-}{6}");
		
		System.out.println(list.get(1));
		
		float f1 = 36F;
		
		System.out.println((int)f1);
		
		String head = HexUtils.bytesToHexString(SystemConsts.head);

		
		String foot = HexUtils.bytesToHexString(SystemConsts.foot);
		
		System.out.println(head);
		System.out.println(foot);
		
		byte[] headbyte = HexUtils.hexStringToBytes("aaaa");
		byte[] footbyte = HexUtils.hexStringToBytes("aa55");
		
		System.out.println(Arrays.toString(headbyte));
		
		System.out.println(Arrays.toString(footbyte));
	}
	
	public  static List<String> getTeacherList(String managers){
        List<String> ls=new ArrayList<String>();
       // Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Pattern pattern = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
        
        Matcher matcher = pattern.matcher(managers);
        while(matcher.find())
            ls.add(matcher.group());
        return ls;
    }
}

package com.mobmixer.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateManager
{


	public static String getDate()
	{
		String date = "";
		try
		{
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			date = formater.format(new Date());
		}
		catch (Exception e)
		{
			// Modified by 김현준 (2015-02-11): 로그 메소드로 수정!
			LogPrint.e("getDate() Exception!", e);
			// e.toString();
		}

		return date;
	}

}

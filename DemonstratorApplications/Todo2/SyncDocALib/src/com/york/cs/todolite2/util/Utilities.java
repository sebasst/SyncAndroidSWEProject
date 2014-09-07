package com.york.cs.todolite2.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class Utilities {

	/**
	 * @return current time in format yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 */

	public static String getCurrentTimeString() {

		Calendar calendar = GregorianCalendar.getInstance();
		return getDateFormater().format(calendar.getTime());

	}

	public static String calendarToString(Calendar calendar) {

		return getDateFormater().format(calendar.getTime());

	}
	
	public static Calendar stringToCalendar(String time) throws ParseException{
		
		Calendar cal=Calendar.getInstance();
		cal.setTime(getDateFormater().parse(time));
		
		return cal;
	}

	public static SimpleDateFormat getDateFormater() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return dateFormatter;
	}
	
	public static ByteArrayInputStream BitmaptoOutputStream(Bitmap image){
	
		
		ByteArrayOutputStream  out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, out);
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return in;
	}
	
	
	public static Bitmap inputStreamToBitmap(InputStream inputStream){
		 return BitmapFactory.decodeStream(inputStream);
		
	}
	
	
	public static void printKeyFromMap1(Map<String, Object> map){
		
		System.out.println("-----------------keys----------------");
		
		for(String key:map.keySet()){
			System.out.println(key);
		}
	}
	
	public static <T> List<T> copyIterator(Iterator<T> iter) {
	   List<T> copy = new ArrayList<T>();
	
	    while (iter.hasNext())
	        copy.add(iter.next());
	    return copy;
	}
	
	
	
}

package sk.insomnia.tools.dateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvertor {
	
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	
	public static final Date stringToDate(final String dateString) throws ParseException{		
		return sdf.parse(dateString);		
	}

	public static final String dateToString(final Date date){
		if (date!=null)	return sdf.format(date);
		return "";
	}
}

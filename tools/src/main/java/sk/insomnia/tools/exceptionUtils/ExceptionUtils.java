package sk.insomnia.tools.exceptionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

	public static final String exceptionAsString(Exception e){
		  StringWriter sw = new StringWriter();
		  e.printStackTrace(new PrintWriter(sw));
		  return sw.toString();
	}
}

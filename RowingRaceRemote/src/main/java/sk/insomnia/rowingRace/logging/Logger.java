package sk.insomnia.rowingRace.logging;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.SwingUtilities;

public class Logger {
	//    <<VARIABLES>>
	
	private static final LOG_LEVEL			logLevel	= LOG_LEVEL.DEBUG_LOG_LEVEL;
	private static org.apache.log4j.Logger 	logger 	= null;
	private static final boolean 			DEBUG 		= false;
    private static final SimpleDateFormat	dateFormat 	= new SimpleDateFormat("mm:ss:SSS");
    
    private LOG_LEVEL 						eLogLevel 	= logLevel;
	
	public static 		String Glyph					= "";
	private static PrintStream OutPut 					= System.out;
	private static PrintStream OutErr 					= System.err;
	
	
	//  <<PUBLIC STATIC METHODS>>
	
	public static void setLogger(org.apache.log4j.Logger logger) { 
		Logger.logger = logger; 
		Logger.logger.info("Logging level : " + org.apache.log4j.Logger.getRootLogger().getLevel()); 
	}
	
	public static void setCustomGlyph( String glyph ) { Glyph = glyph; }
	
	public static void setCustomOut( PrintStream os ) { OutPut = os; }
	
//	private static 

	//  <<CONSTRUCTORS>>
	public Logger() { }
	public Logger(LOG_LEVEL loglevel) {
		// we can only narrow down logging level
		if( loglevel.getIndex() < eLogLevel.getIndex() )
			eLogLevel = loglevel;
	}
	
	//  <<PUBLIC METHODS>>
	
	public void setLogLevel(LOG_LEVEL loglevel) { eLogLevel = loglevel; }
	public void setOut(PrintStream out) { OutPut = out; }
	
	private String decorate( String message ) {
		int stack_length = Thread.currentThread().getStackTrace().length;
		stack_length = ( stack_length <= 5 ) ? (stack_length-1) : 5;
		
		final StackTraceElement st = Thread.currentThread().getStackTrace()[stack_length];
		return Glyph + "\"" + st.getFileName() + "\" (" + st.getLineNumber() + ") : " + message; 
	}
	
	public void fatal(String message) { fatal( message, true ); }
	public void fatal(String message, boolean decoration )
	{
		if( logger != null ) {
			if(eLogLevel.getIndex() >= LOG_LEVEL.FATAL_LOG_LEVEL.getIndex())
				logger.fatal((decoration?decorate(message):message));
		} else if(eLogLevel.getIndex() >= LOG_LEVEL.FATAL_LOG_LEVEL.getIndex())
			OutErr.println(LOG_LEVEL.FATAL_LOG_LEVEL.getHighlightMessage() + (decoration?decorate(message):message));
	}
	public void fatal(Throwable e) {
		if( DEBUG ) e.printStackTrace();
		if( logger != null )
			logger.fatal( e.getMessage(), e );
		else {
			fatal( e.getMessage() );
			e.printStackTrace();
		}
	}
	
	public void error(String message) { error( message, true ); }
	public void error(String message, boolean decoration )
	{
		if( logger != null ) {
			if(eLogLevel.getIndex() >= LOG_LEVEL.ERROR_LOG_LEVEL.getIndex())
				logger.error((decoration?decorate(message):message));
		} else if(eLogLevel.getIndex() >= LOG_LEVEL.ERROR_LOG_LEVEL.getIndex())
			OutErr.println(LOG_LEVEL.ERROR_LOG_LEVEL.getHighlightMessage() + (decoration?decorate(message):message));
	}
	public void error(Throwable e) {
		if( DEBUG ) e.printStackTrace();
		if( logger != null )
			logger.error( e.getMessage(), e );
		else {
			error( e.getMessage() );
			e.printStackTrace();
		}
	}
	
	public void warn(String message) { warn( message, true ); }
	public void warn(String message, boolean decoration )
	{
		if( logger != null ) {
			if(eLogLevel.getIndex() >= LOG_LEVEL.WARN_LOG_LEVEL.getIndex())
				logger.warn((decoration?decorate(message):message));
		} else if(eLogLevel.getIndex() >= LOG_LEVEL.WARN_LOG_LEVEL.getIndex())
			OutPut.println(LOG_LEVEL.WARN_LOG_LEVEL.getHighlightMessage() + (decoration?decorate(message):message));
	}
	public void warn(Exception e) {
		if( DEBUG ) e.printStackTrace();
		if( logger != null )
			logger.warn( e.getMessage(), e );
		else
			warn( e.getMessage() );
	}

	public void info(String message) { info( message, true ); }
	public void info(String message, boolean decoration )
	{
		if( logger != null ) {
			if(eLogLevel.getIndex() >= LOG_LEVEL.INFO_LOG_LEVEL.getIndex())
				logger.info((decoration?decorate(message):message));
		} else if(eLogLevel.getIndex() >= LOG_LEVEL.INFO_LOG_LEVEL.getIndex())
			OutPut.println(LOG_LEVEL.INFO_LOG_LEVEL.getHighlightMessage() + (decoration?decorate(message):message) );
	}
	public void info(Exception e) {
		if( DEBUG ) e.printStackTrace();
		if( logger != null )
			logger.warn( e.getMessage(), e );
		else
			info( e.getMessage() );
	}
	
	public void debug(String message) { debug( message, true ); }
	public void debug(String message, boolean decoration )
	{
		if( logger != null ) {
			if(eLogLevel.getIndex() >= LOG_LEVEL.DEBUG_LOG_LEVEL.getIndex())
				logger.debug((decoration?decorate(message):message));
		} else if(eLogLevel.getIndex() >= LOG_LEVEL.DEBUG_LOG_LEVEL.getIndex())
			OutPut.println(LOG_LEVEL.DEBUG_LOG_LEVEL.getHighlightMessage() + (decoration?decorate(message):message));
	}
	
	public void trace(String message) { trace( message, true ); }
	public void trace(String message, boolean decoration )
	{
		if( logger != null ) {
			if(eLogLevel.getIndex() >= LOG_LEVEL.TRACE_LOG_LEVEL.getIndex())
				logger.trace((decoration?decorate(message):message));
		} else if(eLogLevel.getIndex() >= LOG_LEVEL.TRACE_LOG_LEVEL.getIndex())
			OutPut.println(LOG_LEVEL.TRACE_LOG_LEVEL.getHighlightMessage() + (decoration?decorate(message):message));
	}
	
	
	// << MISC PRINTOUTS >>
	public void PrintTiming( long start_time ) {
		trace( PrintTiming( start_time, null ), false );
	}
	public static String PrintTiming( long start_time, String message ) {
		return ( (message==null?"lasted : ":message) + dateFormat.format( 
				new Date( Calendar.getInstance().getTime().getTime() - start_time ) ) );
	}
	public void PrintThread() {
		debug( (SwingUtilities.isEventDispatchThread() ?  " < EDT > " : " < WORKER THREAD >") + " ID : " + Thread.currentThread().getId());
	}
	public void PrintStackTrace() {
		final StackTraceElement[] call = Thread.currentThread().getStackTrace();
		final StringBuffer buf = new StringBuffer();
		
		buf.append(call[3].getClassName());
		buf.append(": ").append(call[3].getMethodName()).append("\n");
		if( call.length >= 4 )
			for( int i=4; i<call.length; i++ ) {
				buf.append("\t\t\t   - ").append(call[i].getClassName());
				buf.append(": ").append(call[i].getMethodName()).append("\n");
			}
		debug( buf.toString(),false );
	}
	public void PrintProperties( Properties props ) {	
		for(Enumeration<String> names = (Enumeration<String>)props.propertyNames(); names.hasMoreElements(); ) {
			String name = names.nextElement();
			debug(name + "=" + props.getProperty(name));
		}
	}
	
	public void PrintObjectMethods(Object obj) {
		Method[] methods = obj.getClass().getDeclaredMethods();

		debug("Class's [" + obj.getClass().toString() + "] methods information :");
	    for (int i = 0; i < methods.length; i++) {
	      Method m = methods[i];
	      Class retType = m.getReturnType();
	      Class[] paramTypes = m.getParameterTypes();
	      String name = m.getName();
	      debug(Modifier.toString(m.getModifiers()));
	      debug(" " + retType.getName() + " " + name + "(");
	      for (int j = 0; j < paramTypes.length; j++) {
	        if (j > 0)
	        	debug(", ");
	        debug(paramTypes[j].getName());
	      }
	      debug(");");
	    }

	}
}

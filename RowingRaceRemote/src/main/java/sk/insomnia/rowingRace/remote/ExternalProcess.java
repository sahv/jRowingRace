package sk.insomnia.rowingRace.remote;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessControlException;
import java.util.Calendar;

import javax.swing.Timer;

import sk.insomnia.rowingRace.logging.LOG_LEVEL;
import sk.insomnia.rowingRace.logging.Logger;

public class ExternalProcess 
{
   	private final Logger logger = new Logger(LOG_LEVEL.DEBUG_LOG_LEVEL);	
	
	private Process		process;
        private String 		command;
	private String []	commands;
	private boolean		bRunning;
	private boolean 	bFinnished;
	private boolean		bBackground;
	private Runnable	callback;
	
	private String		stdOut;
	private String		stdErr;
	private int             retValue;        
        
	private Timer		timer;
	private int		timeout;
    
        long			time;
	

	public ExternalProcess( String command ) {
		Init( command , 0 );
	}
	
	public ExternalProcess( String command, boolean start ) {
		Init( command , 0 );
		if( start ) execute();
	}
	
	public ExternalProcess( String command, boolean start, int timeout ) {
		assert( timeout >= 0 );
		Init( command , timeout );
		if( start ) execute();
	}
	public ExternalProcess( String [] commands, boolean start, int timeout ) {
		assert( timeout >= 0 );
		Init( commands , timeout );
		if( start ) execute();
	}
	
	private void Init( String [] commands, int timeout ) {
		this.commands = commands;
		Init( "",timeout);
	}
	private void Init( String command, int timeout  ) {
		this.command = command;
		this.bRunning = false;
		this.bFinnished = false;
		this.timeout = timeout;
	    
		timer = new Timer(this.timeout, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	OnTimeout();
		    }    
		});
		timer.setRepeats(false);             
	}

	public void Start() {
		if( bRunning ) return;
		
		bFinnished = false;
		bBackground = false;
		execute();
	}
	
	public void BackgroundStart( Runnable callback ) {
		if( bRunning ) return;
		
		bFinnished = false;
		bBackground = true;
		this.callback = callback;
		execute();
	}
	
	public void Stop() {
		if( bRunning == false ) return;
		
		process.destroy();
		bRunning = false;
	}
	
	public boolean isRunning() {
		return this.bRunning;
	}
	
	public int getExitValue() {
		return ( (process==null)? 666 : retValue);
	}
	
	public String getProcessName() {
		if( commands == null ) {
			assert( command != null );
			return command;
		} else {
			assert( commands != null );
			String buf = "";
			for (final String mCommand : commands)
				buf += mCommand + " ";
			return buf;
		}
	}
	
	public void DumpProcessInformations() {
		
              logger.info( "  returncode : " + getExitValue() );
	}

//	<< PRIVATE >>		
	private void OnTimeout( ) {
		timer.stop();
		bFinnished = false;
		
		logger.warn("[" + getProcessName() + "]" + "External process timeout!");
		Stop();
	}
	
	private void execute() {
		bRunning = true;
		bFinnished = true;
		
		try {
	
			process = Runtime.getRuntime().exec( command );
                                
			if( timeout > 0 )
				timer.start();
			
			time = Calendar.getInstance().getTime().getTime();
			
			if(bBackground) { 		// watch process in background	
				final Thread bgrd = new Thread( new Runnable() {
					public void run() {
						try { hook(process); } catch( final Exception e ) { logger.error(e); }
						logger.PrintTiming( time );
						
						timer.stop();
						if( callback != null )
							callback.run();
						bRunning = false;
					}
				}); 
				bgrd.start();
			} else {					// block until process finnish
				try { hook(process); } catch( final Exception e ) { logger.error(e); }
				logger.PrintTiming( time );
				
				timer.stop();
				bRunning = false;
			}

		} catch( final AccessControlException ace ) { 
			logger.warn("no permission for " + command);
		} catch( final Exception e ) {
			logger.error(e);
		}
	}
	
	private void hook( final Process proc ) throws InterruptedException {
        final StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream());            
        final StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream());
            
        errorGobbler.start();
        outputGobbler.start();
                                
        retValue = proc.waitFor();
        
        errorGobbler.join();   // Handle condition where the
        outputGobbler.join();  // process ends before the threads finish
        
        stdErr = errorGobbler.getResult();
        stdOut = outputGobbler.getResult();
	
        }
	
	public String GetProcessErrors() { return stdErr;	}
	
	public String GetProcessInformations() { return stdOut; }
	
	class StreamGobbler extends Thread 
	{
	    InputStream 	inputStream;
	    StringBuffer 	buffer = new StringBuffer();
	    
	    StreamGobbler(InputStream is) {
	        this.inputStream = is;
	    }
	    
	    public String getResult() { return buffer.toString(); }
	    
	    @Override
		public void run() {
        	final BufferedReader stdouts = new BufferedReader( new InputStreamReader(inputStream) );
    		String line = null;
    		try {
    			while ((line = stdouts.readLine()) != null) {
    				buffer.append(line);
    				buffer.append('\n');
    			}
    		} catch( final IOException ioe ) {
    			logger.error(ioe);
    		}
	    }
	}

}

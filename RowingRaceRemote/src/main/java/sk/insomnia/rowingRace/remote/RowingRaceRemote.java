/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.insomnia.rowingRace.remote;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 */
public class RowingRaceRemote {

    private final Logger LOG = LoggerFactory.getLogger(RowingRaceRemote.class);
    private static boolean bInitialized = false;
    private static boolean bLoaded = false;
    private static boolean bReady = false;
    private String sLibraryName;


    public RowingRaceRemote() {

        if (bInitialized == false) {
            bInitialized = true;

            sLibraryName = "RowingRaceJNI";

            try {
                System.loadLibrary(sLibraryName);
                bLoaded = true;

            } catch (final java.lang.UnsatisfiedLinkError ule) {
                           /*
                           try {
				 
                                
                                final String log = libDir + "depends_" + sLibraryName + ".dll"; 
                                final String command = "\"" + libDir + "Depends.bat" + "\" " + "\"" +libDir + sLibraryName + "\" \"" + log + "\"";
                                System.out.println(command);
                                final ExternalProcess p = new ExternalProcess(command,true,50000);         //timeout 50 seconds
                                p.DumpProcessInformations();
                                bLoaded = false; 
                              
			  } catch (final Exception e) {
			  */
                bLoaded = false;
                LOG.error(ule.getMessage());
                //throw new RuntimeException(e);
                //}
            }

        }
    }

    public boolean isLoaded() {
        return bLoaded;
    }

    //Remote control methods

    public int connectToDevice() {
        try {
            int i = RowingRaceJNI.Connect();
            return i;
        } catch (Exception e) {
            LOG.error("error connecting to device :" + e.getMessage());
        }
        return -1;
    }

    public void disconnect() {
        RowingRaceJNI.Disconnect();
    }

    public int getSpeed(int port) {
        return RowingRaceJNI.GetInfo(port);
    }

    public boolean setWorkState(int port) {
        return RowingRaceJNI.SetWorkState(port);
    }

    public int[] sendCommand(int port, int[] commandData) {
        return RowingRaceJNI.SendCommand(port, commandData);
    }
}

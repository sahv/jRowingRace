/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.insomnia.rowingRace.remote;

/**
 *
 * @author Administrator
 */
public class RowingRaceJNI {
    
	public static native int Connect();
	public static native void Disconnect();
        public static native boolean SetWorkState(int port);
        public static native int GetInfo(int port);
        public static native int [] SendCommand(int port, int [] commandData);
}

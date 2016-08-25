package android.util;


public class Log {

	public static void d(String logTag, String logMsg) {
		// TODO Auto-generated method stub
		System.out.println("DEBUG " + logTag + " : " + logMsg);
	}

	public static void e(String logTag, String logMsg, Exception e) {
		// TODO Auto-generated method stub
		System.out.println("ERROR " + logTag + " : " + logMsg);
		e.printStackTrace();
	}

}

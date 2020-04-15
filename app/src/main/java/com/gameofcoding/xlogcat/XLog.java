package com.gameofcoding.xlogcat;

import android.content.Context;
import android.content.Intent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import com.gameofcoding.xlogcat.utils.AppConstants;

public abstract class XLog {
	private static Context mContext;
	private static boolean mHasCalledStop;
	public static void start(Context context) {
		mContext = context;
		mHasCalledStop = false;
		Thread logReader = new Thread(new Runnable() {
				@Override                                                          
				public void run() {
					try {
						Reader reader = new InputStreamReader(Runtime.getRuntime().exec("logcat -v threadtime").getInputStream());
						BufferedReader logcatReader = new BufferedReader(reader);
						while (!mHasCalledStop) {
							String logLine = logcatReader.readLine();
							if (logLine != null)
								sendLogLine(logLine);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		logReader.start();
	}
	
	public static void stop() {
		mHasCalledStop = true;
	}

	private static void sendLogLine(String logLine) {
		Intent intent = new Intent();
		intent.setPackage(AppConstants.LOG_RECEIVER_PKG_NAME);
		intent.setAction(AppConstants.ACTION_APP_LOG);
		intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		intent.putExtra(AppConstants.KEY_APP_PACKAGE_NAME, mContext.getPackageName());
		intent.putExtra(AppConstants.KEY_APP_LOG_LINE, logLine);
		mContext.sendBroadcast(intent);
	}
}

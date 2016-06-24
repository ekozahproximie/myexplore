package com.neural.constant;

public class Constants {
	public static final String  ROOT="NeuralTheraputic";
	public static final String  CRASH_LOG="crash.txt";
	public static final String  APP_NAME="NeuralTheraputic";
	public static class Dialog {
		
		public static final String DIALOG_TITLE = "title";
		public static final String DIALOG_TITLE_ICON = "title_icon";
		public static final String ALERT_MESSAGE = "message";
		public static final String ALERT_MESSAGE_TEXT = "messageText";
		public static final String ALERT_POS = "pos";
		public static final String ALERT_NEG = "neg";
		public static final String ALERT_NEUTRAL = "neutral";
		public static final int ALERT_APP_EXIT = 1;
		private static final int RESULT_FIRST_USER = 0x1;// from: int
															// android.app.Activity.RESULT_FIRST_USER
															// = 1 [0x1]
		public static final int ALERT_RESULT_POS = RESULT_FIRST_USER + 1;
		public static final int ALERT_RESULT_NEG = ALERT_RESULT_POS + 1;
		public static final int ALERT_RESULT_NEUTRAL = ALERT_RESULT_NEG + 1;
	}

}

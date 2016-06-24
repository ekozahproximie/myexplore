package com.spime;

import java.net.HttpURLConnection;

public interface MyCallback {
	public void resultOk(HttpURLConnection hc);
	  public void resultCancel(HttpURLConnection hc);
}

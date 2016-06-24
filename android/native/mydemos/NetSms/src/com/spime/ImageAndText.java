package com.spime;

import javax.security.auth.PrivateCredentialPermission;

import android.graphics.Bitmap;

public class ImageAndText {
    private String imageUrl;
    private String text;
    private Bitmap bitmap;
    private String body;

    public ImageAndText(String imageUrl, String text,Bitmap bitmap) {
        this.imageUrl = imageUrl;
        this.text = text;
        this.bitmap=bitmap;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getText() {
        return text;
    }
    public Bitmap getBit() {
        return bitmap;
    }
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return text;
    }
    public void setBody(String body) {
		this.body = body;
	}
    public String getBody() {
		return body;
	}
}


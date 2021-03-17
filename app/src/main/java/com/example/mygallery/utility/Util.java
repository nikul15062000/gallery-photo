package com.example.mygallery.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {

	public static int slideshow = 0;

	public static int unhidealllayout;

	public static String videoduration;

	public static int rotationscreen;

	public static int stopsongstop = 0;

	public static int screenrotationobject;

	public static String albumname;


	public static boolean isOnline(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	public static final String tiny_url = "https://play.google.com/store/apps/details?id=com.bits_maker_developers.npbits.videoplayer";
	public static String getShareText(Context context, String photo_name) {

		return getRandomStartText() + " application of \"" + photo_name
				+ "\" download from the following link: " + "\n" + tiny_url;

	}
	public static String getRandomStartText() {
		try {
			String[] arr = new String[]{"A beautiful", "Cool",
					"An Awesome", "The best"};
			return arr[random(0, arr.length - 1)];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "A beautiful";
	}
	public static int random(int min, int max) {
		int random = Math
				.round((float) (min + (Math.random() * ((max - min) + 1))));
		if (random >= max) {
			random = max;
		}
		return random;
	}

}

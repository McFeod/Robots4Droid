package com.github.mcfeod.robots4droid;

import java.lang.reflect.InvocationTargetException;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

public class AboutActivity extends Activity{

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
		protected void onCreate(Bundle savedInstanceBundle){
			super.onCreate(savedInstanceBundle);
			setContentView(R.layout.about_activity);
			WebView content = (WebView)findViewById(R.id.about_view);
			WebSettings settings = content.getSettings();
			settings.setDefaultTextEncodingName("utf-8");
			content.loadUrl(getString(R.string.about_url));
			content.setScrollbarFadingEnabled(true);
			settings.setSupportZoom(true);
			settings.setBuiltInZoomControls(true);
			content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				settings.setDisplayZoomControls(false);
			} else {
				ZoomButtonsController zoom_controll;
				try {
					zoom_controll = (ZoomButtonsController) content.getClass().getMethod("getZoomButtonsController").invoke(content, null);
					zoom_controll.getContainer().setVisibility(View.GONE);
				} catch (IllegalAccessException e) {} catch (IllegalArgumentException e){} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
				
			}
	}

}

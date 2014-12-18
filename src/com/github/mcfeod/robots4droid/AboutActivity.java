package com.github.mcfeod.robots4droid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AboutActivity extends Activity{

	@Override
		protected void onCreate(Bundle savedInstanceBundle){
			super.onCreate(savedInstanceBundle);
			setContentView(R.layout.about_activity);
			WebView content = (WebView)findViewById(R.id.about_view);
			WebSettings settings = content.getSettings();
			settings.setDefaultTextEncodingName("utf-8");
			content.loadUrl(getString(R.string.about_url));
			content.setScrollbarFadingEnabled(true);
			content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}

}

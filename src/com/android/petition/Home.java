package com.android.petition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;

public class Home extends Activity {

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.home);
	}

	public void onClick(View v) {
		if (((String) v.getTag()).equals("Create")) {
			Intent intent = new Intent(getApplication(), Create.class);
			startActivityForResult(intent, Activity.RESULT_OK);
		}
		if (((String) v.getTag()).equals("View")) {
			Intent intent = new Intent(getApplication(), PetitionList.class);
			startActivityForResult(intent, Activity.RESULT_OK);

		}
		if (((String) v.getTag()).equals("Settings")) {

		}
		if (((String) v.getTag()).equals("Mail")) {

		}
	}

}

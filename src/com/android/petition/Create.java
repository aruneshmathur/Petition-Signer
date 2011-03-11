package com.android.petition;

import java.util.HashMap;

import com.android.petition.db.Petition_Details_db;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Create extends Activity {
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.create);

		Button doneButton = (Button) findViewById(R.id.done);
		final HashMap<String, String> sendMap = new HashMap<String, String>();
		final Petition_Details_db database = new Petition_Details_db(
				getApplicationContext());
		database.open();

		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.content);
				for (int i = 0; i < layout.getChildCount(); i++) {
					EditText text = (EditText) layout.getChildAt(i);
					sendMap.put((String) text.getTag(), text.getText()
							.toString());
				}
				sendMap.put(Petition_Details_db.KEY_PETITION_COMPLETED, "-1");
				sendMap.put(Petition_Details_db.KEY_PETITION_SIGNED, "0");

				database.insertPetition(sendMap);
				finish();
			}
		});

	}
}

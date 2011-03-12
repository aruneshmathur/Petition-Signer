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

	Button mDoneButton;
	Button mClearButton;
	EditText mText;
	HashMap<String, String> mSendMap;
	Petition_Details_db database; 

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.create);

		mDoneButton = (Button) findViewById(R.id.done);
		mClearButton = (Button) findViewById(R.id.clear);

		mSendMap = new HashMap<String, String>();
		database = new Petition_Details_db(
				getApplicationContext());
		database.open();

		mDoneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.content);
				for (int i = 0; i < layout.getChildCount(); i++) {
					EditText text = (EditText) layout.getChildAt(i);
					mSendMap.put((String) text.getTag(), text.getText()
							.toString());
				}
				mSendMap.put(Petition_Details_db.KEY_PETITION_COMPLETED, "-1");
				mSendMap.put(Petition_Details_db.KEY_PETITION_SIGNED, "0");
				mSendMap.put(Petition_Details_db.KEY_PETITION_ID,
						String.valueOf(System.currentTimeMillis()));

				database.insertPetition(mSendMap);
				finish();
			}
		});

		mClearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.content);
				for (int i = 0; i < layout.getChildCount(); i++) {
					mText = (EditText) layout.getChildAt(i);
					mText.setText("");
				}
			}
		});

	}
}

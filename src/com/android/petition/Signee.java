package com.android.petition;

import java.util.HashMap;
import com.android.petition.db.Petition_Details_db;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Signee extends Activity {

	private static final int SIGN = 1;
	private String mSigneeId;
	private byte[] mSignature;
	private HashMap<String, Object> mSendMap;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.signee_create);
		mSigneeId = String.valueOf(System.currentTimeMillis());
		Intent intent = getIntent();
		final String pid = intent.getStringExtra("PetitionID");

		mSendMap = new HashMap<String, Object>();
		final Petition_Details_db database = new Petition_Details_db(
				getApplicationContext());
		database.open();

		Button signButton = (Button) this.findViewById(R.id.sign);
		signButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Signee.this, Signature.class);
				startActivityForResult(intent, SIGN);
			}
		});

		Button doneButton = (Button) this.findViewById(R.id.done);
		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.subLayout);
				for (int i = 0; i < layout.getChildCount(); i++) {
					if (layout.getChildAt(i).getClass() == EditText.class) {
						EditText text = (EditText) layout.getChildAt(i);
						mSendMap.put((String) text.getTag(), text.getText()
								.toString());
					} else if (layout.getChildAt(i).getClass() == CheckBox.class) {
						CheckBox box = (CheckBox) layout.getChildAt(i);
						mSendMap.put((String) box.getTag(), "0");
						if (box.isChecked()) {
							mSendMap.put((String) box.getTag(), "1");
						}
					}
				}

				mSendMap.put(Petition_Details_db.KEY_PETITION_ID, pid);
				mSendMap.put(Petition_Details_db.KEY_PETITION_SIGNEE_ID,
						mSigneeId);
				mSendMap.put(Petition_Details_db.KEY_PETITION_SIGNEE_SIGNATURE,
						(mSignature));
				mSendMap.put(Petition_Details_db.KEY_PETITION_SIGNEE_SYNCED,
						"0");

				database.insertSignee(mSendMap);
				database.close();
				Intent data = new Intent();
				data.putExtra(
						Petition_Details_db.KEY_PETITION_SIGNEE_ID,
						(String) mSendMap
								.get(Petition_Details_db.KEY_PETITION_SIGNEE_ID));
				setResult(Activity.RESULT_OK, data);

				finish();

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SIGN) {
			if (resultCode == Activity.RESULT_OK) {
				mSignature = data
						.getByteArrayExtra(Petition_Details_db.KEY_PETITION_SIGNEE_SIGNATURE);
			}
		}
	}
}

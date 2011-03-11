package com.android.petition;

import java.util.HashMap;

import com.android.petition.db.Petition_Details_db;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Signee extends Activity{

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.petition_signee_main);
		
		final HashMap<String, String> sendMap = new HashMap<String, String>();
		final Petition_Details_db database = new Petition_Details_db(getApplicationContext());
		database.open();
		
		Button doneButton = (Button) this.findViewById(R.id.done);
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout) findViewById(R.id.subLayout);
				for (int i = 0; i < layout.getChildCount(); i++) {
						if(layout.getChildAt(i).getClass()==EditText.class) {
							EditText text = (EditText) layout.getChildAt(i);
							sendMap.put((String)text.getTag(), text.getText().toString());
						}
						else if(layout.getChildAt(i).getClass()==CheckBox.class) {
							CheckBox box = (CheckBox) layout.getChildAt(i);
							sendMap.put((String)box.getTag(), "0");
							if (box.isChecked()) {
								sendMap.put((String)box.getTag(), "1");
							}
						}
				}
								
				database.insertSignee(sendMap);
				database.close();
				finish();
			}
		});
 }
}

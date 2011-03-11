package com.android.petition;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.petition.db.Petition_Details_db;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class PetitioneeList extends Activity{
	
	ArrayList<HashMap<String, String>> petioneeList = null;
	LayoutInflater inflater;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.petition_list_main);
		
		inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		
		Intent intent = this.getIntent();
		String pid = intent.getStringExtra("PetitionID");
		
		Petition_Details_db database = new Petition_Details_db(getApplicationContext());
		database.open();
		
		petioneeList = database.getPetioneeList(pid);
		database.close();
		
		Button add= (Button)findViewById(R.id.add_signee);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PetitioneeList.this,
						Signee.class);
				startActivity(intent);
			}
		});
		

	}

	private class PetitionListViewAdapter extends BaseAdapter {
		Context context;
		ViewHolder holder;

		public PetitionListViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return petioneeList.size();
		}

		@Override
		public Object getItem(int position) {
			return petioneeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.petitioneetextview, null);
				holder.petitioneeName= (TextView) convertView
						.findViewById(R.id.petitiontitle);

				holder.email = (TextView) convertView
						.findViewById(R.id.number_of_signatures);
				convertView.setTag(holder);
			}

			holder.petitioneeName.setText(petioneeList.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNEE_NAME));

			holder.email.setText(petioneeList.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNEE_EMAIL));
			
			return convertView;
		}

		public class ViewHolder {
			TextView petitioneeName;
			TextView email;
		}

	}
	
	
}

package com.android.petition;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.petition.db.Petition_Details_db;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class PetitioneeList extends Activity {

	ArrayList<HashMap<String, String>> petioneeList = null;
	LayoutInflater inflater;
	ListView mPetitioneeListView;
	static final int ADD = 1;
	Petition_Details_db database;
	String pid = null;;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.petition_list_main);

		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		Intent intent = this.getIntent();
		pid = intent.getStringExtra("PetitionID");

		mPetitioneeListView = (ListView) findViewById(R.id.petitioneeList);

		database = new Petition_Details_db(getApplicationContext());
		database.open();

		petioneeList = database.getPetioneeList(pid);
		database.close();

		ImageButton add = (ImageButton) findViewById(R.id.add_signee);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PetitioneeList.this, Signee.class);
				intent.putExtra("PetitionID", pid);
				startActivityForResult(intent, ADD);
			}
		});

		mPetitioneeListView.setAdapter(new PetitionListViewAdapter());

	}

	private class PetitionListViewAdapter extends BaseAdapter {
		ViewHolder holder;

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.petitioneetextview,
						null);
				holder.petitioneeName = (TextView) convertView
						.findViewById(R.id.petitioneeName);

				holder.email = (TextView) convertView
						.findViewById(R.id.petitioneeEmail);
				convertView.setTag(holder);
			}

			holder.petitioneeName.setText(petioneeList.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNEE_NAME));

			holder.email.setText(petioneeList.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNEE_EMAIL));

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					return false;
				}
			});

			return convertView;
		}

		public class ViewHolder {
			TextView petitioneeName;
			TextView email;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ADD) {
			if (resultCode == Activity.RESULT_OK) {
				String signeeId = data
						.getStringExtra(Petition_Details_db.KEY_PETITION_SIGNEE_ID);
				database.open();
				petioneeList.add(database.getSignee(pid, signeeId));
				((PetitionListViewAdapter) mPetitioneeListView.getAdapter())
						.notifyDataSetChanged();
				database.close();
			}
		}
	}

}

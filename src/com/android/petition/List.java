package com.android.petition;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.petition.constants.Constants;
import com.android.petition.db.Petition_Details_db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class List extends Activity {

	ArrayList<HashMap<String, String>> petition;
	LayoutInflater inflater;
	boolean touched = false;
	Petition_Details_db database;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		database = new Petition_Details_db(this);
		database.open();
		petition = database.getPetitions();
		database.close();

		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ListView petitionList = (ListView) findViewById(R.id.petitionlist);

		petitionList.setAdapter(new PetitionListViewAdapter(this));

	}

	private class PetitionListViewAdapter extends BaseAdapter {
		Context context;
		ViewHolder holder;

		public PetitionListViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return petition.size();
		}

		@Override
		public Object getItem(int position) {
			return petition.get(position);
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
				convertView = inflater.inflate(R.layout.petitiontextview, null);
				holder.petitionTitle = (TextView) convertView
						.findViewById(R.id.petitiontitle);

				holder.no_of_signatures = (TextView) convertView
						.findViewById(R.id.number_of_signatures);
				convertView.setTag(holder);
			}

			holder.petitionTitle.setText(petition.get(position).get(
					Petition_Details_db.KEY_PETITION_TITLE));

			holder.no_of_signatures.setText(petition.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNED));

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(List.this, PetitioneeList.class);
					intent.putExtra(
							"PetitionID",
							petition.get(position).get(
									Petition_Details_db.KEY_PETITION_ID));
					startActivity(intent);
				}
			});
			return convertView;
		}

		public class ViewHolder {
			TextView petitionTitle;
			TextView no_of_signatures;
		}

	}
}
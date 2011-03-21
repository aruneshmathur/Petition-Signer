package com.android.petition;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.petition.db.Petition_Details_db;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PetitionList extends Activity {

	ArrayList<HashMap<String, String>> mPetition;
	LayoutInflater mInflater;
	Petition_Details_db database;
	ListView mPetitionList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.petition_list);
		database = new Petition_Details_db(this);
		database.open();
		mPetition = database.getPetitions();

		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mPetitionList = (ListView) findViewById(R.id.petitionlist);

		mPetitionList.setAdapter(new PetitionListViewAdapter());

		registerForContextMenu(mPetitionList);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.petitionlist) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(mPetition.get(info.position).get(
					Petition_Details_db.KEY_PETITION_TITLE));
			menu.add(Menu.NONE, 0, 0, "Delete");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
			HashMap<String, String> map = mPetition.get(info.position);
			String pid = map.get(Petition_Details_db.KEY_PETITION_ID);
			database.open();
			database.deletePetition(pid);
			mPetition.remove(info.position);
			((PetitionListViewAdapter) mPetitionList.getAdapter())
					.notifyDataSetChanged();
			database.close();
		}

		return true;
	}

	private class PetitionListViewAdapter extends BaseAdapter {
		ViewHolder mHolder;

		@Override
		public int getCount() {
			return mPetition.size();
		}

		@Override
		public Object getItem(int position) {
			return mPetition.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.petition_text_view,
						null);
				mHolder.petitionTitle = (TextView) convertView
						.findViewById(R.id.petitionTitle);

				mHolder.no_of_signatures = (TextView) convertView
						.findViewById(R.id.number_of_signatures);

				mHolder.created = (ImageButton) convertView
						.findViewById(R.id.not_created);
				mHolder.synced = (ImageButton) convertView
						.findViewById(R.id.not_synced);
				convertView.setTag(mHolder);
			}

			mHolder = (ViewHolder) convertView.getTag();

			mHolder.petitionTitle.setText(mPetition.get(position).get(
					Petition_Details_db.KEY_PETITION_TITLE));

			mHolder.no_of_signatures.setText(mPetition.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNED));

			if (mPetition.get(position)
					.get(Petition_Details_db.KEY_PETITION_SYNCED).equals("1")) {
				mHolder.synced.setVisibility(View.GONE);
			} else {
				mHolder.synced.setVisibility(View.VISIBLE);
			}

			if (mPetition.get(position)
					.get(Petition_Details_db.KEY_PETITION_CREATED).equals("1")) {
				mHolder.created.setVisibility(View.GONE);
			} else {
				mHolder.created.setVisibility(View.VISIBLE);
			}

			mHolder.created.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					HashMap<String, String> map = mPetition.get(position);
					String pid = map.get(Petition_Details_db.KEY_PETITION_ID);
					if (!pid.contains("aruneshmathur1990@gmail.com")) {
						pid = "aruneshmathur1990@gmail.com" + pid;
						map.put(Petition_Details_db.KEY_PETITION_ID, pid);
					}
					new Petition_SyncTask(getApplicationContext()).execute(map);
				}
			});

			mHolder.synced.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					HashMap<String, String> map = mPetition.get(position);
					String pid = map.get(Petition_Details_db.KEY_PETITION_ID);
					database.open();
					ArrayList<HashMap<String, Object>> sendSigneeList = database
							.getSigneesForUpload(pid);
					database.close();

					// Launch SyncTask for the Signees
					new Signee_SyncTask(getApplicationContext())
							.execute(sendSigneeList);

				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					HashMap<String, String> map = mPetition.get(position);
					String pid = map.get(Petition_Details_db.KEY_PETITION_ID);
					if (pid.contains("aruneshmathur1990@gmail.com")) {
						pid = pid.substring(pid.indexOf("com") + 3);
						map.put(Petition_Details_db.KEY_PETITION_ID, pid);
					}
					Intent intent = new Intent(PetitionList.this,
							SigneeList.class);
					intent.putExtra("PetitionID",
							map.get(Petition_Details_db.KEY_PETITION_ID));
					database.close();
					startActivity(intent);
				}
			});

			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					return false;
				}
			});

			return convertView;
		}

		public class ViewHolder {
			TextView petitionTitle;
			TextView no_of_signatures;
			ImageButton created;
			ImageButton synced;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		database.open();
		for (HashMap<String, String> map : mPetition) {
			String pid = map.get(Petition_Details_db.KEY_PETITION_ID);
			HashMap<String, String> result = database.getSigneeStatus(pid);
			map.put(Petition_Details_db.KEY_PETITION_SIGNED,
					result.get(Petition_Details_db.KEY_PETITION_SIGNED));
			map.put(Petition_Details_db.KEY_PETITION_SYNCED,
					result.get(Petition_Details_db.KEY_PETITION_SYNCED));
		}
		database.close();

		((PetitionListViewAdapter) mPetitionList.getAdapter())
				.notifyDataSetChanged();
	}
}
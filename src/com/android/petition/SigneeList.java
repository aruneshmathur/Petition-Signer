package com.android.petition;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.petition.PetitionList.PetitionListViewAdapter.ViewHolder;
import com.android.petition.db.Petition_Details_db;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SigneeList extends Activity {

	ArrayList<HashMap<String, String>> mSigneeData = null;
	LayoutInflater mInflater;
	ListView mSigneeListView;
	static final int ADD = 1;
	Petition_Details_db database;
	String mPid = null;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.signee_list);

		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		Intent intent = this.getIntent();
		mPid = intent.getStringExtra("PetitionID");

		mSigneeListView = (ListView) findViewById(R.id.signeeList);

		database = new Petition_Details_db(getApplicationContext());
		database.open();

		mSigneeData = database.getPetioneeList(mPid);
		database.close();

		ImageButton add = (ImageButton) findViewById(R.id.add_signee);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SigneeList.this, Signee.class);
				intent.putExtra("PetitionID", mPid);
				startActivityForResult(intent, ADD);
			}
		});

		mSigneeListView.setAdapter(new SigneeListViewAdapter());

		registerForContextMenu(mSigneeListView);

	}

	private class SigneeListViewAdapter extends BaseAdapter {
		ViewHolder mHolder;

		@Override
		public int getCount() {
			return mSigneeData.size();
		}

		@Override
		public Object getItem(int position) {
			return mSigneeData.get(position);
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
				convertView = mInflater
						.inflate(R.layout.signee_text_view, null);
				mHolder.petitioneeName = (TextView) convertView
						.findViewById(R.id.signeeName);

				mHolder.email = (TextView) convertView
						.findViewById(R.id.signeeEmail);
				convertView.setTag(mHolder);
			}
			
			mHolder=(ViewHolder)convertView.getTag();
			
			mHolder.petitioneeName.setText(mSigneeData.get(position).get(
					Petition_Details_db.KEY_PETITION_SIGNEE_NAME));

			mHolder.email.setText(mSigneeData.get(position).get(
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
				mSigneeData.add(database.getSignee(mPid, signeeId));
				((SigneeListViewAdapter) mSigneeListView.getAdapter())
						.notifyDataSetChanged();
				database.close();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.signeeList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(mSigneeData.get(info.position).get(
					Petition_Details_db.KEY_PETITION_SIGNEE_NAME));

			menu.add(Menu.NONE, 0, 0, "Delete");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
			HashMap<String, String> map = mSigneeData.get(info.position);
			String pid = map.get(Petition_Details_db.KEY_PETITION_SIGNEE_ID);
			database.open();
			database.deleteSignee(pid);
			mSigneeData.remove(info.position);
			((SigneeListViewAdapter) mSigneeListView.getAdapter())
					.notifyDataSetChanged();
			database.close();
		}

		return true;
	}

}

package com.android.petition;

import java.net.MalformedURLException;
import java.util.HashMap;

import com.android.petition.db.Petition_Details_db;

import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class Petition_SyncTask extends
		AsyncTask<HashMap<String, String>, Void, HashMap<String, Object>> {

	private Context mContext;
	private Petition_Details_db mDatabase;
	private String mToastString;
	private final String KEY_RESULT_MAP = "resultMap";
	private final String KEY_RESULT_STATUS = "resultStatus";

	public Petition_SyncTask(Context appContext) {
		mContext = appContext;
	}

	@Override
	protected void onPostExecute(HashMap<String, Object> result) {

		if (result != null) {
			Boolean status = (Boolean) result.get(KEY_RESULT_STATUS);
			if (status) {
				HashMap<String, String> sendMap = (HashMap<String, String>) result
						.get(KEY_RESULT_MAP);
				mDatabase = new Petition_Details_db(mContext);
				mDatabase.open();

				mDatabase.setPetitionStatus(sendMap
						.get(Petition_Details_db.KEY_PETITION_ID));

				mDatabase.close();
				mToastString = mContext.getResources().getString(
						R.string.petition_send_success);
			} else {
				mToastString = mContext.getResources().getString(
						R.string.petition_server_failed);
			}
		} else {
			mToastString = mContext.getResources().getString(
					R.string.petition_send_failed);
		}
		Toast.makeText(mContext, mToastString, 10).show();
	}

	@Override
	protected HashMap<String, Object> doInBackground(
			HashMap<String, String>... maps) {

		if (!maps[0].get(Petition_Details_db.KEY_PETITION_ID).contains(
				"aruneshmathur1990@gmail.com")) {
			maps[0].put(
					Petition_Details_db.KEY_PETITION_ID,
					"aruneshmathur1990@gmail.com"
							+ maps[0].get(Petition_Details_db.KEY_PETITION_ID));
		}

		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient(
					"http://www.petitionsigner.appspot.com/petition_server",
					false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Object result_received = false;
		HashMap<String, Object> result_map = null;

		try {
			result_received = client.invoke(
					"PetitionServer.requestCreatePetition",
					new Object[] { maps[0] });
			System.currentTimeMillis();

		} catch (XmlRpcException e) {
			e.printStackTrace();
			return result_map;
		} catch (XmlRpcFault e) {
			e.printStackTrace();
			return result_map;
		}
		result_map = new HashMap<String, Object>();
		result_map.put(KEY_RESULT_MAP, maps[0]);
		result_map.put(KEY_RESULT_STATUS, result_received);
		return result_map;
	}

}
package com.android.petition;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.android.petition.db.Petition_Details_db;

import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class Signee_SyncTask
		extends
		AsyncTask<ArrayList<HashMap<String, Object>>, Void, HashMap<String, Boolean>> {

	private Context mContext;
	private Petition_Details_db mDatabase;
	private String mToastString;

	public Signee_SyncTask(Context appContext) {
		mContext = appContext;
	}

	@Override
	protected void onPostExecute(HashMap<String, Boolean> result) {

		mDatabase = new Petition_Details_db(mContext);
		if (result != null) {
			mDatabase.open();
			for (String keys : result.keySet()) {
				if (result.get(keys)) {
					mDatabase.setSigneeStatus(keys);
					mToastString = mContext.getResources().getString(
							R.string.signee_send_success);
				}
			}
			mDatabase.close();

		} else {
			mToastString = mContext.getResources().getString(
					R.string.signee_send_failure);
		}
		Toast.makeText(mContext, mToastString, 10).show();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected HashMap<String, Boolean> doInBackground(
			ArrayList<HashMap<String, Object>>... list) {

		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");

		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient(
					"http://www.petitionsigner.appspot.com/petition_server",
					false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Object result = null;
		HashMap<String, Boolean> result_list = null;

		try {
			result = client.invoke("PetitionServer.syncPetition",
					new Object[] { list[0] });
			result_list = (HashMap<String, Boolean>) result;

		} catch (XmlRpcException e) {
			e.printStackTrace();
			return result_list;
		} catch (XmlRpcFault e) {
			e.printStackTrace();
			return result_list;
		}

		return result_list;
	}

}
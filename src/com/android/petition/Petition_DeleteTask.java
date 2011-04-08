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

public class Petition_DeleteTask extends
		AsyncTask<String, Void, HashMap<String, Object>> {

	private Context mContext;
	private Petition_Details_db mDatabase;
	private String mToastString;
	private static String RESULT = "result";

	public Petition_DeleteTask(Context appContext) {
		mContext = appContext;
	}

	@Override
	protected void onPostExecute(HashMap<String, Object> result) {

		if (result != null) {
			String pid = (String) result
					.get(Petition_Details_db.KEY_PETITION_ID);
			Boolean status = (Boolean) result.get(RESULT);
			mDatabase.open();
			if (status) {
				mDatabase.deletePetition(pid);
				mToastString = mContext.getResources().getString(
						R.string.petition_delete_sucess);
			} else {
				mToastString = mContext.getResources().getString(
						R.string.petition_delete_failure);
			}
			mDatabase.updatePetitionXmlrpcStatus(pid, "0");
			mDatabase.close();

		} else {
			mToastString = mContext.getResources().getString(
					R.string.petition_delete_communication_failure);
		}
		Toast.makeText(mContext, mToastString, 10).show();
	}

	@Override
	protected HashMap<String, Object> doInBackground(String... pids) {

		mDatabase = new Petition_Details_db(mContext);
		mDatabase.open();
		String pid = pids[0];
		mDatabase.updatePetitionXmlrpcStatus(pid, "1");
		mDatabase.close();
		HashMap<String, Object> mResult = new HashMap<String, Object>();
		mResult.put(Petition_Details_db.KEY_PETITION_ID, pid);
		mResult.put(RESULT, false);
		if (pid != null) {
			System.setProperty("org.xml.sax.driver",
					"org.xmlpull.v1.sax2.Driver");

			XmlRpcClient client = null;
			try {
				client = new XmlRpcClient(
						"http://www.petitionsigner.appspot.com/petition_server",
						false);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			Object result_received = false;
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Petition_Details_db.KEY_PETITION_ID, pid);

			try {
				result_received = client.invoke(
						"PetitionServer.deletePetition",
						new Object[] { map });
				mResult.put(RESULT, (Boolean) result_received);
			} catch (XmlRpcException e) {
				e.printStackTrace();
				return mResult;
			} catch (XmlRpcFault e) {
				e.printStackTrace();
				return mResult;
			}
		}

		return mResult;

	}
}
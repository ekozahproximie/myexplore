package com.trimble.reporter.looper;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackIncidents {

	public Vector<Incidents> vecIncidents = null;

	public static TrackIncidents parseFromJSON(JSONObject jsonObject) {
		TrackIncidents trackIncidents = new TrackIncidents();
		JSONArray array;
		try {
			array = jsonObject.getJSONArray("entries");

			trackIncidents.vecIncidents = new Vector<Incidents>(array.length());
			// Receive the JSON object from server
			for (int i = 0; i < array.length(); i++) {
				Incidents incidents = Incidents.parseFromJSON(array
						.getJSONObject(i));
				trackIncidents.vecIncidents.add(incidents);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return trackIncidents;
	}
	@Override
	public String toString() {
		String stData=null;
		if(vecIncidents != null){
			stData=vecIncidents.toString();
		}
		return stData;
	}
}

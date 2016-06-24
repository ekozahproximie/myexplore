package com.trimble.reporter.looper;

import org.json.JSONException;
import org.json.JSONObject;

public class Incidents {
	public String incidentId = null;
	public String reporterComment = null;
	public String lat = null;
	public String lon = null;
	public String category = null;
	public String history = null;
	public String status = null;
	public String link = null;
	public String incidentIdInternal=null;

	public static Incidents parseFromJSON(JSONObject jsonObject) {
		Incidents incidents = new Incidents();

		try {
			incidents.incidentId = jsonObject.getString("incidentId");
			incidents.reporterComment = jsonObject.getString("reporterComment");
			incidents.link = jsonObject.getString("link");
			incidents.status = jsonObject.getString("status");
			incidents.lon = jsonObject.getString("lon");
			incidents.lat = jsonObject.getString("lat");
			incidents.category = jsonObject.getString("category");
			incidents.history = jsonObject.getString("history");
			incidents.status = jsonObject.getString("status");
			incidents.incidentIdInternal = jsonObject.getString("incidentIdInternal");
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return incidents;
	}
	@Override
	public String toString() {
		
		return incidentId;
	}

}

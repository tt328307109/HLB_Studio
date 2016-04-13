package com.lk.qf.pay.beans;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BasicResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7314798688932428397L;
	protected String RSPCOD;
	protected String RSPMSG;
	private boolean isSuccess = false;
	private String msg;
	private byte[] response;
	private JSONObject jsonBody;

	// private JSONArray jsonArray;
	public BasicResponse(byte[] response) {
		this.response = response;
	}

	public BasicResponse getResult() throws JSONException {
		if (response != null) {
			JSONObject obj = new JSONObject(new String(response))
					.getJSONObject("REP_BODY");
			this.jsonBody = obj;
			this.msg = obj.optString("RSPMSG");
			if (obj.optString("RSPCOD").equals("000000")) {
				isSuccess = true;
			} else if (obj.optString("RSPCOD").equals("888888")
					|| obj.optString("RSPCOD").equals("888889")) {
				// 长时间未操作超时
				
			} else {
				isSuccess = false;
			}
			return this;
		}
		return null;
	}

	public String getRSPCOD() {
		return RSPCOD;
	}

	public void setRSPCOD(String rSPCOD) {
		RSPCOD = rSPCOD;
	}

	public String getRSPMSG() {
		return RSPMSG;
	}

	public void setRSPMSG(String rSPMSG) {
		RSPMSG = rSPMSG;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public String getMsg() {
		return msg;
	}

	public JSONObject getJsonBody() {
		return jsonBody;
	}

	public void setJsonBody(JSONObject jsonBody) {
		this.jsonBody = jsonBody;
	}

	// public JSONArray getJsonArray() {
	// return jsonArray;
	// }
	//
	// public void setJsonArray(JSONArray jsonArray) {
	// this.jsonArray = jsonArray;
	// }

}

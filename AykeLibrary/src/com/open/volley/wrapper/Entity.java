package com.open.volley.wrapper;

import org.json.JSONObject;

/**
 * Created by Administrator on 15.10.10.
 */
public class Entity implements IParseResponse<JSONObject> {

	private JSONObject data;

	@Override
	public void parse(JSONObject obj) {
		data = obj;
	}

	@Override
	public String toString() {
		if (data != null)
			return data.toString();
		return super.toString();
	}
}

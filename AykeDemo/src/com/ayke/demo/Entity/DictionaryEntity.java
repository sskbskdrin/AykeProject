package com.ayke.demo.Entity;

import com.open.volley.wrapper.IParseResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15.10.10.
 */
public class DictionaryEntity implements IParseResponse<JSONObject> {

	private int errno;
	private String msg;
	private JSONObject data;

	private String name;
	private List<Symbols> mList = new ArrayList<Symbols>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name + "\n");
		for (Symbols symbols : mList) {
			sb.append("英[");
			sb.append(symbols.en);
			sb.append("] ");
			sb.append("美[");
			sb.append(symbols.am);
			sb.append("]\n");
			for (Dictionary dictionary : symbols.list) {
				sb.append(dictionary.part);
				sb.append(" ");
				sb.append(dictionary.means);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	@Override
	public void parse(JSONObject obj) {
		errno = obj.optInt("errNum", -1);
		msg = obj.optString("errMsg", "");
		data = obj.optJSONObject("retData");
		if (data != null) {
			JSONObject result = data.optJSONObject("dict_result");
			name = result.optString("word_name", "");
			JSONArray array = result.optJSONArray("symbols");
			mList = new ArrayList<Symbols>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject sym = array.optJSONObject(i);
				Symbols symbols = new Symbols();
				symbols.am = sym.optString("ph_am");
				symbols.en = sym.optString("ph_en");
				JSONArray symArray = sym.optJSONArray("parts");
				for (int j = 0; j < symArray.length(); j++) {
					Dictionary dictionary = new Dictionary();
					dictionary.part = symArray.optJSONObject(j).optString("part");
					JSONArray means = symArray.optJSONObject(j).optJSONArray("means");
					dictionary.means = "";
					for (int k = 0; k < means.length(); k++)
						dictionary.means += means.optString(k) + ",";
					dictionary.means = dictionary.means.substring(0, dictionary.means.length() -
							1);
					symbols.list.add(dictionary);
				}
				mList.add(symbols);
			}
		}
	}

	private class Symbols {
		String am;
		String en;
		List<Dictionary> list = new ArrayList<Dictionary>();
	}

	private class Dictionary {
		public String part;
		public String means;
	}
}

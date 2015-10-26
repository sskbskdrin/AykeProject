package com.open.volley.wrapper;

import com.open.volley.DefaultRetryPolicy;
import com.open.volley.NetworkResponse;
import com.open.volley.ParseError;
import com.open.volley.Request;
import com.open.volley.Response;
import com.open.volley.RetryPolicy;
import com.open.volley.VolleyError;
import com.open.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

public class VolleyJsonObjectRequest<T extends IParseResponse<JSONObject>> extends
		VolleyRequest<JSONObject> {
	private static final String TAG = VolleyJsonObjectRequest.class
			.getSimpleName();

	private T mResult;
	private IVolleyRequestListener mVolleyRequestListener;

	public VolleyJsonObjectRequest(String url, T result, IVolleyRequestListener listener) {
		super(url);
		mVolleyRequestListener = listener;
		mResult = result;
	}

	public VolleyJsonObjectRequest(String url, T result, IVolleyRequestListener listener, int
			method) {
		super(url, method);
		mVolleyRequestListener = listener;
		mResult = result;
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			return Response.success(new JSONObject(new String(response.data)),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
		retryPolicy = new DefaultRetryPolicy(20 * 1000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return super.setRetryPolicy(retryPolicy);
	}

	@Override
	protected void onSuccessResponse(JSONObject response, int id) {
		if (mVolleyRequestListener != null) {
			mResult.parse(response);
			mVolleyRequestListener.onVolleySuccess(mResult, true, id);
		}
	}

	@Override
	protected void onErrorResponse(VolleyError error, int id) {
		if (mVolleyRequestListener != null)
			mVolleyRequestListener.onVolleyError(error, id);
	}

	public interface IVolleyRequestListener<T extends IParseResponse> {
		public void onVolleySuccess(T result, boolean isSuccess, int id);

		public void onVolleyError(VolleyError error, int id);
	}
}

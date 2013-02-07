package com.ningtest.weizhang;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class GetInfoTask extends AsyncTask<String, Integer, Boolean> {
	protected String _errmsg;
	protected List<NameValuePair> sess_params;
	protected HttpContext hcon;
	//final private String _cookie_sess = "ASPSESSIONIDQCDATRQT", _cookie_token = "CNZZDATA946534";
	public String result;
	private String _type;
	private CookieStore cs;
	
	GetInfoTask() {
		cs = new BasicCookieStore();
		sess_params = new ArrayList<NameValuePair>();
		result = new String();
	}
	
	protected void initPostValues() {}
	
	protected void initCookies(String key, String val) {
		BasicClientCookie bc1 = new BasicClientCookie(key, val);
		bc1.setVersion(0);
        bc1.setDomain(".stc.gov.cn");
        bc1.setPath("/");
        cs.addCookie(bc1);
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO: attempt authentication against a network service.
		String urlstr = params[0];
		//String cookie = params[1];
		//String token = params[2];
		//String code = params[3];
		String useragent = params[1];
		_type = new String(params[2]);
		
		_errmsg = new String();
		
		try {
			hcon = new BasicHttpContext();
			hcon.setAttribute(ClientContext.COOKIE_STORE, cs);
			
			HttpParams httpparam = new BasicHttpParams();
			HttpProtocolParams.setUserAgent(httpparam, useragent);
			
			if ( _type.equals("get") ) {
				HttpGet httpRequest = new HttpGet(urlstr); 
				HttpResponse httpRep = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
				result = EntityUtils.toString(httpRep.getEntity());
			} else {
				initPostValues();
				HttpPost httpRequest = new HttpPost(urlstr);
				httpRequest.setEntity(new UrlEncodedFormEntity(sess_params,HTTP.UTF_8));
				HttpResponse httpRep = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
				result = EntityUtils.toString(httpRep.getEntity());
			}
			
			//_tab = TableAdapter.DecodeHtml(EntityUtils.toString(httpRep.getEntity()), _width);

		} catch ( Exception e ) {
			_errmsg = "stage 3: "+e.toString();
			return false;
		}

		/*
		for (String credential : DUMMY_CREDENTIALS) {
			String[] pieces = credential.split(":");
			if (pieces[0].equals(mEmail)) {
				// Account exists, return true if the password matches.
				return pieces[1].equals(mPassword);
			}
		}
		*/

		// TODO: register the new account here.
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean succ) {
		//showProgress(false);

		
		if ( _type.equals("get") ) {
			onPostExecGet(succ);
			// TODO: get data
			//Bundle sess_data = new Bundle();
			//sess_data.putString("html", result);
			//Intent yunjianIntent = new Intent(LoginActivity.this,NetAppActivity.class);
			//yunjianIntent.putExtras(sess_data);
			
			//startActivity(yunjianIntent);
		} else {
			onPostExecPost(succ);
			// TODO: get data faild
			//mPasswordView.setError(_errmsg);
			//mPasswordView.requestFocus();
		}
	}

	@Override
	protected void onCancelled() {
		//showProgress(false);
	}
	
	protected void onPostExecGet( Boolean succ ) {}
	protected void onPostExecPost( Boolean succ ) {}
}
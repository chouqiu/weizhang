package com.ningtest.weizhang;

import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	final static String _rsturl = "http://www.stc.gov.cn/search/vehicle_peccacy_result_wwww.asp";
	final static String _ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
	private String _chejia, _chepai, _code;
	private TextView _infoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_infoView = (TextView)findViewById(R.id.textView_info);
		_infoView.setVisibility(View.VISIBLE);
		_infoView.setText("debug msg\n\n");
		_infoView.setMovementMethod(LinkMovementMethod.getInstance());
		
		try {
			_code = new String(this.getIntent().getExtras().getString("code"));
		} catch( Exception e ) {
			_code = "";
		}
		
		((Button)findViewById(R.id.button_submit)).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						_chepai = ((EditText)findViewById(R.id.editText_chepai)).getText().toString();
						_chejia = ((EditText)findViewById(R.id.editText_chejia)).getText().toString();
						//_code = new String("6686");
						
						if ( _code.length() > 0 ) {
							_infoView.append("code: "+_code+"\n");
							new GetWeizhangInfo().execute(_rsturl, _ua, "post");
						} else {
							Intent getcode = new Intent(MainActivity.this, PopImgCode.class);
							MainActivity.this.startActivity(getcode);
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private class GetWeizhangInfo extends GetInfoTask {
		
		@SuppressWarnings("unused")
		private void initPostValue() {
			super.initPostValues();
			
			sess_params.add(new BasicNameValuePair("cph",_chepai));
			sess_params.add(new BasicNameValuePair("stc","69404"));
			sess_params.add(new BasicNameValuePair("cl","02"));
			sess_params.add(new BasicNameValuePair("stype","0"));
			sess_params.add(new BasicNameValuePair("fdjh", _chejia));
			sess_params.add(new BasicNameValuePair("image_code", _code));
		}
		
		protected void onPostExecPost(final Boolean succ) {
			if ( succ ) {
				//Toast.makeText(MainActivity.this, "get result: "+result.length(), Toast.LENGTH_LONG).show();
				//_infoView.setVisibility(View.VISIBLE);
				_infoView.setText(result);
			} else {
				//_infoView.setVisibility(View.VISIBLE);
				_infoView.setText("err msg:\n"+_errmsg);
			}
		}
	}

}

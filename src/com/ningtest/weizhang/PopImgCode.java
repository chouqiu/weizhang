package com.ningtest.weizhang;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PopImgCode extends Activity {
	final static String _imgurl = "http://www.stc.gov.cn/search/image_code.asp?rnd=0.06766127818264067";
	final static String _ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
	private ImageView _img;
	private EditText _txt;
	private int _flag;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop);
		
        // Ïê¼ûStrictModeÎÄµµ
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        
		_flag = 0;
		
		_img = (ImageView)findViewById(R.id.pop_img);
		_txt = (EditText)findViewById(R.id.pop_code);
		
		((Button)findViewById(R.id.pop_btn)).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if ( _flag > 0 ) {
							Bundle code = new Bundle();
							code.putString("code", _txt.getText().toString());
							Intent codeIntent = new Intent(PopImgCode.this, MainActivity.class);
							codeIntent.putExtras(code);
							
							PopImgCode.this.startActivity(codeIntent);
						} else {
							try { 
								URL myImgUrl = new URL(_imgurl);
								HttpURLConnection conn = (HttpURLConnection)myImgUrl.openConnection();   
								conn.setDoInput(true);
								conn.connect();   
								InputStream is = conn.getInputStream();
								_img.setImageBitmap(BitmapFactory.decodeStream(is));
								//_img.setVisibility(View.VISIBLE);
								_flag = 1;
							} catch(Exception e) {
								Toast.makeText(PopImgCode.this, e.toString(), Toast.LENGTH_LONG).show();
							}
						}
					}
				});
	}
	
	
	protected class GetWeizhangCode extends GetInfoTask {
		
		protected void onPostExecGet(final Boolean succ) {
			//showProgress(false);

			if ( succ ) {
				// TODO: get data
				//Bundle sess_data = new Bundle();
				//sess_data.putString("html", result);
				//Intent yunjianIntent = new Intent(LoginActivity.this,NetAppActivity.class);
				//yunjianIntent.putExtras(sess_data);
				List<Cookie> cookieLst = cookies.getCookies();
				for ( int i=0; i<cookieLst.size(); ++i ) {
					if ( cookieLst.get(i).getName().equals("_check_app_session") ) {
						mCookie = cookieLst.get(i).getValue();
					} else if ( cookieLst.get(i).getName().equals("remember_token") ) {
						mToken = cookieLst.get(i).getValue();
					}
				}
				
				//startActivity(yunjianIntent);
			} else {
				// TODO: get data faild
				//mPasswordView.setError(_errmsg);
				//mPasswordView.requestFocus();
			}
		}
	}
}

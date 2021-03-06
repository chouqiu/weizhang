package com.ningtest.weizhang;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.cookie.Cookie;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PopImgCode extends Activity {
	final static String _imgurl_old = "http://www.stc.gov.cn/search/image_code.asp?rnd=0.06766127818264067";
	final static String _imgurl = "http://www.stc.gov.cn:8082/szwsjj_web/ImgServlet.action?rnd=";
	final static String _ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
	private ImageView _img;
	private EditText _txt;
	private int _flag;
	private Bundle _param_store;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pop);
		
        // 详见StrictMode文档
		/*
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
        */
		_flag = 0;
		
		_img = (ImageView)findViewById(R.id.pop_img);
		_txt = (EditText)findViewById(R.id.pop_code);
		
		_param_store = new Bundle();
		
		((Button)findViewById(R.id.pop_btn)).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if ( _flag > 0 ) {
							_param_store.putString("code", _txt.getText().toString());
							Intent codeIntent = new Intent(PopImgCode.this, MainActivity.class);
							codeIntent.putExtras(_param_store);
							
							PopImgCode.this.startActivity(codeIntent);
						} else {
							/*
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
							*/
							String imgurl = _imgurl+Math.random();
							new GetWeizhangCode().execute(imgurl, _ua, "get");
						}
					}
				});
		
		String imgurl = _imgurl+Math.random();
		new GetWeizhangCode().execute(imgurl, _ua, "get");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		_flag = 0;
		new GetWeizhangCode().execute(_imgurl, _ua, "get");
		_txt.setHint("等待拉取验证码...");
	}
	
	protected class GetWeizhangCode extends GetInfoTask {
		@Override
		protected void onPreExecute() {
			super.initHeaders("Host", "www.stc.gov.cn:8082");
			super.initHeaders("Referer", "http://www.stc.gov.cn:8082/szwsjj_web/jsp/xxcx/jdcjtwfcx.jsp");
			super.initHeaders("Accept",	"image/webp,*/*;q=0.8");
			super.initHeaders("Accept-Encoding", "gzip,deflate,sdch");
			super.initHeaders("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
		}
		
		protected void onPostExecGet(final Boolean succ) {
			//showProgress(false);

			if ( succ ) {
				_txt.setHint("等待拉取验证码...OK!");
				// TODO: get data
				//Bundle sess_data = new Bundle();
				//sess_data.putString("html", result);
				//Intent yunjianIntent = new Intent(LoginActivity.this,NetAppActivity.class);
				//yunjianIntent.putExtras(sess_data);
				List<Cookie> cookieLst = super.cs.getCookies();
				Pattern ptn = Pattern.compile("JSESSIONID");
				for ( int i=0; i<cookieLst.size(); ++i ) {
					String nn = cookieLst.get(i).getName();
					String vv = cookieLst.get(i).getValue();
					Matcher mm = ptn.matcher(nn);
					if ( mm.find() ) {
						_param_store.putString(nn, vv);	
					}
				}
				
				Bitmap bmp = BitmapFactory.decodeByteArray(result, 0, result.length);
				_img.setImageBitmap(bmp);
				
				// TODO: 暂时屏蔽
				//String rst = tryProcImg(bmp);
				//Toast.makeText(PopImgCode.this, "try decode img: "+rst, Toast.LENGTH_SHORT).show();
				//_txt.setText(rst);
				_flag = 1;
				//startActivity(yunjianIntent);
			} else {
				_txt.setHint("等待拉取验证码...failed!");
				// TODO: get data faild
				//mPasswordView.setError(_errmsg);
				//mPasswordView.requestFocus();
			}
		}
		
		@SuppressLint("SdCardPath")
		private String tryProcImg(Bitmap bmp) {
			TessBaseAPI baseApi=new TessBaseAPI();
			baseApi.init("/sdcard/", "eng");
			baseApi.setImage(bmp);
			String text = new String(baseApi.getUTF8Text());
			baseApi.clear();
			baseApi.end();
			return text;
		}
	}
}

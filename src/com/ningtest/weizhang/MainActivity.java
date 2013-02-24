package com.ningtest.weizhang;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	final static String _rsturl = "http://www.stc.gov.cn/search/vehicle_peccacy_result_wwww.asp";
	final static String _ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
	final static String PREFS_NAME = "weizhang_pref"; 
	private String _chejia, _chepai, _code;
	private TextView _infoView;
	//private GetWeizhangInfo _weizhang;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_infoView = (TextView)findViewById(R.id.textView_info);
		_infoView.setVisibility(View.VISIBLE);
		_infoView.setText("debug msg\n\n");
		_infoView.setMovementMethod(LinkMovementMethod.getInstance());
		_code = new String();
		
		// 载入保存的车牌车架号
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);  
		((EditText)findViewById(R.id.editText_chepai)).setText(settings.getString("chepai", ""));
		((EditText)findViewById(R.id.editText_chejia)).setText(settings.getString("chejia", ""));
		
		((Button)findViewById(R.id.button_submit)).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						_chepai = ((EditText)findViewById(R.id.editText_chepai)).getText().toString();
						_chejia = ((EditText)findViewById(R.id.editText_chejia)).getText().toString();
						try {
							Set<String> ks = MainActivity.this.getIntent().getExtras().keySet();
							GetWeizhangInfo wz = new GetWeizhangInfo();
							Iterator<String> it = ks.iterator();
								
							while ( it.hasNext() ) {
								String kk = it.next();
								String vv = MainActivity.this.getIntent().getExtras().getString(kk);
								
								if (kk.equals("code")) {
									_code = vv;
									_infoView.append("Code: "+_code+"\n");
								} else {
									wz.initCookies(kk, vv);
									_infoView.append(kk+": "+vv+"\n");
								}
							}
							
							_infoView.append("chepai: "+_chepai+"\n");
							wz.execute(_rsturl, _ua, "post");
						} catch ( Exception e ) {
							Intent getcode = new Intent(MainActivity.this, PopImgCode.class);
							MainActivity.this.startActivity(getcode);
						}
						
						// 保存车辆信息
						try {
							SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);  
							SharedPreferences.Editor editor = settings.edit();  
							editor.putString("chepai", _chepai);
							editor.putString("chejia", _chejia);
							// Don't forget to commit your edits!!!  
							editor.commit();
						} catch (Exception e) {
							
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
		@Override
		protected void onPreExecute() {
			super.initHeaders("Referer", "http://www.stc.gov.cn/");
		}
		
		@Override
		protected void initPostValues() {
			super.initPostValues();
			
			sess_params.add(new BasicNameValuePair("cph","粤B"+_chepai));
			sess_params.add(new BasicNameValuePair("stc","69404"));
			sess_params.add(new BasicNameValuePair("cl","02"));
			sess_params.add(new BasicNameValuePair("stype","0"));
			sess_params.add(new BasicNameValuePair("fdjh", _chejia));
			sess_params.add(new BasicNameValuePair("image_code", _code));
			sess_params.add(new BasicNameValuePair("submit", "确 定"));
		}
		
		protected void onPostExecPost(final Boolean succ) {
			if ( succ ) {
				//Toast.makeText(MainActivity.this, "get result: "+result.length(), Toast.LENGTH_LONG).show();
				//_infoView.setVisibility(View.VISIBLE);
				//_infoView.setText(new String(result));
				
				String body;
				try {
					body = new String(result, "GBK");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					body = new String(result);
					_infoView.append("charExcept: "+e.toString()+"\n");
				}
				PatternStat ps = new PatternStat();
				int cnt = ps.Analyse(body);
				_infoView.append("Get result count: "+cnt+"\n\n");
				if ( cnt > 0 ) {
					for ( String ii : ps.getItems() ) {
						_infoView.append(ii+"\n");
					}
				} else {
					_infoView.append(body+"\n");
				}

			} else {
				//_infoView.setVisibility(View.VISIBLE);
				_infoView.setText("err msg:\n"+_errmsg);
			}
		}
	}

	private class PatternStat {
		final static private String _stat_begin = "<tr align=\"center\">(.*?)</tr>";
		final static private String _stat_item = "<td>[\r\n \t]*([^\r\n]*?)[\r\n \t]*</td>";
		private int _cnt;
		private List<String> _items;
		
		PatternStat() {
			_cnt = 0;
			_items = new ArrayList<String>();
		}
		
		List<String> getItems() {
			return _items;
		}
		
		int Analyse( String body ) {
			Pattern pat1 = Pattern.compile(_stat_begin, Pattern.MULTILINE|Pattern.DOTALL);
			Pattern pat2 = Pattern.compile(_stat_item, Pattern.MULTILINE|Pattern.DOTALL);
			Matcher mm1 = pat1.matcher(body);
			_cnt = 0;
			
			String rst = new String();
			while ( mm1.find() ) {
				String it1 = mm1.group(1);
				Matcher mm2 = pat2.matcher(it1);
				while ( mm2.find() ) {
					rst += mm2.group(1) + "  ";
					++_cnt;
					if ( _cnt > 2 ) {
						break;
					}
				}
				
				if ( rst.length() > 0 ) {
					_items.add(rst+"\n");
					rst = "";
					_cnt = 0;
				}
			}
			return _items.size();
		}
	}
}

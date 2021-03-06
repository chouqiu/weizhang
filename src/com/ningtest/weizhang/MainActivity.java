package com.ningtest.weizhang;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
	final static String _rsturl_old = "http://www.stc.gov.cn/search/vehicle_peccacy_result_wwww.asp";
	final static String _rsturl = "http://www.stc.gov.cn:8082/szwsjj_web/JdcjtwfcxServlet";
	final static String _ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
	final static String PREFS_NAME = "weizhang_pref"; 
	private String _chejia, _chepai, _code;
	private TextView _infoView;
	private int _update_cnt = 0;
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
		
		((Button)findViewById(R.id.button_submit)).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
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
							
							_infoView.append(getString(R.string.tips_chepai)+": "+getString(R.string.tips_chepai_prefix, "UTF-8")+_chepai+"\n");
							_infoView.append(getString(R.string.tips_chejia)+": "+_chejia+"\n");
							wz.execute(_rsturl, _ua, "post");
						} catch ( Exception e ) {
							Intent getcode = new Intent(MainActivity.this, PopImgCode.class);
							MainActivity.this.startActivity(getcode);
						}
						
						// 保存车牌信息
						try {
							SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);  
							SharedPreferences.Editor editor = settings.edit();  
							editor.putString("chepai", _chepai);
							editor.putString("chejia", _chejia);
							// Don't forget to commit your edits!!!  
							editor.commit();
						} catch (Exception e) {
							
						}
						
						_update_cnt++;
					}
				});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// 加载车牌信息
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		_chepai = settings.getString("chepai", "");
		_chejia = settings.getString("chejia", "");
		((EditText)findViewById(R.id.editText_chepai)).setText(_chepai);
		((EditText)findViewById(R.id.editText_chejia)).setText(_chejia);
		
		// 如果车牌车架信息有效，直接显示
		if ( _update_cnt <=0 && _chepai.length() > 0 && _chejia.length() > 0 ) {
			((Button)findViewById(R.id.button_submit)).performClick();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
	private class GetWeizhangInfo extends GetInfoTask {
		GetWeizhangInfo() {
			super("UTF-8");
		}
		
		@Override
		protected void onPreExecute() {
			super.initHeaders("Accept", "*/*");
			super.initHeaders("Accept-Encoding", "gzip,deflate,sdch");
			super.initHeaders("Accept-Language", "zh-CN,zh;q=0.8");
			super.initHeaders("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			super.initHeaders("Host", "www.stc.gov.cn:8082");
			super.initHeaders("Origin", "http://www.stc.gov.cn:8082");
			super.initHeaders("Referer", "http://www.stc.gov.cn:8082/szwsjj_web/jsp/xxcx/jdcjtwfcx.jsp");
			super.initHeaders("X-Requested-With", "XMLHttpRequest");
		}
		
		@Override
		protected void initPostValues() {
			super.initPostValues();
			// CXLXMC=jdcjtwf&YANZHEN=fpa2&CPHM=%E7%B2%A4B053hn&CJH=8519&JDCLX=02
			sess_params.add(new BasicNameValuePair("CXLXMC", "jdcjtwf"));
			sess_params.add(new BasicNameValuePair("CPHM", getString(R.string.tips_chepai_prefix, "UTF-8")+_chepai));
			//sess_params.add(new BasicNameValuePair("stc","69404"));
			sess_params.add(new BasicNameValuePair("JDCLX","02"));
			//sess_params.add(new BasicNameValuePair("stype","0"));
			sess_params.add(new BasicNameValuePair("CJH", _chejia));
			sess_params.add(new BasicNameValuePair("YANZHEN", _code));
		}
		
		protected void onPostExecPost(final Boolean succ) {
			if ( succ ) {
				//Toast.makeText(MainActivity.this, "get result: "+result.length(), Toast.LENGTH_LONG).show();
				//_infoView.setVisibility(View.VISIBLE);
				//_infoView.setText(new String(result));
				
				String body;
				try {
					body = new String(result, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					body = new String(result);
					_infoView.append("charExcept: "+e.toString()+"\n");
				}
				PatternStat ps = new PatternStat();
				int cnt = ps.Analyse(body);
				_infoView.append("违章数: "+cnt+"\n");
				_infoView.append("扣分数: "+ps.getPoints()+"\n");
				DecimalFormat df = new DecimalFormat("0,000.00元");
				_infoView.append("罚款数: "+df.format(ps.getMoney())+"\n");
				
				_infoView.append("最后更新时间: "+ps.getUpdateTime()+"\n\n");
				
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
		//final static private String _stat_begin = "<tr align=\"center\">(.*?)</tr>";
		final static private String _stat_begin = "<tr bgcolor=\"#EEF4FD\">(.*?)</tr>";
		//final static private String _stat_item = "<td>[\r\n \t]*([^\r\n]*?)[\r\n \t]*</td>";
		final static private String _stat_item = "<td width=.*? align='center'>[\r\n \t]*([^\r\n<>]*?)[\r\n \t]*</td>";
		final static private String _stat_upt = "<td colspan=\"5\" align=\"center\" height=\"30\"><font color=\"red\">.*?([0-9- :]+)</font></td>";
		private int _cnt;
		private List<String> _items;
		private String _updatetime;
		
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
					if ( _cnt > 3 ) {
						break;
					}
				}
				
				if ( rst.length() > 0 ) {
					_items.add(rst+"\n");
					rst = "";
					_cnt = 0;
				}
			}
			
			Pattern pat_upt = Pattern.compile(_stat_upt, Pattern.MULTILINE|Pattern.DOTALL);
			Matcher mm_upt = pat_upt.matcher(body);
			if ( mm_upt.find() ) {
				_updatetime = mm_upt.group(1);
			}
			return _items.size();
		}
		
		int getPoints() {
			int total = 0;
			Pattern pat = Pattern.compile("/违法记分:([0-9]+)", Pattern.MULTILINE|Pattern.DOTALL);
			
			for ( String ii : _items ) {
				Matcher mm = pat.matcher(ii);
				if ( mm.find() ) {
					total += Integer.parseInt(mm.group(1));
				}
			}
			
			return total;
		}
		
		double getMoney() {
			double total = 0.0;
			Pattern pat = Pattern.compile("/罚款:([0-9]+)", Pattern.MULTILINE|Pattern.DOTALL);
			
			for ( String ii : _items ) {
				Matcher mm = pat.matcher(ii);
				if ( mm.find() ) {
					total += Double.parseDouble(mm.group(1));
				}
			}
			
			return total;
		}
		
		String getUpdateTime() {
			return _updatetime;
		}
	}
}

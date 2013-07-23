package com.example.lirurssreader_v4;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.lirurssreader_v4.MainActivity.MyAsyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.params.CookiePolicy;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.protocol.HTTP;

public class rssFeeds extends Activity {
	
	final String LOG_TAG = "MyLog";
	
	private ListView lvMain;
	LoginDataBaseAdapter loginDataBaseAdapter,loginDataBaseAdapterAT;
	
	String current_task = "";
	
	
	String bbusername,bbuserid,bbpassword,jurl,url;
	// final int PROGRESS_DLG_ID = 6;
		String currentTag = "";
		static Cursor cursor_rep;
		static SimpleCursorAdapter scAdapter;
		
		
		 static void isNetworkAvailable(final Handler handler, final int timeout) {

		        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
		        // the answer must be send before before within the 'timeout' (in milliseconds)

		        new Thread() {

		            private boolean responded = false;

		            @Override
		            public void run() {

		                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)

		                new Thread() {

		                    @Override
		                    public void run() {
		                        HttpGet requestForTest = new HttpGet("http://chizzx.p.ht/");
		                        try {
		                            new DefaultHttpClient().execute(requestForTest); // can last...
		                            responded = true;
		                        } catch (Exception e) {}
		                    }

		                }.start();

		                try {
		                    int waited = 0;
		                    while(!responded && (waited < timeout)) {
		                        sleep(100);
		                        if(!responded ) { 
		                            waited += 100;
		                        }
		                    }
		                } 
		                catch(InterruptedException e) {} // do nothing 
		                finally { 
		                    if (!responded) { handler.sendEmptyMessage(0); } 
		                    else { handler.sendEmptyMessage(1); }
		                }

		            }

		        }.start();
			 }

		
		
	public static XmlPullParser prepareXpp(BufferedReader rd) throws XmlPullParserException {
		    // получаем фабрику
		    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    // включаем поддержку namespace (по умолчанию выключена)
		    factory.setNamespaceAware(true);
		    // создаем парсер
		    XmlPullParser xpp = factory.newPullParser();
		    // даем парсеру на вход Reader
		    xpp.setInput(rd);
		    return xpp;
		  }
		
		
		public String DescrPrepare(String desc) {
			String result = "";
			return result;
		}
		
		// For the tags title and summary, extracts their text values.
		private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		    String result = "";
		    if (parser.next() == XmlPullParser.TEXT) {
		        result = parser.getText();
		        parser.nextTag();
		    }
		    return result;
		}
		
	 
	 public static String getContentCharSet(final HttpEntity entity) throws ParseException {
		 
			if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
			 
			String charset = null;
			 
			if (entity.getContentType() != null) {
			 
			HeaderElement values[] = entity.getContentType().getElements();
			 
			if (values.length > 0) {
			 
			NameValuePair param = values[0].getParameterByName("charset");
			 
			if (param != null) {
			 
			charset = param.getValue();
			 
			}
			 
			}
			 
			}
			 
			return charset;
			 
			}
	 
	 public static class MyAsyncTask extends CustomAsyncTask<String, Void,  String> {
	 static ArrayList<post> objects;
	 LoginDataBaseAdapter loginDataBaseAdapterMT;
	 String position;
	 String jidStr,postLink;
	 
	 
	
	 
	 
	 private static ProgressDialog mProgress;
		private int mCurrProgress;

		public MyAsyncTask(rssFeeds activity) {
			super(activity);
		}	
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//if (mProgress==null)
			showProgressDialog();
		}
		
		@Override
		protected void onActivityDetached() {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		}

		@Override
		protected void onActivityAttached() {
		//	if (mProgress == null)
			showProgressDialog();
		}

		private void showProgressDialog() {
			//if (mProgress == null)
			mProgress = new ProgressDialog(mActivity);
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setMessage("Обновление RSS лент...");
			mProgress.setCancelable(true);
		    
			mProgress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});

			mProgress.show();
			mProgress.setProgress(mCurrProgress);
		}

		
		
			protected void onPostExecute(String name) {
			//super.onPostExecute(params);
				
			if (mActivity != null) {
			   	 mProgress.dismiss();
			  	 loginDataBaseAdapterMT=new LoginDataBaseAdapter(mActivity);
			     loginDataBaseAdapterMT=loginDataBaseAdapterMT.open();
			  //  loginDataBaseAdapterMT.updateEntryRSS(params.get(1), params.get(0));
			     cursor_rep = loginDataBaseAdapterMT.getRSSNames(name);
				 scAdapter.changeCursor(cursor_rep);
			     loginDataBaseAdapterMT.close();
				
				//Toast.makeText(mActivity, "Обновление завершено", Toast.LENGTH_LONG).show();
			}
			else {
				//Log.d(TAG, "AsyncTask finished while no Activity was attached.");
			}
				//Toast.makeText(rssFeeds.this, "Count:" + params.get(0) +"URL:" + params.get(1), Toast.LENGTH_LONG).show();
					// изменить соответствующий textView
				// dismissDialog(PROGRESS_DLG_ID);
				
				
				// lvMain.setAdapter(scAdapter);
				 // update table RSS
				 
	  		    // View convertedView = (View) lvMain.getAdapter().getItem(Integer.parseInt(position));
	  		    
	  		    // TextView Text1 = (TextView)convertedView.findViewById(R.id.tvNew);
	  		  //  Text1.setText("йохоу мазафака");
	  		    //listview.getItemAtPosition(position).getString(0);
				
				 
				 /*
				// add objects to CustomAdapter
							CustomAdapter customAdapter = new CustomAdapter(ViewActivity.this, objects);
							// bind adapter to ListView
							listview =  (ListView) findViewById(R.id.lView);
							listview.setAdapter(customAdapter);
							listview.setOnItemClickListener(new OnItemClickListener() {
							      public void onItemClick(AdapterView<?> parent, View view,
							          int position, long id) {
							    	
							    	//  final String user = (String) ((Cursor) listview
								  		//		.getItemAtPosition(position)).getString(1);
							    	  
							    	  cAdName = (TextView) view.findViewById(R.id.tvLink);
							    	  String goTo = cAdName.getText().toString();
							    	  if (!goTo.startsWith("http://") && !goTo.startsWith("https://"))
							    		  goTo = "http://" + goTo;
							    	  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(goTo));
							    	  startActivity(browserIntent);
							    	//  Toast.makeText(ViewActivity.this, "URLtoGO:" + cAdName.getText().toString(), Toast.LENGTH_LONG).show();
							    	  
			 
							}
							 });*/
			}
		
			protected void onProgressUpdate(Integer... progress) {
				mCurrProgress = progress[0];
				if (mActivity != null) {
					mProgress.setProgress(mCurrProgress);	
				}
				else {
					//Log.d(TAG, "Progress updated while no Activity was attached.");
				}
			}
			
		
			 
			 
			@Override
	        protected  String doInBackground(String... params) {
	        	// publishProgress(new Void[]{});
	        	
				
				
				
				// int position = 1;
				
				 
				// 
				  
				  
					try {
						
						// String mUrl = params[0];
						 String jurl = params[0];
						 String bbuserid = params[1];
						 String bbpassword = params[2];
						 String bbusername = params[3];
						// String numload = params[4];
						 
						loginDataBaseAdapterMT=new LoginDataBaseAdapter(mActivity);
					    loginDataBaseAdapterMT=loginDataBaseAdapterMT.open();
						String numl = loginDataBaseAdapterMT.getNUMLOAD(bbusername);
						if (numl.equals("NOT EXIST"))
						numl="1000";
						
						
						 Cursor cursor_all = loginDataBaseAdapterMT.getAlldata();
						 
						 while (cursor_all.moveToNext()) {
							 
								// определяем номера столбцов по имени в выборке
					    	    //  int idColIndex = cursor_all.getColumnIndex("USER");
					    	      // получаем значения по номерам столбцов 
					    	   //  String USER = cursor_all.getString(idColIndex);
					    	     
					    	 	// определяем номера столбцов по имени в выборке
					    	      int idColIndex = cursor_all.getColumnIndex("URL");
					    	      // получаем значения по номерам столбцов 
					    	      String mUrl = cursor_all.getString(idColIndex);
					    	   //  MyAsyncTask mt = new MyAsyncTask();
					    	    // mt.execute(URL,jurl,bbuserid,bbpassword,bbusername,numl);
					    	 
					    	    // new MyAsyncTask(rssFeeds.this).execute(URL,jurl,bbuserid,bbpassword,bbusername,numl);
					    	 	 DefaultHttpClient  client = new DefaultHttpClient();
								 client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
							
								
								// position = params[6];
								

								 HttpGet httpGet = new HttpGet(mUrl); 
								 httpGet.addHeader("Cookie", "chbx=guest; jurl="+jurl+"; ucss=normal; bbuserid="+bbuserid+"; bbpassword="+bbpassword+"; bbusername="+bbusername);			
								 HttpResponse response = client.execute(httpGet);
								
							
								
								 objects = new ArrayList<post>();
								// String mTitle = "";
								// String mDescr = "";
								 String pdaUrl = "";
								// String mpubDate = "";
								
								 String charset = getContentCharSet(response.getEntity());
								 
								 if (charset == null) {
								  
								 charset = HTTP.DEFAULT_CONTENT_CHARSET;
								  
								 }
								 
								BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),charset));
								XmlPullParser xpp = prepareXpp(rd);
								boolean itemStart = false;
								int i = 0;
								int countNew = 0;
								while ((xpp.getEventType() != XmlPullParser.END_DOCUMENT) && (i<Integer.parseInt(numl))) {
									  switch (xpp.getEventType()) {
									  case XmlPullParser.START_DOCUMENT:
								         // Log.d(LOG_TAG, "START_DOCUMENT");
								          break;
								        // начало тэга
								        case XmlPullParser.START_TAG:
								        	//currentTag = xpp.getName();
								        	if (xpp.getName().equals("item")) itemStart = true;
								        	
								        	if (xpp.getName().equals("link")&&itemStart)
								        	{
								        		postLink = readText(xpp);
								        		//Log.w(LOG_TAG,postLink);
								        		Pattern pattern = Pattern.compile("post(\\d+)");
								        		Matcher matcher = pattern.matcher(postLink);
								        		if (matcher.find())
								        		{
								        			postLink=matcher.group(1);
								        			//Log.w(LOG_TAG,postLink);
								        		}
								        	}
								        	
								        	if (xpp.getName().equals("url"))
								        	{
								        		jidStr = readText(xpp);
								        		// разобрать jidStr, вернуть jid
								        		//Log.w(LOG_TAG,jidStr);
								        		Pattern pattern = Pattern.compile("(\\d+)_(\\d+)");
								        		Matcher matcher = pattern.matcher(jidStr);
								        		if (matcher.find())
								        		{
								        			jidStr=matcher.group(1);
								        		}
								        		
								        	}
								        	 //	Log.w(LOG_TAG, "text = " + readText(xpp));
								        		
								        //  Log.d(LOG_TAG, "START_TAG: name = " + xpp.getName()
								        //      + ", depth = " + xpp.getDepth() + ", attrCount = "
								        //      + xpp.getAttributeCount());
								        //  tmp = "";
								        //  for (int i = 0; i < xpp.getAttributeCount(); i++) {
								        //    tmp = tmp + xpp.getAttributeName(i) + " = "
								        //        + xpp.getAttributeValue(i) + ", ";
								      //    }
								        //  if (!TextUtils.isEmpty(tmp))
								       //     Log.d(LOG_TAG, "Attributes: " + tmp);
								          break;
								        // конец тэга
								        case XmlPullParser.END_TAG:
								        	if (xpp.getName().equals("item"))
								        	{
								        		pdaUrl = "http://www.li.ru/interface/pda/?act=comments&jid="+jidStr+"&pid="+postLink;
								        		//post mPost = new post(mTitle,mDescr,pdaUrl,mpubDate);
								        		//objects.add(mPost);
								        		// вот тут надо увеличить count и передать его в PostExecute
								        		String UrlFromDB = loginDataBaseAdapterMT.getSinlgeEntryURLF(pdaUrl);
								        		//Log.w(LOG_TAG,UrlFromDB);
								        		//Log.w(LOG_TAG,pdaUrl);
								        		if (UrlFromDB.equals("NOT EXIST"))
								        		countNew++;
								        		
								        		i++; // numload count
								    	      }
								       //   Log.d(LOG_TAG, "END_TAG: name = " + xpp.getName());
								          break;
								        // содержимое тэга
								        case XmlPullParser.TEXT:
								        
								      //    Log.d(LOG_TAG, "text = " + xpp.getText());
								          break;

								        default:
								      //    break;
									  }
									  // следующий элемент
								        xpp.next();
								}
								
								
								 loginDataBaseAdapterMT.updateEntryRSS(mUrl, Integer.toString(countNew)); 
							 }	
						    loginDataBaseAdapterMT.close();
							//list.add();
							//list.add(mUrl);
							//list.add(bbusername);
						    cursor_all.close(); 
						    return bbusername;
						
						// return list; 
					
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					loginDataBaseAdapterMT.close();
					return null; 
				
			 }
			
			
		}
	 
/*	 
	 @Override
	 public void onPause() {
	     super.onPause();  // Always call the superclass method first
	     cursor_upd.close();
	     // Release the Camera because we don't need it when paused
	     // and other activities might need to use it.
	    
	 }
	 */
	 
/*	 @Override
	 public void onResume()
	 {
		    super.onResume();
			//loginDataBaseAdapterAT=new LoginDataBaseAdapter(this);
			//loginDataBaseAdapterAT=loginDataBaseAdapterAT.open();
		    cursor_upd = loginDataBaseAdapter.getRSSNames(bbusername);
			scAdapter.changeCursor(cursor_upd);
			//loginDataBaseAdapterAT.close();
	 }*/
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		
		setTitle("My rss feeds");
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        Intent intent = getIntent();
        bbusername = intent.getStringExtra("bbusername");
        bbuserid = intent.getStringExtra("bbuserid");
	    bbpassword = intent.getStringExtra("bbpassword");
	  //  String bbusername = intent.getStringExtra("bbusername");
	 //   String bbredirect = intent.getStringExtra("bbredirect");
	    jurl = intent.getStringExtra("jurl");
    
        cursor_rep = loginDataBaseAdapter.getRSSNames(bbusername);
      //  startManagingCursor(cursor_rep);
     
    	// Toast.makeText(rssFeeds.this, "RSS Feeds not found for user:" + bbusername, Toast.LENGTH_LONG).show();
     //}
        
        
    	// формируем столбцы сопоставления
		String[] from = new String[] { "mname" };
		int[] to = new int[] { R.id.tvRss };
		
		// создааем адаптер и настраиваем список
		scAdapter = new SimpleCursorAdapter(rssFeeds.this, R.layout.itemrss, cursor_rep, from,
				to);
		lvMain = (ListView) findViewById(R.id.lvRep);
		lvMain.setAdapter(scAdapter);
	 	//cursor_rep.close();
		
		lvMain.setOnItemLongClickListener(new OnItemLongClickListener() {
	        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	            //Log.e("MyApp", "get onItem Click position= " + position);
	      	  final String user = (String) ((Cursor) lvMain
		  				.getItemAtPosition(position)).getString(0);
	      	  
	      	AlertDialog.Builder ad;
	    	Context context;
	    	context = rssFeeds.this;
	    	String title = "Удалить RSS: "+user+"?";
	    	ad = new AlertDialog.Builder(context);
	    	ad.setTitle(title);  // заголовок
	    	String button1String = "Да";
			String button2String = "Нет";
	    	
	    	ad.setPositiveButton(button1String, new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
				//	Toast.makeText(context, "Вы сделали правильный выбор",
					//		Toast.LENGTH_LONG).show();
					loginDataBaseAdapter.deleteEntryRSS(user);
					cursor_rep = loginDataBaseAdapter.getRSSNames(bbusername);
					scAdapter.changeCursor(cursor_rep);
					
				}
			});
			ad.setNegativeButton(button2String, new OnClickListener() {
				public void onClick(DialogInterface dialog, int arg1) {
				//	Toast.makeText(context, "Возможно вы правы", Toast.LENGTH_LONG)
				//			.show();
				}
			});
	    	
			ad.show();
	        	return true;
	        }
	    });
		
	
		lvMain.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view,
		          int position, long id) {
		    	  final String user = (String) ((Cursor) lvMain
		  				.getItemAtPosition(position)).getString(0);
		    	  
		    	  Cursor cursor_name = loginDataBaseAdapter.getRSSbyName(user);
		    	  if (cursor_name.moveToFirst()) {
		    		// определяем номера столбцов по имени в выборке
		    	      int idColIndex = cursor_name.getColumnIndex("mUrl");
		    	      // получаем значения по номерам столбцов 
		    	     String URL = cursor_name.getString(idColIndex);
		    	     // вызываем Intent, передавая URL
		    	 	Intent intent = new Intent(rssFeeds.this, ViewActivity.class);
					intent.putExtra("bbusername", bbusername);
					intent.putExtra("bbuserid", bbuserid);
					intent.putExtra("bbpassword", bbpassword);
					intent.putExtra("jurl", jurl);
					intent.putExtra("url", URL);
					String numl = loginDataBaseAdapter.getNUMLOAD(bbusername);
					if (!numl.equals("NOT EXIST"))
					intent.putExtra("numload", numl);
					else intent.putExtra("numload", "1000");
					String fastload = loginDataBaseAdapter.getFastLoad(bbusername);
					intent.putExtra("fastload", fastload);
					startActivity(intent);
		    	  }
		    			  cursor_name.close();
		    	//  Toast.makeText(
		  		//		rssFeeds.this,
		  		//		"Single tap on item position " + position + " Selected Item:"
		  		//				+ user, Toast.LENGTH_SHORT).show();
		    	  
		      }
		    });
		
        
	}
	
	protected void onDestroy() {
		// закрываем подключение при выходе
				
			//	loginDataBaseAdapter.close();
		super.onDestroy();
		loginDataBaseAdapter.close();
		cursor_rep.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 getMenuInflater().inflate(R.menu.main, menu);

		//menu.add(0, 1, 0, "addRssFeed");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// StringBuilder sb = new StringBuilder();

		// Выведем в TextView информацию о нажатом пункте меню
		// sb.append("Item Menu");
		// sb.append("\r\n groupId: " + String.valueOf(item.getGroupId()));
		// sb.append("\r\n itemId: " + String.valueOf(item.getItemId()));
		// sb.append("\r\n order: " + String.valueOf(item.getOrder()));
		// sb.append("\r\n title: " + item.getTitle());
		// tv.setText(sb.toString());
		Toast.makeText(rssFeeds.this, "Menu title:" + item.getTitle().toString(), Toast.LENGTH_LONG).show();
		
		if (item.getTitle().toString().equals(getString(R.string.menuAdd))) {
			Intent intent = new Intent(rssFeeds.this, addSkill.class);
			intent.putExtra("USER", bbusername);
			startActivity(intent);
		}
		if (item.getTitle().toString().equals(getString(R.string.action_settings))) {
			Intent intent = new Intent(rssFeeds.this, options.class);
			intent.putExtra("USER", bbusername);
			startActivity(intent);
		}
		if (item.getTitle().toString().equals(getString(R.string.reload))) {
			//Intent intent = new Intent(rssFeeds.this, options.class);
			//intent.putExtra("USER", bbusername);
			//startActivity(intent);
			
			Handler h = new Handler() {

     		    @Override
     		    public void handleMessage(Message msg) {

     		        if (msg.what != 1) { Toast.makeText(rssFeeds.this, "No internet connection. Try again.", Toast.LENGTH_LONG).show();

     		        } else {  new MyAsyncTask(rssFeeds.this).execute(jurl,bbuserid,bbpassword,bbusername);
                 				

     		        }

     		    }
     		};
    		
     		isNetworkAvailable(h,5000);
			
			
			
			 
		//	 cursor_rep = loginDataBaseAdapter.getRSSNames(bbusername);
		//	 scAdapter.changeCursor(cursor_rep);
			 
			// cursor_rep.close();

					 
			// прочитать все 
		}
		

		return super.onOptionsItemSelected(item);
	}
	
	 @Override
	    public void onSaveInstanceState(Bundle outState) {
	    	super.onSaveInstanceState(outState);
	    	
	    	((CustomApplication) getApplication()).detach(this);
	    }
	    
	    @Override
	    public void onRestoreInstanceState(Bundle savedInstanceState) {
	    	super.onRestoreInstanceState(savedInstanceState);
	    	
	    	((CustomApplication) getApplication()).attach(this);
	    }

	

/*	@Override
    protected Dialog onCreateDialog(int dialogId){
        ProgressDialog progress = null;
        switch (dialogId) {
        case PROGRESS_DLG_ID:
            progress = new ProgressDialog(this);
                progress.setMessage("Loading...");
            
            break;
        }
        return progress;
    }*/
	
	
	
}

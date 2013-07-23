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

import com.example.lirurssreader_v4.rssFeeds.MyAsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.params.CookiePolicy;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.protocol.HTTP;

public class ViewActivity extends Activity {
	
	final String LOG_TAG = "MyLog";
	String currentTag = "";
	static String jidStr,postLink;
	private static ListView listview;
	private static TextView cAdName;
	//MyAsyncTask mt;
    //final int PROGRESS_DLG_ID = 6;
	
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

	
	public static class MyAsyncTask extends CustomAsyncTask<String, Void,  ArrayList<post>> {
		
		ArrayList<post> objects;
		LoginDataBaseAdapter loginDataBaseAdapter;
	
		private ProgressDialog mProgress;
		private int mCurrProgress;

		public MyAsyncTask(ViewActivity activity) {
			super(activity);
		}	
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
			showProgressDialog();
		}

		private void showProgressDialog() {
		//	if (mProgress == null)  
			mProgress = new ProgressDialog(mActivity);
			mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgress.setMessage("Загрузка RSS ленты...");
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
		
		protected void onPostExecute(ArrayList<post> objects) {
			if (mActivity != null) {
				mProgress.dismiss();
				CustomAdapter customAdapter = new CustomAdapter(mActivity, objects);
				listview =  (ListView) mActivity.findViewById(R.id.lView);
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
				    	  mActivity.startActivity(browserIntent);
				    	//  Toast.makeText(ViewActivity.this, "URLtoGO:" + cAdName.getText().toString(), Toast.LENGTH_LONG).show();
				    	  
 
				}
				 });
				Toast.makeText(mActivity, "Загрузка RSS ленты завершена", Toast.LENGTH_LONG).show();
			}
			else {
				//Log.d(TAG, "AsyncTask finished while no Activity was attached.");
			}
			// dismissDialog(PROGRESS_DLG_ID);
			// add objects to CustomAdapter
						
						// bind adapter to ListView
				
		}
	
		
        protected void onProgressUpdate(Integer... progress) {
           // super.onProgressUpdate(values);
            mCurrProgress = progress[0];
			if (mActivity != null) {
				mProgress.setProgress(mCurrProgress);	
			}
			else {
				//Log.d(TAG, "Progress updated while no Activity was attached.");
			}
          //  showDialog(PROGRESS_DLG_ID);
        }
		 
		 
		@Override
        protected  ArrayList<post> doInBackground(String... params) {
        	 //publishProgress(new Void[]{});
				try {
					
					loginDataBaseAdapter=new LoginDataBaseAdapter(mActivity);
			        loginDataBaseAdapter=loginDataBaseAdapter.open();
					 DefaultHttpClient  client = new DefaultHttpClient();
					 client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
					 String mUrl = params[0];
					 String jurl = params[1];
					 String bbuserid = params[2];
					 String bbpassword = params[3];
					 String bbusername = params[4];
					 String numload = params[5];
					 String fastload = params[6];
					
					// boolean howToLoad = true;
					 HttpResponse response;
// если мы делаем через лиру, то это
					 if (fastload.equals("false"))
					 {
					 HttpGet httpGet = new HttpGet(mUrl); 
					 httpGet.addHeader("Cookie", "chbx=guest; jurl="+jurl+"; ucss=normal; bbuserid="+bbuserid+"; bbpassword="+bbpassword+"; bbusername="+bbusername);			
					 response = client.execute(httpGet);
					 }
					 else
					 {
						// mUrl = "http://liveinternet.ru/users/chert/rss/";
						   // client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
						    List<NameValuePair> formparams = new ArrayList<NameValuePair>(6);
						   // bbusername = "Chizz_TecTeP";
						    formparams.add(new BasicNameValuePair("jurl", jurl));
			        		formparams.add(new BasicNameValuePair("bbuserid", bbuserid));
			        		formparams.add(new BasicNameValuePair("bbpassword", bbpassword));
			        		formparams.add(new BasicNameValuePair("bbusername", bbusername));
			        		formparams.add(new BasicNameValuePair("url", mUrl));
			        		formparams.add(new BasicNameValuePair("count", numload));
			        		
			        	/*	Log.d("myLogs", jurl  );
			        		Log.d("myLogs", bbuserid  );
			        		Log.d("myLogs", bbpassword  );
			        		Log.d("myLogs", bbusername  );
			        		Log.d("myLogs", mUrl  );
			        		*/
			        		
			        		
			        		
			        		
			 	    //	formparams.add(new BasicNameValuePair("username", params[0]));
			 	    //	formparams.add(new BasicNameValuePair("password", params[1]));
			 	    	//formparams.add(new BasicNameValuePair("action", "http://chizzx.p.ht/rss/rss.php"));
			 	    	HttpEntity entity;
			 		
			 				entity = new UrlEncodedFormEntity(formparams); // , "UTF-8"
			 				HttpPost httppost = new HttpPost("http://chizzx.p.ht/rss/rss.php");
			 	   			httppost.setEntity(entity);
			 	   		    response = client.execute(httppost);
					 }
					 // если через chizzx.p.ht, то это
					
				
			
					 objects = new ArrayList<post>();
					 String mTitle = "";
					 String mDescr = "";
					 String pdaUrl = "";
					 String mpubDate = "";
					
					/* String charset = getContentCharSet(response.getEntity());
					 
					 if (charset == null) {
					  
					 charset = HTTP.DEFAULT_CONTENT_CHARSET;
					  
					 }*/
					 
					 
					 
					BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));//,charset
				/*	String sCurrentLine;
					while ((sCurrentLine = rd.readLine()) != null) {
						//System.out.println(sCurrentLine);
						Log.d("myLogs", sCurrentLine  );
					}*/
		 
					XmlPullParser xpp = prepareXpp(rd);
					boolean itemStart = false;
					int i = 0;
					while ((xpp.getEventType() != XmlPullParser.END_DOCUMENT) && (i<Integer.parseInt(numload))) {
						  switch (xpp.getEventType()) {
						  case XmlPullParser.START_DOCUMENT:
					         // Log.d(LOG_TAG, "START_DOCUMENT");
					          break;
					        // начало тэга
					        case XmlPullParser.START_TAG:
					        	//currentTag = xpp.getName();
					        	if (xpp.getName().equals("item")) itemStart = true;
					        	if (xpp.getName().equals("title")&&itemStart)
					        		mTitle = readText(xpp);
					        	if (xpp.getName().equals("description")&&itemStart)
					        		mDescr = readText(xpp);
					        	if (xpp.getName().equals("pubDate")&&itemStart)
					        		mpubDate = readText(xpp);
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
					        		post mPost = new post(mTitle,mDescr,pdaUrl,mpubDate);
					        		objects.add(mPost);
					        		// also add record in FEEDS table
					        		loginDataBaseAdapter.insertEntryFeed(bbusername, mTitle, mpubDate, pdaUrl);
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
					loginDataBaseAdapter.close();
					 return objects; 
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
				loginDataBaseAdapter.close();
				return objects; 
			
		 }
		
		
	}
	
	private static XmlPullParser prepareXpp(BufferedReader rd) throws XmlPullParserException {
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
	

	
	

	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewactivity);
		
		Intent intent = getIntent();
		
		
	    String  bbuserid = intent.getStringExtra("bbuserid");
	    String bbpassword = intent.getStringExtra("bbpassword");
	    String bbusername = intent.getStringExtra("bbusername");
	 //   String bbredirect = intent.getStringExtra("bbredirect");
	    String jurl = intent.getStringExtra("jurl");
	    String mUrl = intent.getStringExtra("url");
	    String mNumload = intent.getStringExtra("numload");
	    String fastload = intent.getStringExtra("fastload");
	    
	    // String mUrl = params[0];
		// String jurl = params[1];
		// String bbuserid = params[2];
		// String bbpassword = params[3];
		// String bbusername = params[4];

	   // mt = new MyAsyncTask();
       // mt.execute(mUrl,jurl,bbuserid,bbpassword,bbusername,mNumload);
	    
	    
	   new MyAsyncTask(ViewActivity.this).execute(mUrl,jurl,bbuserid,bbpassword,bbusername,mNumload,fastload);
             				

 	
	   
		 
		 
		 
	    //Toast.makeText(ViewActivity.this, "bbuserid:" + fName + " bbpassword: " + lName, Toast.LENGTH_LONG).show();
	    
	    
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
	

}

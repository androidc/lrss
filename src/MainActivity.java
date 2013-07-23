package com.example.lirurssreader_v4;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.params.CookiePolicy;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;


public class MainActivity extends Activity {
	
	ProgressDialog progress;
	HttpPost httppost;
	
	
	
	//MyAsyncTask mt;

	LoginDataBaseAdapter loginDataBaseAdapter;
	
	final int PROGRESS_DLG_ID = 6;
	
	/*@Override
    protected Dialog onCreateDialog(int dialogId){
        ProgressDialog progress = null;
        switch (dialogId) {
        case PROGRESS_DLG_ID:
            progress = new ProgressDialog(this);
                progress.setMessage("Loading...");
            
            break;
        }
        return progress;
    }
	*/
	
	
	
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

   
	public boolean isOnline(){
		ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if(netInfo !=null&& netInfo.isConnectedOrConnecting())
		{		
			return true;}
		return false;
		}
	
	private boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    
	    return haveConnectedWifi || haveConnectedMobile ;
/*	    if (haveConnectedWifi || haveConnectedMobile)
	    {
	    	try {
	    	 InetAddress addr = InetAddress.getByName(URL);
	            if(addr!=null){
	               return true;
	            }
	    	}
	     catch (UnknownHostException e) {
            Log.d("RemoteDnsCheck", "UnknownHostException");
        }*/
	
	}
	
	public static class MyAsyncTask extends CustomAsyncTask<String, Void, List<Cookie>> {
		 
		
		LoginDataBaseAdapter loginDataBaseAdapterAT;
		private String userName,password;
		 List<Cookie> cookies ;
		 
		 private ProgressDialog mProgress;
	        private int mCurrProgress;
		 
		 	   
		 public MyAsyncTask(MainActivity activity) {
	            super(activity);
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
		 
		  @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            showProgressDialog();
	        }
		  
		  private void showProgressDialog() {
			  //  if (mProgress == null)  
			    mProgress = new ProgressDialog(mActivity);
	            mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            mProgress.setMessage("Авторизация на liveinternet...");
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
		    
		   
		
		  protected void onPostExecute(List<Cookie> cookies) {
			
			  
			  if (mActivity != null) {
	                mProgress.dismiss();
	                if (cookies.isEmpty()) {
					     //for (Cookie a : cookies)
					   // cookieStore.addCookie(a);
						Toast.makeText(mActivity, "Error register. Username or password incorrect.", Toast.LENGTH_LONG).show();
					 } else {
						 Intent intent = new Intent(mActivity,rssFeeds.class);
						 loginDataBaseAdapterAT=new LoginDataBaseAdapter(mActivity);
					     loginDataBaseAdapterAT=loginDataBaseAdapterAT.open();
					     if (loginDataBaseAdapterAT.getSinlgeEntry(userName).equals("NOT EXIST"))
					     { loginDataBaseAdapterAT.insertEntry(userName, password);
					       loginDataBaseAdapterAT.insertEntryUsers(userName, "0");}
					     else
					         loginDataBaseAdapterAT.updateEntryLogin(userName,password);
					     
					     
					     loginDataBaseAdapterAT.updateEntryLastin(userName);
					     
					     loginDataBaseAdapterAT.close();
					     for (Cookie a : cookies) {
					      //   cookieStore.addCookie(a);
					       //  Log.w("MyLog","- " + a.getName().toString());
					       //  Log.w("MyLog","- " + a.getValue().toString());
					    	 
					    	 intent.putExtra(a.getName().toString(), a.getValue().toString());
					    	 
					     }
					    
					     // 
					     // loginDataBaseAdapterAT.updateEntryLogin(userName,password);
					     // 
					     mActivity.startActivity(intent);
					   //  Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
					    // dialog.dismiss();
					     
					    
					 } 
	             //   Toast.makeText(mActivity, "Авторизация ", Toast.LENGTH_LONG).show();
	            }
	            else {
	               // Log.d(TAG, "AsyncTask finished while no Activity was attached.");
	            }
			  
			  
			  
			
		    }
		  
		  
		@Override
		protected List<Cookie> doInBackground(String... params) {
			
			
		 DefaultHttpClient  client;
		 
	//	 publishProgress(new Void[]{}); //or null
		 userName = params[0];
		 password = params[1];
		    
         // fetch the Password form database for respective user name
		// loginDataBaseAdapterAT=new LoginDataBaseAdapter(MainActivity.this);
		// loginDataBaseAdapterAT=loginDataBaseAdapterAT.open();
		 
		 try
		 {
			 client =  new DefaultHttpClient();
	      	   
	      	   client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	      	   //mt = new MyTask();
	      	  // mt.execute(userName,storedPassword);
	      	   List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	     		    formparams.add(new BasicNameValuePair("username", userName));
	        		formparams.add(new BasicNameValuePair("password", password));
	 	    //	formparams.add(new BasicNameValuePair("username", params[0]));
	 	    //	formparams.add(new BasicNameValuePair("password", params[1]));
	 	    	formparams.add(new BasicNameValuePair("action", "login"));
	 	    	HttpEntity entity;
	 		
	 				entity = new UrlEncodedFormEntity(formparams, "UTF-8");
	 				HttpPost httppost = new HttpPost("https://www.liveinternet.ru/member.php?rndm=1234567890");
	 	   			httppost.setEntity(entity);
	 	   		    client.execute(httppost);
	 	   		    cookies = client.getCookieStore().getCookies();
	 	   		    //loginDataBaseAdapterAT.close();
				    return cookies;
		 } catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	
			 catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
        /* String storedPassword=loginDataBaseAdapterAT.getSinlgeEntry(userName);
         if (!storedPassword.equals("NOT EXIST"))
         {
      		try { 
      			 // create the instance of Databse
      	      //   loginDataBaseAdapter=new LoginDataBaseAdapter(MainActivity.this);
      	   
      			loginDataBaseAdapterAT.updateEntryLastin(userName);
      	   // соединиться
      	   // достать куки и сохранить их.
      	   // TODO открыть другой активити, передать ему куки. 
      	   
      	   client =  new DefaultHttpClient();
      	   
      	   client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
      	   //mt = new MyTask();
      	  // mt.execute(userName,storedPassword);
      	   List<NameValuePair> formparams = new ArrayList<NameValuePair>();
     		    formparams.add(new BasicNameValuePair("username", userName));
        		formparams.add(new BasicNameValuePair("password", storedPassword));
 	    //	formparams.add(new BasicNameValuePair("username", params[0]));
 	    //	formparams.add(new BasicNameValuePair("password", params[1]));
 	    	formparams.add(new BasicNameValuePair("action", "login"));
 	    	HttpEntity entity;
 		
 				entity = new UrlEncodedFormEntity(formparams, "UTF-8");
 				HttpPost httppost = new HttpPost("https://www.liveinternet.ru/member.php?rndm=1234567890");
 	   			httppost.setEntity(entity);
 	   		    client.execute(httppost);
 	   		    cookies = client.getCookieStore().getCookies();
 	   		    loginDataBaseAdapterAT.close();
			    return cookies;
			
 			} catch (UnsupportedEncodingException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			} 
  	
			 catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
     		
  		
  		
      	   
}
         else
         {
      	   // сохранить пароль и соедининиться.
        	 try
        	 {
        		 // create the instance of Databse
             //    loginDataBaseAdapter=new LoginDataBaseAdapter(MainActivity.this);
              //   loginDataBaseAdapter=loginDataBaseAdapter.open();
        		 loginDataBaseAdapterAT.insertEntry(userName, password);
        		 loginDataBaseAdapterAT.insertEntryUsers(userName, "0");
        		 loginDataBaseAdapterAT.updateEntryLastin(userName);
      	 
      	    client =  new DefaultHttpClient();
     	   
     	   client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
     	   //mt = new MyTask();
     	  // mt.execute(userName,storedPassword);
     	   List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    		formparams.add(new BasicNameValuePair("username", userName));
       		formparams.add(new BasicNameValuePair("password", password));
	    //	formparams.add(new BasicNameValuePair("username", params[0]));
	    //	formparams.add(new BasicNameValuePair("password", params[1]));
	    	formparams.add(new BasicNameValuePair("action", "login"));
	    	HttpEntity entity;
		
				entity = new UrlEncodedFormEntity(formparams, "UTF-8");
				HttpPost httppost = new HttpPost("https://www.liveinternet.ru/member.php?rndm=1234567890");
	   			httppost.setEntity(entity);
	   		    client.execute(httppost);
	   		    cookies = client.getCookieStore().getCookies();
	   		   loginDataBaseAdapterAT.close();
			    return cookies;
			
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
 	
			 catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }*/
         return cookies;
		}
      	
          
		  
	        protected void onProgressUpdate(Integer... progress) {
	            mCurrProgress = progress[0];
	            if (mActivity != null) {
	                mProgress.setProgress(mCurrProgress);
	            }
	            else {
	              //   Log.d(TAG, "Progress updated while no Activity was attached.");
	            }
	        }
		
	/*	*//** {@inheritDoc} *//*
		@Override
	    protected final void onPreExecute() {
	        final List<Cookie> target = mTarget.get();
	        if (target != null) {
	            this.onPreExecute(target);
	        }
	    }*/

			
		
		}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//signIn();
	}
	
	@Override 
	protected void onResume()
	{
		super.onResume();
		signIn();
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public String getCookie(String inputstr,String cookie)
	{
		int index;
		String result = "";
		index = inputstr.indexOf(cookie);
		if (index!=-1)
		{
			result = inputstr.substring(index+cookie.length()+1, inputstr.indexOf(";",index));
		}
		else
		{
			result = "NoCookie";
		}
		return result;
	}
	 // Methos to handleClick Event of Sign In Button
    public void signIn()
       {
                
              final Dialog dialog = new Dialog(MainActivity.this);

                dialog.setContentView(R.layout.login);
                dialog.setTitle("Login");
                
                // create the instance of Databse
                loginDataBaseAdapter=new LoginDataBaseAdapter(MainActivity.this);
                loginDataBaseAdapter=loginDataBaseAdapter.open();
                
                // get the Refferences of views
                final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
                final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
                
                String LastUser=loginDataBaseAdapter.getLastIn("0");
                if (!LastUser.equals("NOT EXIST"))
                {
                	editTextUserName.setText(LastUser);
                	editTextPassword.setText(loginDataBaseAdapter.getSinlgeEntry(LastUser));
                }
             /*   else
                {
                	loginDataBaseAdapter.insertEntry( editTextUserName.getText().toString(), editTextPassword.getText().toString());
                	loginDataBaseAdapter.insertEntryUsers(editTextUserName.getText().toString(), "0");
                	loginDataBaseAdapter.updateEntryLastin(editTextUserName.getText().toString());
                }*/
                	
                		
               
                

               
                
                Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);
                dialog.show();
                // Set On ClickListener
                btnSignIn.setOnClickListener(new View.OnClickListener() {
                    
                    public void onClick(View v) {
                    	
                      //  mt = new MyAsyncTask();
                       // mt.execute(editTextUserName.getText().toString(),editTextPassword.getText().toString());
                    	if (haveNetworkConnection())
                    	{
                    		
                    		Handler h = new Handler() {

                    		    @Override
                    		    public void handleMessage(Message msg) {

                    		        if (msg.what != 1) { Toast.makeText(MainActivity.this, "No internet connection. Try again.", Toast.LENGTH_LONG).show();

                    		        } else {  new MyAsyncTask(MainActivity.this).execute(editTextUserName.getText().toString(),editTextPassword.getText().toString());
                                				dialog.dismiss();

                    		        }

                    		    }
                    		};
                    		
                    		isNetworkAvailable(h,5000);
                    	
                    	}
                    	else
                    	{
                    		Toast.makeText(MainActivity.this, "No internet connection. Try again.", Toast.LENGTH_LONG).show();
                    	}
                    }
                });
                
                loginDataBaseAdapter.close();
                
                
                
                
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
    
 // Methos to handleClick Event of Sign In Button
    public void signIn(View V)
       {
                
              final Dialog dialog = new Dialog(MainActivity.this);

                dialog.setContentView(R.layout.login);
                dialog.setTitle("Login");
                
                // create the instance of Databse
                loginDataBaseAdapter=new LoginDataBaseAdapter(MainActivity.this);
                loginDataBaseAdapter=loginDataBaseAdapter.open();
                
                // get the Refferences of views
                final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
                final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
                
                String LastUser=loginDataBaseAdapter.getLastIn("0");
                if (!LastUser.equals("NOT EXIST"))
                {
                	editTextUserName.setText(LastUser);
                	editTextPassword.setText(loginDataBaseAdapter.getSinlgeEntry(LastUser));
                }
           /*     else
                {
                	loginDataBaseAdapter.insertEntry( editTextUserName.getText().toString(), editTextPassword.getText().toString());
                	loginDataBaseAdapter.insertEntryUsers(editTextUserName.getText().toString(), "0");
                	loginDataBaseAdapter.updateEntryLastin(editTextUserName.getText().toString());
                }*/
              
                		
               
            

               
                
                Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);
                dialog.show();
                // Set On ClickListener
                btnSignIn.setOnClickListener(new View.OnClickListener() {
                    
                    public void onClick(View v) {
                    	
                       // mt = new MyAsyncTask();
                      //  mt.execute(editTextUserName.getText().toString(),editTextPassword.getText().toString());
                     	if (haveNetworkConnection())
                    	{
                     		 Handler h = new Handler() {

                     		    @Override
                     		    public void handleMessage(Message msg) {

                     		        if (msg.what != 1) { Toast.makeText(MainActivity.this, "No internet connection. Try again.", Toast.LENGTH_LONG).show();

                     		        } else {  new MyAsyncTask(MainActivity.this).execute(editTextUserName.getText().toString(),editTextPassword.getText().toString());
                                 				dialog.dismiss();

                     		        }

                     		    }
                     		};
                    		
                     		isNetworkAvailable(h,5000);
                    	
                    	}
                    	else
                    	{
                    		Toast.makeText(MainActivity.this, "No internet connection. Try again.", Toast.LENGTH_LONG).show();
                    	}
                    }
                });
                
                loginDataBaseAdapter.close();
                
                
                
                
    }



}

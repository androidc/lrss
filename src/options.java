package com.example.lirurssreader_v4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class options extends Activity {
	LoginDataBaseAdapter loginDataBaseAdapter;
	EditText etNumLoad;
	
	private CheckBox chkIos;
	private Button btnDisplay;
	  
	  
	private String username;
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("USER");
	    
		// прочитать Options из базы, если есть.
		
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        
        String numLoad = loginDataBaseAdapter.getNUMLOAD(username);
        etNumLoad = (EditText) findViewById(R.id.etNumLoad);
        chkIos = (CheckBox) findViewById(R.id.cbLoad);
        
       
        
        
        
        if (!numLoad.equals("NOT EXIST"))
        {
        	if (numLoad.equals("1000"))
        	{
        		etNumLoad.setText("10");
            	etNumLoad.setEnabled(false);
            	chkIos.setChecked(true);	
        	}
        	else
        	{
        	etNumLoad.setText(numLoad);
        	etNumLoad.setEnabled(true);
        	chkIos.setChecked(false);
        	}
        }
        else
        {
        	etNumLoad.setText("10");
        	etNumLoad.setEnabled(false);
        	chkIos.setChecked(true);
        }	
        
		addListenerOnChkIos();
		addListenerOnButton();
	  }
	 
		protected void onDestroy() {
			// закрываем подключение при выходе
				//	cursor_rep.close();
				//	loginDataBaseAdapter.close();
			super.onDestroy();
			loginDataBaseAdapter.close();
		}
		
		 public void addListenerOnChkIos() {
			 
				//chkIos = (CheckBox) findViewById(R.id.cbLoad);
			 
				chkIos.setOnClickListener(new OnClickListener() {
			 
				  @Override
				  public void onClick(View v) {
			                //is chkIos checked?
					if (((CheckBox) v).isChecked()) {
						etNumLoad.setEnabled(false);
					}
					else
					{
						etNumLoad.setEnabled(true);
					}
			 
				  }
				});
			 
			  }
			 
			  public void addListenerOnButton() {
			 
				chkIos = (CheckBox) findViewById(R.id.cbLoad);
				
				btnDisplay = (Button) findViewById(R.id.btnOK);
			 
				btnDisplay.setOnClickListener(new OnClickListener() {
			 
			          //Run when button is clicked
				  @Override
				  public void onClick(View v) {
					
					  String numLoad = loginDataBaseAdapter.getNUMLOAD(username);
					  RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
				      int checkedRadioButton = radioGroup.getCheckedRadioButtonId();
				      String fastload = "true";
				      switch (checkedRadioButton) {
				      case R.id.radiobutton1 : fastload = "false" ;
				                       	              break;
				      case R.id.radiobutton2 : fastload = "true";
				    		                      break;
				     
				    }
					  if (chkIos.isChecked())
					  {
						 
						  if (!numLoad.equals("NOT EXIST"))
						  {
							 
							  loginDataBaseAdapter.updateEntryOptions(username, "1000",fastload);
							  Toast.makeText(options.this, "Options updated.",
										Toast.LENGTH_LONG).show(); 
						  }
						  else
						  {
						
						      loginDataBaseAdapter.insertEntryOptions(username, "1000",fastload);
							  Toast.makeText(options.this, "Options saved.",
										Toast.LENGTH_LONG).show(); 
						  }
						 
					  }
					  else
					  {
						  
						  if (!numLoad.equals("NOT EXIST"))
						  {
							  loginDataBaseAdapter.updateEntryOptions(username, etNumLoad.getText().toString(),fastload);
							  Toast.makeText(options.this, "Options updated",
										Toast.LENGTH_LONG).show();
						  }
						  else
						  {
							  loginDataBaseAdapter.insertEntryOptions(username, etNumLoad.getText().toString(),fastload);
							  Toast.makeText(options.this, "Options saved.",
										Toast.LENGTH_LONG).show(); 
						  }
						  
					  }
			 
					//StringBuffer result = new StringBuffer();
				//	result.append("IPhone check : ").append(chkIos.isChecked());
				//	result.append("\nAndroid check : ").append(chkAndroid.isChecked());
				//	result.append("\nWindows Mobile check :").append(chkWindows.isChecked());
			 
				//	Toast.makeText(MyAndroidAppActivity.this, result.toString(),
				//			Toast.LENGTH_LONG).show();
			 
				  }
				});
			 
			  }

}



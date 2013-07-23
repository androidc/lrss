package com.example.lirurssreader_v4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addSkill extends Activity {

	final String LOG_TAG = "myLogs";
	Button btnAdd;

	LoginDataBaseAdapter loginDataBaseAdapter;
	EditText etURL, etName;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addrss);

		setTitle("Add RSS window");
		
		btnAdd = (Button) findViewById(R.id.btnAdd);
		etURL = (EditText) findViewById(R.id.etURL);
		etName = (EditText) findViewById(R.id.etName);

		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

		OnClickListener oclBtnAdd = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO add record to skill table
				// создаем объект для данных
				// ContentValues cv = new ContentValues();

				// подключаемся к БД
				// SQLiteDatabase db = dbHelper.getWritableDatabase();

				String URL = etURL.getText().toString();
				String Name = etName.getText().toString();
				Intent intent = getIntent();
				
				loginDataBaseAdapter.insertEntryRSS(URL, Name,intent.getStringExtra("USER"));
				// cv.put("skill", skill);
				// cv.put("value",value);
				// long rowID = db.insert("TSkill", null, cv);
				// Log.d(LOG_TAG, "row inserted, ID = " + rowID);
				
				Toast.makeText(getApplicationContext(), "row inserted, ID=",
						Toast.LENGTH_LONG).show();
			}
		};

		btnAdd.setOnClickListener(oclBtnAdd);

	}

	protected void onDestroy() {
		super.onDestroy();
		// закрываем подключение при выходе
		loginDataBaseAdapter.close();
	}

}

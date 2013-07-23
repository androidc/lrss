package com.example.lirurssreader_v4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class LoginDataBaseAdapter 
{
            static final String DATABASE_NAME = "loginRss.db";
      
            static final int DATABASE_VERSION = 1;

            public static final int NAME_COLUMN = 1;
            // TODO: Create public field for each column in your table.
            // SQL Statement to create a new database.
            static final String DATABASE_CREATE = "create table "+"LOGIN"+
                                         "( " +"_id"+" integer primary key autoincrement,"+ "USERNAME  text,PASSWORD text); ";
            static final String DATABASE_CREATE_USERS =  "create table "+"USERS"+
                    "( " +"_id"+" integer primary key autoincrement,"+ "USERNAME  text,LASTIN text); ";                            
            static final String DATABASE_CREATE_RSS_FEEDS =  "create table "+"RSS"+
                    "( " +"_id"+" integer primary key autoincrement,"+ "URL  text,NAME text, USER text, NAMEOUT text); ";
            static final String DATABASE_CREATE_OPTIONS =  "create table OPTIONS"+
                    "( " +"_id"+" integer primary key autoincrement,"+ "NUMLOAD  text, USER text, FASTLOAD text); ";
            static final String DATABASE_CREATE_FEEDS =  "create table FEEDS"+
                    "( " +"_id"+" integer primary key autoincrement,"+ "DATEPOST  text, TITLE text,URL text UNIQUE ON CONFLICT REPLACE, USER text ); ";
            
            // Variable to hold the database instance 
            public  SQLiteDatabase db;
            // Context of the application using the database.
            private final Context context;
            // Database open/upgrade helper
            private DataBaseHelper dbHelper;
            public  LoginDataBaseAdapter(Context _context) 
            {
                    context = _context;
                    dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

             // Method to openthe Database  
            public  LoginDataBaseAdapter open() throws SQLException 
            {
                    db = dbHelper.getWritableDatabase();
                    return this;
            }
         
            // Method to close the Database  
            public void close() 
            {
                    db.close();
            }
  
             // method returns an Instance of the Database 
            public  SQLiteDatabase getDatabaseInstance()
            {
                    return db;
            }
            
            // method to insert a record in Table FEEDS
            public void insertEntryFeed(String userName,String title, String datepost, String URL)
            {
                       
                     
                       ContentValues newValues = new ContentValues();
                        // Assign values for each column.
                        newValues.put("USER", userName);
                        newValues.put("TITLE", title);
                        newValues.put("URL", URL);
                        newValues.put("DATEPOST", datepost);
                       
                        // Insert the row into your table
                        db.insert("FEEDS", null, newValues);
                      //  Toast.makeText(context, "User Info Saved", Toast.LENGTH_LONG).show();
           
       
            }
    
              // method to insert a record in Table
            public void insertEntry(String userName,String password)
            {
                       
                     
                       ContentValues newValues = new ContentValues();
                        // Assign values for each column.
                        newValues.put("USERNAME", userName);
                        newValues.put("PASSWORD",password);
                       
                       
                       
                        // Insert the row into your table
                        db.insert("LOGIN", null, newValues);
                      //  Toast.makeText(context, "User Info Saved", Toast.LENGTH_LONG).show();
           
       
            }
            
            // method to insert a record in Table
            public void insertEntryOptions(String userName,String NUMLOAD, String load)
            {
                       
                     
                       ContentValues newValues = new ContentValues();
                        // Assign values for each column.
                        newValues.put("USER", userName);
                        newValues.put("NUMLOAD",NUMLOAD);
                        newValues.put("FASTLOAD",load);
                       
                       
                       
                        // Insert the row into your table
                        db.insert("OPTIONS", null, newValues);
                      //  Toast.makeText(context, "User Info Saved", Toast.LENGTH_LONG).show();
           
       
            }
            
            // Method to Update an Existing Record 
            public void  updateEntryOptions(String userName,String NUMLOAD, String fastload)
            {
                    //  create object of ContentValues
                    ContentValues updatedValues = new ContentValues();
                    // Assign values for each Column.
                    updatedValues.put("USER", userName);
                    updatedValues.put("NUMLOAD",NUMLOAD);
                    updatedValues.put("FASTLOAD", fastload);
                   
                    String where="USER = ?";
                    db.update("OPTIONS",updatedValues, where, new String[]{userName});
                  
            }
            
            // method to insert a record in Table
            public void insertEntryRSS(String URL,String NAME, String USER)
            {
                       
                     
                       ContentValues newValues = new ContentValues();
                        // Assign values for each column.
                        newValues.put("URL", URL);
                        newValues.put("NAME",NAME);
                        newValues.put("USER",USER);
                        newValues.put("NAMEOUT",NAME);
                       
                       
                       
                        // Insert the row into your table
                        db.insert("RSS", null, newValues);
                       // Toast.makeText(context, "RSS Info Saved", Toast.LENGTH_LONG).show();
           
       
            }
            
            // method to insert a record in Table
            public void insertEntryUsers(String userName,String lastin)
            {
                       
                     
                       ContentValues newValues = new ContentValues();
                        // Assign values for each column.
                        newValues.put("USERNAME", userName);
                        newValues.put("LASTIN",lastin);
                       
                       
                       
                        // Insert the row into your table
                        db.insert("USERS", null, newValues);
                      //  Toast.makeText(context, "User Info Saved in USERS", Toast.LENGTH_LONG).show();
           
       
            }
           
           // method to delete a Record of UserName
            public int deleteEntry(String UserName)
            {
                     
                   String where="USERNAME=?";
                   int numberOFEntriesDeleted= db.delete("LOGIN", where, new String[]{UserName}) ;
                   Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
                return numberOFEntriesDeleted;
               
            }
            
            // method to delete a Record of UserName
            public int deleteEntryUSERS(String UserName)
            {
                     
                   String where="USERNAME=?";
                   int numberOFEntriesDeleted= db.delete("USERS", where, new String[]{UserName}) ;
                   Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
                return numberOFEntriesDeleted;
               
            }
       
            // method to delete a Record of UserName
            public int deleteEntryRSS(String NAME)
            {
                     
                   String where="NAME=? or NAMEOUT=?";
                   int numberOFEntriesDeleted= db.delete("RSS", where, new String[]{NAME, NAME}) ;
                   Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
                return numberOFEntriesDeleted;
               
            }
       
       // method to get the password  of userName
        public String getSinlgeEntry(String userName)
        {
           
               
                Cursor cursor=db.query("LOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
                if(cursor.getCount()<1) // UserName Not Exist
                    return "NOT EXIST";
                cursor.moveToFirst();
                String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));
                return password;
               
           
        }
        
     // method to get the password  of userName
        public String getSinlgeEntryURLF(String url)
        {
           
               
                Cursor cursor=db.query("FEEDS", null, " URL=?", new String[]{url}, null, null, null);
                if(cursor.getCount()<1) // URL Not Exist
                    return "NOT EXIST";
                cursor.moveToFirst();
                String password= cursor.getString(cursor.getColumnIndex("URL"));
                return password;
               
           
        }
        
     // получить все данные из таблицы DB_TABLE
    	public Cursor getAlldata() {
    		return db.query("RSS", null, null, null, null, null, null);
    	}
    	
    	 // получить все данные из таблицы DB_TABLE
    	public Cursor getAlldata(String tbl) {
    		return db.query(tbl, null, null, null, null, null, null);
    	}
    	
    	public Cursor getRSSNames(String user) {
    		String strSQL = "SELECT  NAMEOUT as mname ,  * FROM  RSS" +
    				" WHERE USER = '"+user+"'";
    		try
    		{
    			Cursor cursor = db.rawQuery(strSQL, null);
    			return cursor;
    		
    	} catch (Exception e) {
    		
    		return null;
    		
    		}
    	}
    	
    	public Cursor getRSSbyName(String name) {
    		String strSQL = "SELECT  URL as mUrl ,  * FROM  RSS" +
    				" WHERE NAMEOUT = '"+name+"'";
    		try
    		{
    			Cursor cursor = db.rawQuery(strSQL, null);
    			return cursor;
    		
    	} catch (Exception e) {
    		
    		return null;
    		
    		}
    	}
    	
    	// method to get the password  of userName
        public String getNUMLOAD(String userName)
        {
           
               
                Cursor cursor=db.query("OPTIONS", null, " USER=?", new String[]{userName}, null, null, null);
                if(cursor.getCount()<1) // UserName Not Exist
                    return "NOT EXIST";
                cursor.moveToFirst();
                String numload= cursor.getString(cursor.getColumnIndex("NUMLOAD"));
                return numload;
               
           
        }
        
    	// method to get the password  of userName
        public String getFastLoad(String userName)
        {
           
               
                Cursor cursor=db.query("OPTIONS", null, " USER=?", new String[]{userName}, null, null, null);
                if(cursor.getCount()<1) // UserName Not Exist
                    return "true";
                cursor.moveToFirst();
                String fastload= cursor.getString(cursor.getColumnIndex("FASTLOAD"));
                		//cursor.getInt(cursor.getColumnIndex("FASTLOAD"));
                return fastload;
               
           
        }
        
        
     // method to get the password  of userName
        public String getLastIn(String Lastin)
        {
           
               
                Cursor cursor=db.query("USERS", null, " LASTIN=?", new String[]{Lastin}, null, null, null);
                if(cursor.getCount()<1) // UserName Not Exist
                    return "NOT EXIST";
                cursor.moveToFirst();
                String password= cursor.getString(cursor.getColumnIndex("USERNAME"));
                return password;
               
           
        }
        
    

     // Method to Update an Existing Record 
        public void  updateEntry(String userName,String password)
        {
                //  create object of ContentValues
                ContentValues updatedValues = new ContentValues();
                // Assign values for each Column.
                updatedValues.put("USERNAME", userName);
                updatedValues.put("PASSWORD",password);
               
                String where="USERNAME = ?";
                db.update("LOGIN",updatedValues, where, new String[]{userName});
              
        }
        
     // Method to Update an Existing Record 
        public void  updateEntryRSS(String URL, String count)
        {
        	String strSQL = "UPDATE RSS SET NAMEOUT =  NAME || '                    Новых: "+count+"'  WHERE URL='"+URL+"'";
    		try
    		{
    			db.execSQL(strSQL);
    			//Cursor cursor = db.rawQuery(strSQL, null);
    			
    		
    	} catch (Exception e) {
    		
    		
    		
    		}
              
        }
        
        // Method to Update an Existing Record 
        public void  updateEntryLastin(String userName)
        {
                //  create object of ContentValues
                ContentValues updatedValues = new ContentValues();
                // Assign values for each Column.
               // updatedValues.put("USERNAME", userName);
                updatedValues.put("LASTIN","1");
               
                String where="USERNAME <> ?";
                db.update("USERS",updatedValues, where, new String[]{userName});
                
                
               // updatedValues.put("USERNAME", userName);
                updatedValues.put("LASTIN","0");
               
                where="USERNAME = ?";
                db.update("USERS",updatedValues, where, new String[]{userName});
              
        }
        
        // Method to Update an Existing Record 
        public void  updateEntryLogin(String userName, String password)
        {
        	
     
                //  create object of ContentValues
                ContentValues updatedValues = new ContentValues();
                // Assign values for each Column.
                updatedValues.put("USERNAME", userName);
                updatedValues.put("PASSWORD",password);
               
               
               
                String where="USERNAME = ?";
                db.update("LOGIN",updatedValues, where, new String[]{userName});
              
        }
       
       
       
}

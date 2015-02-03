package com.siddworks.simguard.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.noveogroup.android.log.Log;
import com.siddworks.simguard.bean.Contact;
import com.siddworks.simguard.bean.KeyValue;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper  {
	
	SQLiteDatabase dbWritable = null;
	SQLiteDatabase dbReadable = null;
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
//		Log.d(LOGTAG, "In DatabaseHelper.DatabaseHelper");
//		open();
	}
	
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        Log.d(LOGTAG, "In DatabaseHelper.DatabaseHelper");
//        open();
    }
	 
    //private SQLiteDatabase dbHelper;
	  
	// Logcat tag
    private static final String LOGTAG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "simguard";
 
    // Table names
    public static final String TABLE_KEYVALUE = "keyvalues";
    public static final String TABLE_CONTACTS = "contacts";
    
    // Column names for TABLE_KEYVALUE
    private static final String COL_ID = "id";
    private static final String COL_KEY = "key";
    private static final String COL_VALUE = "value";
    
    // Column names for TABLE_GROUP_CONTACTS
    private static final String COL_CONTACT_ID = "contact_id";
    private static final String COL_CONTACT_NAME = "contact_name";
    private static final String COL_CONTACT_NUMBER = "contact_number";
    private static final String COL_ORDER = "contact_order";

    // Table Create Statements
    // TABLE_GROUPS create statement
    private static final String CREATE_TABLE_KEYVALUE = "CREATE TABLE "
            + TABLE_KEYVALUE + "(" + COL_ID + " INTEGER PRIMARY KEY," + COL_KEY + " TEXT UNIQUE," + COL_VALUE
            + " TEXT" + ")";
    
 // TABLE_GROUP_CONTACTS create statement
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE "
            + TABLE_CONTACTS + "(" + COL_ID + " INTEGER PRIMARY KEY," 
            + COL_CONTACT_ID + " INTEGER, " + COL_CONTACT_NAME + " TEXT, " + 
            COL_CONTACT_NUMBER + " TEXT UNIQUE," + COL_ORDER + " INTEGER" + ")";

    @Override
	public void onCreate(SQLiteDatabase db) {
//    	Log.d(LOGTAG+Log.FLOWDEBUG, "In DatabaseHelper.onCreate");
    	
    	Log.i("Creating Tables:");
    	Log.i("CREATE_TABLE_KEYVALUE:"+CREATE_TABLE_KEYVALUE);
    	Log.i("CREATE_TABLE_CONTACTS:"+CREATE_TABLE_CONTACTS);
    	
    	db.execSQL(CREATE_TABLE_KEYVALUE);
    	db.execSQL(CREATE_TABLE_CONTACTS);
        
        dbReadable = db;
        dbWritable = db;
    }
    
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
//    	Log.d(LOGTAG, "In DatabaseHelper.onUpgrade");

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEYVALUE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		
        // create new tables
        onCreate(db);
	}
	
	public KeyValue createKeyValue(KeyValue kv) {
//		Log.d(LOGTAG, "In "+LOGTAG+".createGroup");
			
	    ContentValues values = new ContentValues();
	    values.put(COL_KEY, kv.getKey());
	    values.put(COL_VALUE, kv.getValue());
	 
	    // insert row
	    long id = dbWritable.insert(TABLE_KEYVALUE, null, values);
		kv.setId(id);
		Log.i("KeyValue inserted:" + kv.toString());
		
	    return kv;
	}
	
	public ArrayList<KeyValue> fetchKeyValues() {
//		Log.d(LOGTAG, "In "+LOGTAG+".fetchGroups");
			
		Cursor cursor = null;
	    ArrayList<KeyValue> kvs = new ArrayList<KeyValue>();;
	    
		StringBuffer sbQuery = new StringBuffer("SELECT * from ").append(
				TABLE_KEYVALUE);
		
		try {
			cursor = dbReadable.rawQuery(sbQuery.toString(), null);
	        Log.i(LOGTAG, "Query:"+sbQuery);
	    	
	        if (cursor != null && cursor.moveToFirst()) {
	        	
	        	KeyValue kv = null;
	        	while (!cursor.isAfterLast()) {
	        		kv = cursorToKeyValue(cursor);
	        		Log.i(kv.toString());
	        		kvs.add(kv);
	        		cursor.moveToNext();
	        	}
	        }
	    } catch (Exception e) {
	        Log.e("AppoitnmentDBhelper", e.toString());
	    }
	    return kvs;
	}
	
	public long updateKeyValue(KeyValue kv) {
//		Log.d(LOGTAG, "In "+LOGTAG+".createGroup");
			
	    ContentValues values = new ContentValues();
	    values.put(COL_KEY, kv.getKey());
	    values.put(COL_VALUE, kv.getValue());
	 
	    long id = dbWritable.update(TABLE_KEYVALUE, values, COL_KEY + " = ?",
	            new String[] { kv.getKey() });
	            
	    // update row
		Log.i("KeyValue updated:" + kv.toString());
	 
	    return id;
	}
	
	public KeyValue getKeyValueByKey(String key) {
//		Log.d(LOGTAG, "In "+LOGTAG+".getContactByNumber");
			
		Cursor cursor = null;
		KeyValue kv = null;
		
		StringBuffer sbQuery = new StringBuffer("SELECT * from ").append(
				TABLE_KEYVALUE).append(" where ").append(COL_KEY).append(" =?");
    	String[] args = { key };

		try {
			cursor = dbReadable.rawQuery(sbQuery.toString(), args);
	        Log.i(LOGTAG, "Query:"+sbQuery);
	    	
	        if (cursor != null && cursor.moveToFirst()) {
	        	
	        	
	        	while (!cursor.isAfterLast()) {
	        		kv = cursorToKeyValue(cursor);
	        		Log.i(kv.toString());
	        		cursor.moveToNext();
	        	}
	        }
	    } catch (Exception e) {
	        Log.e("AppoitnmentDBhelper", e.toString());
	    }
	    return kv;
	}
	
	
	public long deleteKeyValue(KeyValue kv) {
//		Log.d(LOGTAG, "In "+LOGTAG+".deleteGroup");
			
	    // delete row
	    String[] values = { "" + String.valueOf(kv.getId()) };
	    long id = dbWritable.delete(TABLE_KEYVALUE, COL_ID + " = ?", values);

	    Log.i("KeyValue deleted:" + kv.toString());
	 
	    return id;
	}

	private KeyValue cursorToKeyValue(Cursor cursor) {
		KeyValue kv = new KeyValue();
		kv.setId(cursor.getLong(0));
		kv.setKey(cursor.getString(1));
		kv.setValue(cursor.getString(2));
	    return kv;
	}
	
	public long addContact(Contact cnt) {
//		Log.d(LOGTAG, "In "+LOGTAG+".addContactToGroup");
			
	    ContentValues values = new ContentValues();
	    values.put(COL_CONTACT_ID, cnt.getId());
	    values.put(COL_CONTACT_NAME, cnt.getName());
	    values.put(COL_CONTACT_NUMBER, cnt.getNumber());
	    values.put(COL_ORDER, cnt.getOrder());
		 
	    // insert row
	    long id = dbWritable.insert(TABLE_CONTACTS, null, values);
		cnt.setId(id);
		Log.i("contact inserted:" + cnt);
	 
	    return id;
	}

	public ArrayList<Contact> fetchContacts() {
//		Log.d(LOGTAG, "In "+LOGTAG+".fetchContactsOfGroup");
			
		Cursor cursor = null;
	    ArrayList<Contact> cnts = new ArrayList<Contact>();
	    
		StringBuffer sbQuery = new StringBuffer("SELECT * from ")
		.append(TABLE_CONTACTS)
		.append(" ORDER BY ").append(COL_ORDER);
		
    	String[] args = { };

		try {
			cursor = dbReadable.rawQuery(sbQuery.toString(), args);
	        Log.i(LOGTAG, "Query:"+sbQuery);
	    	
	        if (cursor != null && cursor.moveToFirst()) {
	        	
	        	Contact cnt = null;
	        	while (!cursor.isAfterLast()) {
	        		cnt = cursorToContact(cursor);
	        		Log.i(cnt.toString());
	        		cnts.add(cnt);
	        		cursor.moveToNext();
	        	}
	        }
	    } catch (Exception e) {
	        Log.e("AppoitnmentDBhelper", e.toString());
	    }
	    return cnts;
	}
	
	public long deleteContactFromGroup(Contact cnt) {
//		Log.d(LOGTAG, "In "+LOGTAG+".deleteContactFromGroup");
			
	    // delete row
	    String[] values = { cnt.getNumber()};
	    long id = dbWritable.delete(TABLE_CONTACTS, COL_CONTACT_NUMBER+ " = ?", values);

	    Log.i("contact deleted:" + cnt.toString());
	 
	    return id;
	}
	
	private Contact cursorToContact(Cursor cursor) {
		Contact cnt = new Contact();
		cnt.setId(cursor.getLong(0));
		cnt.setId(cursor.getLong(1));
		cnt.setName(cursor.getString(2));
		cnt.setNumber(cursor.getString(3));
		cnt.setOrder(cursor.getInt(4));
	    return cnt;
	}
	
	public long showTable(String TableName) {
//		Log.d(LOGTAG+Log.FLOWDEBUG, "In DatabaseHelper.showTable. TableName:"+TableName);
		
	    Cursor cursor = null;
	    long id = 0;
	    
	    try {
	        StringBuffer sbQuery = new StringBuffer("SELECT * from ").append(
	        		TableName);
	        cursor = dbReadable.rawQuery(sbQuery.toString(), null);
	        if (cursor != null && cursor.moveToFirst()) {
	        	
	        	while (!cursor.isAfterLast()) {
		        	if(TableName.equals(TABLE_KEYVALUE))
		        	{
		        		Log.i(cursorToKeyValue(cursor).toString());
		        		cursor.moveToNext();
		        	}
		        	if(TableName.equals(TABLE_CONTACTS))
		        	{
		        		Log.i(cursorToContact(cursor).toString());
		        		cursor.moveToNext();
		        	}
	        	}
	        }
	    } catch (Exception e) {
	        Log.e("AppoitnmentDBhelper", e.toString());
	    }
	    return id;
	}
	
	public long updateTable(String TableName, String columnName, String value, String idColumn, String idString) {
		Log.d(LOGTAG, "In DatabaseHelper.updateTable. TableName:"+TableName+", columnName:"+columnName+
				", value:"+value+", idColumn:"+idColumn+", idString:"+idString);

		long id = 0; 
    	ContentValues values = new ContentValues();
	    
	    try {
	    	
	 	    values.put(columnName, value);
	 	 
	 	    id = dbWritable.update(TableName, values, idColumn + " = ?",
	 	            new String[] { idString });
	 	    System.out.println("id:"+id);
	 	    
	    } catch (Exception e) {
	        Log.e("AppoitnmentDBhelper", e.toString());
	    }
	    return id;
	}
	
	public void closeDB() {
//		Log.d(LOGTAG, "In DatabaseHelper.closeDB");
		 
        if (dbWritable != null && dbWritable.isOpen())
        {
        	dbWritable.close();
        }
    }

	
	public void open() {
//		Log.d(LOGTAG, "In DatabaseHelper.open");
		if(dbWritable == null)
		{
			dbWritable = this.getWritableDatabase();
			if(dbWritable == null)
			{
				Log.i(LOGTAG, "dbWritable null");
			}
		}
		if(dbReadable == null)
		{
			dbReadable = this.getReadableDatabase();
			if(dbReadable == null)
			{
				Log.i(LOGTAG, "dbReadable null");
			}
		}
	}
}

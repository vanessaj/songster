package com.songsuggester;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.songsuggester/databases/";
 
    private static String DB_NAME = "songSug.db";
 
    private SQLiteDatabase myDatabase; 
    public SQLiteDatabase myData;
 
    private final Context myContext;
    
    private static final String DATABASE_TABLE_USERS = "users";
    public static final String DATABASE_TABLE_SONGS = "songs";
    private static final String DATABASE_TABLE_TAGS = "tags";
    private static final String DATABASE_TABLE_USERDATA = "userData";
    
    // columns in songs
    public static final String SONG_COLUMN = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String ARTIST_COLUMN = SearchManager.SUGGEST_COLUMN_TEXT_2;
    
/*    private static final String DATABASE_TABLE_USERS = "sampleUsers";
    private static final String DATABASE_TABLE_SONGS = "sampleSongs";
    private static final String DATABASE_TABLE_TAGS = "sampleTags";
    private static final String DATABASE_TABLE_SAMPLE = "sampleData";*/

 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDatabase() throws IOException{
 
    	boolean dbExist = checkDatabase();
 
    	if(dbExist){
    		//do nothing - db already exists
    	}else{
 
    		//create db to overwrite
        	this.getReadableDatabase();
 
        	try {
 
    			copyDatabase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring the bytestream.
     * */
    private void copyDatabase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDatabase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDatabase != null)
    		    myDatabase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			copyDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		openDatabase();
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_USERS);
		db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_SONGS);
		db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_TAGS);
		db.execSQL("DROP TABLE IF EXISTS " +DATABASE_TABLE_USERDATA);
        onCreate(db);
	}
	
	 
	
  /*
   * PUBLIC HELPER METHODS ACCESSING DB
   */
	
	
	public void createLogin(){
		String myPath = DB_PATH + DB_NAME;
		myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		
		myData.execSQL("CREATE TABLE IF NOT EXISTS login (_id INTEGER, username TEXT)");
	}
	
	public void loginUser(String usernameInput){
		String myPath = DB_PATH + DB_NAME;
		myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		myData.compileStatement("CREATE TABLE IF NOT EXISTS login (_id INTEGER, username TEXT)");
		myData.execSQL("INSERT INTO login (_id, username) VALUES (1, ?)", new String[]{usernameInput});
	}
	
	public String getLoggedUser(){
		String myPath = DB_PATH + DB_NAME;
		myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		
		myData.compileStatement("CREATE TABLE IF NOT EXISTS login (_id INTEGER, username TEXT)");
		
		Cursor cursor = myData.rawQuery("SELECT username FROM login WHERE _id = 1", null);
		
		if(cursor.moveToFirst() == false){
			return "Guest";
		}
		else{
			cursor.moveToFirst();
			int colUser = cursor.getColumnIndex("username");
			String user = cursor.getString(colUser);
			return user;
		}
	}
	
	public void dropLogin(){
		String myPath = DB_PATH + DB_NAME;
		myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		
		myData.execSQL("DROP TABLE IF EXISTS login");
	}
 
	// Login method to get password for username inputed
    public Cursor getPassword(String inputUsername) {
    	//String passwordfound;
        Cursor cursor = null;
        
        // get readable db
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        // make the table, db is there but table isn't made
        myData.compileStatement("CREATE TABLE IF NOT EXISTS users (_id TEXT, username TEXT, password TEXT)");
       
        //myData.compileStatement("CREATE TABLE IF NOT EXISTS sampleUsers (_id TEXT, username TEXT, password TEXT)");
        
        // sql query
        cursor = myData.rawQuery("SELECT password FROM users WHERE username = ?", new String[]{inputUsername});
        //cursor = myData.rawQuery("SELECT password FROM sampleUsers WHERE username = '" + inputUsername + "'", null);
        
        
        // return cursor
        return cursor;
        
 /*       // get results and return
        if (cursor.moveToFirst() == false) {
        	cursor.close();
        	myData.close();
        	return passwordfound = "THE TABLE IS EMPTY";
        }
        
        else {
    		cursor.moveToFirst();
    		int placeColumn = cursor.getColumnIndex("password");
    		passwordfound = cursor.getString(placeColumn);
    		cursor.close();
    		myData.close();
    		return passwordfound;
        }
        */
        
    }
    
// Check the db for the number of users listed in users table    
    public int getNumUsers(){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        myData.compileStatement("CREATE TABLE IF NOT EXISTS users (_id TEXT, username TEXT, password TEXT)");
       // myData.compileStatement("CREATE TABLE IF NOT EXISTS sampleUsers (_id TEXT, username TEXT, password TEXT)");
        
        int numUsers;
        c = myData.rawQuery("SELECT Count(*) FROM users", null); 
        //c = myData.rawQuery("SELECT Count(*) FROM sampleUsers", null);
        c.moveToFirst();
        numUsers = c.getInt(0);
        c.close();
        myData.close();
    	return numUsers;
    }
    
    
    // check the db for the number of songs listed in the song table
    public int getNumSongs(){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        myData.compileStatement("CREATE TABLE IF NOT EXISTS songs (_id INTEGER, songname TEXT, artistname TEXT)");
       // myData.compileStatement("CREATE TABLE IF NOT EXISTS sampleSongs (_id INTEGER, songname TEXT, artistname TEXT)");
        
        int numSongs;
        c = myData.rawQuery("SELECT Count(*) FROM songs", null);
      //  c = myData.rawQuery("SELECT Count(*) FROM sampleSongs", null);
        c.moveToFirst();
        numSongs = c.getInt(0);
        c.close();
        myData.close();
    	return numSongs;
    }
    
    
    // check the db for the number of tags listed in the tags table
    public int getNumTags(){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        myData.compileStatement("CREATE TABLE IF NOT EXISTS tags (_id INTEGER, tagName TEXT, category TEXT)");
       // myData.compileStatement("CREATE TABLE IF NOT EXISTS sampleTags (_id INTEGER, tagName TEXT, category TEXT)");
    	
        int numTags;
        c = myData.rawQuery("SELECT Count(*) FROM tags", null);  
       // c = myData.rawQuery("SELECT Count(*) FROM sampleTags", null);
        c.moveToFirst();
        numTags = c.getInt(0);
        c.close();
        myData.close();
    	return numTags;
    }
    
    // access the userData table with user-tag-song relationships
    // return as a cursor so createAdjacencyMatrix method can construct matrix with these relationships
    public Cursor getSampleData(){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
       // myData.compileStatement("CREATE TABLE IF NOT EXISTS sampleData (_id integer, song integer, tag integer)");
        myData.compileStatement("CREATE TABLE IF NOT EXISTS userData (_id integer, song integer, tag integer)");
        
        //c = myData.rawQuery("SELECT * FROM sampleData", null);
        c = myData.rawQuery("SELECT * FROM userData", null);
    	
        return c;
    }
    
    // get the tag id for the inputted string of the tag name
    public Cursor searchTag(String tagName){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        
        myData.compileStatement("CREATE TABLE IF NOT EXISTS tags (_id INTEGER, tagName TEXT, category TEXT)");
        
        c = myData.rawQuery("SELECT _id FROM tags WHERE tagName = ?", new String[]{tagName});
        
        //c.close();
       // myData.close();
        return c;
    }
	
    // return all information as a cursor about an inputted tag name
    public Cursor tagLookup(String tag){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        
        myData.compileStatement("CREATE TABLE IF NOT EXISTS tags (_id INTEGER, tagName TEXT, category TEXT)");
        
        c = myData.rawQuery("SELECT * FROM tags WHERE tagName = ?", new String[]{tag});
        
        return c;
    }
    
    // return all information in users table for designated username
    public Cursor userLookup(String username){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        
        myData.compileStatement("CREATE TABLE IF NOT EXISTS users (_id TEXT, username TEXT, password TEXT)");
        
        c = myData.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        
        return c;
    }
    
    // return the song name and artist name for a specific song ID
    // USES SONG ID
    public String songLookup(int songNum){
    	Cursor c = null;
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS songs (_id INTEGER, songname TEXT, artistname TEXT)");
    	
    	String ssongnum = Integer.toString(songNum); 
    	c = myData.rawQuery("SELECT * FROM songs WHERE _id = ?", new String[]{ssongnum});
    	
    	String result;
    	if(c.moveToFirst() == false){
    		result = "Song not found!";
    	}
    	else{
    		c.moveToFirst();
    		int songCol = c.getColumnIndex("songname");
    		String songName = c.getString(songCol);
    		int artistCol = c.getColumnIndex("artistname");
    		String artistName = c.getString(artistCol);
    		result = songName + " by " + artistName;
    	}
    	c.close();
    	myData.close();
    	return result;
    }
    
    // return the username of a user by looking up his/her user ID number
    public String userLookupByID(int userNum){
    	Cursor c = null;
        String myPath = DB_PATH + DB_NAME;
        myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        
        myData.compileStatement("CREATE TABLE IF NOT EXISTS users (_id TEXT, username TEXT, password TEXT)");
        
        String suserNum = Integer.toString(userNum);
        c = myData.rawQuery("SELECT * FROM users WHERE _id = ?", new String[]{suserNum});
    	
        String result;
    	if(c.moveToFirst() == false){
    		result = "Song not found!";
    	}
    	else{
    		c.moveToFirst();
    		int usernameCol = c.getColumnIndex("username");
    		result = c.getString(usernameCol);
    	}
    	c.close();
    	myData.close();
    	return result;
    }
    
    // return the song info for looking up a song by name
    public Cursor songLookupByName(String songname){
    	Cursor c = null;
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS songs (_id INTEGER, songname TEXT, artistname TEXT)");
    	
    	c = myData.rawQuery("SELECT * FROM songs WHERE songname = ?", new String[]{songname});
    	
    	return c;
    }
    
    // search through songs table for songname or artistname matching query
    // eventually extend to a full search of tag and user table as well
    public Cursor fullSearch(String query){
    	Cursor songResults = null;
    	
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS songs (_id INTEGER, songname TEXT, artistname TEXT)");
    	//myData.compileStatement("CREATE TABLE IF NOT EXISTS tags (_id INTEGER, tagName TEXT, category TEXT)");
    	
    	//songResults = myData.rawQuery("SELECT * FROM songs WHERE songname = '" + query + "'", null);
    	//songResults = myData.rawQuery("SELECT * FROM songs WHERE artistname = '" + query + "'", null);
    	songResults = myData.rawQuery("SELECT * FROM songs WHERE songname like ? OR artistname like ?", new String[]{"%" + query + "%", "%" + query + "%"});
    	
    	return songResults;
    	
    }
    
    public void addSong(String song, String artist){
    	
    	//getWritableDatabase();
    	String myPath = DB_PATH + DB_NAME;
    	//myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS|SQLiteDatabase.CREATE_IF_NECESSARY);
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS songs (_id INTEGER, songname TEXT, artistname TEXT)");
    	
    	myData.execSQL("INSERT INTO songs (songname, artistname) VALUES (?, ?)", new Object[]{song, artist});
    	
    	//onUpgrade(myData, 1, 2);
    	
    	
    	/*try {
			createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}*/
    }
    
    public void insertTag(String tagname){
    	
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS tags (_id INTEGER, tagName TEXT, category TEXT)");
    	
    	myData.execSQL("INSERT INTO tags (tagName) VALUES (?)", new Object[]{tagname});
    	
    }
    
    public void updateUserInfo(int user, int song, int tag){
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS userData (_id INTEGER, song INTEGER, tag INTEGER)");
    	
    	myData.execSQL("INSERT INTO userData (_id, song, tag) VALUES (?, ?, ?)", new Object[]{user, song, tag});
    }
    
    public String lastEntryUserData(){
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS userData (_id INTEGER, song INTEGER, tag INTEGER)");
    	Cursor c = myData.rawQuery("SELECT * FROM userData", null);
    	c.moveToLast();
    	int colUser = c.getColumnIndex("_id");
    	int colSong = c.getColumnIndex("song");
    	int colTag = c.getColumnIndex("tag");
    	int user = c.getInt(colUser);
    	int song = c.getInt(colSong);
    	int tag = c.getInt(colTag);
    	
    	String result = "User: " + user + " song: " + song + " tag: " + tag;
    	return result;
    }
    
    public String lastEntrySongs(){
    	String myPath = DB_PATH + DB_NAME;
    	myData = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	myData.compileStatement("CREATE TABLE IF NOT EXISTS songs (_id INTEGER, songname TEXT, artistname TEXT)");
    	Cursor c = myData.rawQuery("SELECT * FROM songs", null);
    	c.moveToLast();
    	int colUser = c.getColumnIndex("_id");
    	int colSong = c.getColumnIndex("songname");
    	int colArtist = c.getColumnIndex("artistname");
    	int user = c.getInt(colUser);
    	String song = c.getString(colSong);
    	String tag = c.getString(colArtist);
    	
    	String result = "#: " + user + " song: " + song + " artist: " + tag;
    	return result;
    }
    
	
}
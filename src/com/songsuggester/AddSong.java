package com.songsuggester;

import com.example.songsuggester.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

public class AddSong extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_song);
		
		/*Button addTag = (Button) findViewById(R.id.assign_tag);
		addTag.setVisibility(View.INVISIBLE);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchable_menu, menu);
		
		// Associate searchable configuration with the SearchView
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.menu_home:
			Intent goHome = new Intent(this, LoggedIn.class);
			startActivity(goHome);
			break;
		case R.id.menu_search:
			onSearchRequested();
			break;
		case R.id.menu_add_song:
			Intent addSong = new Intent(this, AddSong.class);
			startActivity(addSong);
			break;
		case R.id.menu_logout:
			DatabaseHelper db = new DatabaseHelper(this);
			db.dropLogin();
			db.close();
			Intent logout = new Intent(this, MainActivity.class);
			startActivity(logout);
			finish();
			break;
			
		default:
			break;
		}
		return true;
	}
	
	public void addSong(View view){
		
		DatabaseHelper db = new DatabaseHelper(null);
		
		//db.getWritableDatabase();
    	
    	EditText mSongname = (EditText) findViewById (R.id.add_songname);
    	String ssongname = mSongname.getText().toString().toLowerCase().trim();
    	EditText mArtistname = (EditText) findViewById (R.id.add_artistname);
        String sartistname = mArtistname.getText().toString().toLowerCase().trim();	
        
      //Toast.makeText(AddSong.this, songname + "\n" + artistname, Toast.LENGTH_SHORT).show();
		
        
        
        if(ssongname.length() > 0 && sartistname.length() > 0){
        // CHECK IF SONG ALREADY EXISTS
        
        Cursor songInfo = db.songLookupByName(ssongname);
        
        // if the cursor is not null the song is in the db
        if(songInfo != null){
        	// get the artist of the song
        	
        	songInfo.moveToFirst();
        	boolean checkSong = true;
        	
        	int artistCol = songInfo.getColumnIndex("artistname");
        	
        	int count = songInfo.getCount();
        	
        	if(count == 1){
        		String checkart = songInfo.getString(artistCol);
        		if(checkart.equals(sartistname)){
        			checkSong = false;
        			Toast toast = Toast.makeText(AddSong.this, "Song already exists!", Toast.LENGTH_LONG);
                	toast.setGravity(Gravity.CENTER, 0, 0);	
                	toast.show();
        			mSongname.setText("");
        			mArtistname.setText("");	
        		}
        	}
        	
        	if (count > 1){
        		songInfo.moveToFirst();
        		
        	while(songInfo.isAfterLast() == false && checkSong == true){
        	String checkart = songInfo.getString(artistCol);
        	
        		if(checkart.equals(sartistname)){
        			checkSong = false;
        			Toast toast = Toast.makeText(AddSong.this, "Song already exists!", Toast.LENGTH_LONG);
        			toast.setGravity(Gravity.CENTER, 0, 0);	
        			toast.show();
        			mSongname.setText("");
        			mArtistname.setText("");
        			}
        		else{
        			songInfo.moveToNext();
        		}	
        	}
        	}
        	
        	// if there are no more songs in the cursor
        	// and the boolean checkNext is still set to true
        	// then add the song to the db
        	if(checkSong == true){
        		db.addSong(ssongname, sartistname);
        		// check the song was successfully added and notify the user
                Cursor c = db.songLookupByName(ssongname);
                if(c.moveToFirst() == false){
                	Toast toast = Toast.makeText(AddSong.this, "Not added :(", Toast.LENGTH_LONG);
                	toast.setGravity(Gravity.CENTER, 0, 0);
                			toast.show();
                }
                else{
                	Toast toast = Toast.makeText(AddSong.this, "Song added!", Toast.LENGTH_LONG);
                	toast.setGravity(Gravity.CENTER, 0, 0);
                			toast.show();
                	mSongname.setText("");
                	mArtistname.setText("");
                	}  
        	}        	
        }
        
        else{
        // add the song
        db.addSong(ssongname, sartistname);
        // check the song was successfully added and notify the user
        Cursor c = db.songLookupByName(ssongname);
        if(c.moveToFirst() == false){
        	Toast toast = Toast.makeText(AddSong.this, "Not added :(", Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        }
        else{
        	Toast toast = Toast.makeText(AddSong.this, "Song added!", Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER, 0, 0);
        			toast.show();
        	mSongname.setText("");
        	mArtistname.setText("");
        	}       
        }
        
        /*//String userData = db.lastEntryUserData();
        String songData = db.lastEntrySongs();
        Toast toast = Toast.makeText(AddSong.this, songData, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/
		}
        else{
        	Toast toast = Toast.makeText(AddSong.this, "Please insert a song name and artist!", Toast.LENGTH_LONG);
        	toast.setGravity(Gravity.CENTER, 0, 0);
        	toast.show();
        }
	}
	
	public void addTag(View view){
		Intent intent = new Intent(this, AddTag.class);
		startActivity(intent);
	}

}

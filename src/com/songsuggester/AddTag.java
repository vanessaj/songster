package com.songsuggester;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.songsuggester.R;

public class AddTag extends Activity {

	//public static boolean check;
	public static int songidnum;
	//public static String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_tag);
		
		Bundle extras = getIntent().getExtras();
		
		long received = extras.getLong("ID");
		//check = extras.getBoolean("CHECK");
		//user = extras.getString("USER");
		
		
		songidnum = (int) received;
				tagDisplay(songidnum);
		
		/*TextView mTextView = (TextView) findViewById(R.id.id_display);
		mTextView.setText("ID of song is " + received);*/
		
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
			db.close();
			db.dropLogin();
			Intent logout = new Intent(this, MainActivity.class);
			startActivity(logout);
			finish();
			break;
			
		default:
			break;
		}
		return true;
	}
	
	
	public void tagDisplay(int id){
		TextView mText = (TextView) findViewById(R.id.display_song_info);
		
		DatabaseHelper db = new DatabaseHelper(null);
		String result = db.songLookup(id);
		
		ModifyTextCase modText = new ModifyTextCase();
		String casedResult = modText.camelCase(result);
				
		mText.setText("For:" + casedResult + "\n" + "Assign a tag!");
	}
	
	public void addTag(View view){
		
		EditText mEditText = (EditText) findViewById(R.id.insert_tag);
		String tag = mEditText.getText().toString().trim();
		
		DatabaseHelper db = new DatabaseHelper(null);
		String user = db.getLoggedUser();
		
		if(tag.length() == 0){
			Toast.makeText(AddTag.this, "Please input a tag!", Toast.LENGTH_SHORT).show();
		}
		
		else if(user.equals("Guest")){
			Toast toast = Toast.makeText(AddTag.this, "Must be logged in to assign a tag!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);	
			toast.show();
		}
		
		else if(tag.length() > 0){
			// get tag id			
			int tag_id_found = getTagId(tag);
			
			//Toast.makeText(AddTag.this, "Tag ID = " + id_found, Toast.LENGTH_SHORT).show();
			
			Cursor cursor = db.userLookup(user);
			
			// find the id of the user
			// if it doesn't exist
			if(cursor.moveToFirst() == false){
				Toast toast = Toast.makeText(AddTag.this, "User does not exist!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);	
				toast.show();
			}
			// else if user does exist get the id num
			// insert the user id, the song id and the tag id
			// represents this # user assigning this # tag to this # song
			else{
				cursor.moveToFirst();
				int coluserID = cursor.getColumnIndex("_id");
				int userID = cursor.getInt(coluserID);
				
				db.updateUserInfo(userID, songidnum, tag_id_found);
				// display if successful
				Toast toast = Toast.makeText(AddTag.this, "Tag added!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);	
				toast.show();
				mEditText.setText("");
			}
			
			
			
		}
		
	}
	
	public int getTagId(String tag){
		int tagID = 0;
		DatabaseHelper db = new DatabaseHelper(null);
		// look up the inputted tag
		Cursor c = db.tagLookup(tag);
	
		// if it's not in db add it, get the id for the insert
		if(c.moveToFirst() == false){
			// add tag
			db.insertTag(tag);
			Cursor newC = db.tagLookup(tag);
			
			if(newC.moveToFirst() == false){
				Toast toast = Toast.makeText(AddTag.this, "Tag not inserted!", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);	
				toast.show();
			}
			else{
			newC.moveToFirst();
			int colTagID = newC.getColumnIndex("_id");
			tagID = newC.getInt(colTagID);}
		}
		// else get the id of it
		else{
			c.moveToFirst();
			int colTagID = c.getColumnIndex("_id");
			tagID = c.getInt(colTagID);
		}
		return tagID;
	}

}

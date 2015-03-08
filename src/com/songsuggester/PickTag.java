package com.songsuggester;

import com.example.songsuggester.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

public class PickTag extends Activity {

	//public static String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_tag);
		
		//Bundle extras = getIntent().getExtras();
		
		//String username = extras.getString("USERNAME");
		
		//Toast.makeText(PickTag.this, "user: " + username, Toast.LENGTH_LONG).show();

		//user = username;

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

	
	// use userString from LoggedIn
	
	// get id of tag selected
	// make string of k 0s, k = # tags in DB
	
	// make string of m 0s, m = # songs in DB
	
	// concactentate userString + tagString + songString
	// makes vectorString
	// call MatrixData (vectorString)
	

	
	public void decadePicked(View view){
		Intent intent = new Intent(this, DecadeTag.class);
		startActivity(intent);
		}
	
	public void genrePicked(View view){
		Intent intent = new Intent(this, GenreTag.class);
		startActivity(intent);
		} 
	
	public void moodPicked(View view){
		Intent intent = new Intent(this, MoodTag.class);
		startActivity(intent);
		}
	
	public void descriptionPicked(View view){
		Intent intent = new Intent(this, DescriptionTag.class);
		startActivity(intent);
		}
	
	public void tagSearch(View view){
		EditText edittext = (EditText)findViewById(R.id.searchByTag);
		String tag = edittext.getText().toString().toLowerCase().trim();
		
		//Toast.makeText(PickTag.this, "Username: " + user + " tag: " + tag, Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent(this, ResultsTag.class);
		intent.putExtra("SELECTED_TAG", tag);
		startActivity(intent);
		
	}
	
}

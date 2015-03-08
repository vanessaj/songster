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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

public class FindUsers extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_users);
		
		Spinner spinner = (Spinner) findViewById(R.id.song_spinner);
		// array adapter
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		R.array.song_array, android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);
		
		

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
	
	
	
	public void goFindUsers(View view){
		
		// get ID of song selected in drop down menu
		Spinner spinner = (Spinner) findViewById(R.id.song_spinner);
		int pos = spinner.getSelectedItemPosition();
		pos = pos + 1; 
		String songID = Integer.toString(pos);
		
		// get string of inputted tag
		EditText edittext = null;
		edittext = (EditText)findViewById(R.id.inputted_tag);
		String inputtedTag = edittext.getText().toString().toLowerCase().trim();
		
		// pass the two strings to next activity
		Intent intent = new Intent(this, ResultsUsers.class);
		intent.putExtra("SONGID", songID);
		intent.putExtra("TAG", inputtedTag);
		
		startActivity(intent);
		
	}
	
	

}

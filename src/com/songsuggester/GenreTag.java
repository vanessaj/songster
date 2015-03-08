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
import android.widget.SearchView;
import android.widget.Spinner;

public class GenreTag extends Activity {

	//protected String userGen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genre_tag);
		
		
		
		Spinner spinner = (Spinner) findViewById(R.id.tag_genre_spinner);
		// array adapter
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		R.array.tagsGenre, android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinner.setAdapter(adapter);
		
		//Bundle extras = getIntent().getExtras();
		
		//String username = extras.getString("USERNAME");
		//userGen = username;
		
		//Toast.makeText(GenreTag.this, "user: " + username, Toast.LENGTH_LONG).show();
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

	
	public void onGenreSelect(View view){
		
		// get id of song picked
		// match to song list
		
		Spinner spinner = (Spinner) findViewById(R.id.tag_genre_spinner);
		String tag = spinner.getSelectedItem().toString();
		//String username = userGen;
		Intent intent = new Intent(this, ResultsTag.class);
		intent.putExtra("SELECTED_TAG", tag);
		//intent.putExtra("NAME", username);
		startActivity(intent);
	}
}

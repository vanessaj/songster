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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class LoggedIn extends Activity {


	//public static String username;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logged_in);
		
			
			//MainActivity main = new MainActivity();
			//username = main.getName();
			DatabaseHelper db = new DatabaseHelper(null);
			String username = db.getLoggedUser();
			
			String usergreeting = "Welcome, ";
			
			TextView userinfo = (TextView) findViewById(R.id.username_display);
			userinfo.setText(usergreeting + username);

/*			TextView boolDisplay = (TextView)findViewById(R.id.user_bool_display);
			boolDisplay.setText(valueUser);*/
			
			
				
		
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

	
/*	public void searchClick (View view){
		// string for input in search box
		// verify string is in array
		// if not put pop up toast saying it can't be found
		
		// else
		 Intent intent = new Intent(this, SearchSong.class);
		 //pass string
		 startActivity(intent);
	}*/
	
	
	public void tagClick(View view){
/*    	TextView userinfo = (TextView) findViewById(R.id.username_display);
    	CharSequence user = userinfo.getText();
		int length = user.length();
		String username = user.subSequence(9, length).toString();*/
			
		Intent intent = new Intent(this, PickTag.class);
		//intent.putExtra("USERNAME", username);
		startActivity(intent);
	}

	public void usersClick(View view){
		Intent intent = new Intent(this, FindUsers.class);
		startActivity(intent);
	}
	

	
	// logout
	public void logoutClick(View view){
		// close any dbs
		Intent intent = new Intent(this, MainActivity.class);
		DatabaseHelper db = new DatabaseHelper(null);
		db.dropLogin();
		db.close();
		startActivity(intent);
	}
	
	public void addNewSong(View view){
		Intent intent = new Intent(this, AddSong.class);
		startActivity(intent);
	}



}

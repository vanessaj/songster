package com.songsuggester;

import com.example.songsuggester.R;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SearchableActivity extends Activity {
	
	private TextView myTextView;
	private ListView myListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable);
		myTextView = (TextView) findViewById(R.id.results_text);
		myListView = (ListView) findViewById(R.id.list);
		
		
		//mTextView.setText("hello there");
		
		//handleIntent(getIntent());
/*		Intent intent = getIntent();
		String searched = intent.getAction();
		mTextView.setText(searched);*/
		handleIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchable_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.menu_home:
			Intent goHome = new Intent(this, LoggedIn.class);
			startActivity(goHome);
			break;
		//case R.id.menu_search:
			// start search for new song
			//break;
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
	
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent){
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase().trim();
			//mTextView.setText(query);
			displayList(query);
		}
		else{
			String searched = intent.getAction();
			myTextView.setText(searched);
		}
	}
	

	public void displayList(String query){
		DatabaseHelper db = new DatabaseHelper(this);
		//db.getReadableDatabase();
		//db.openDataBase();
		Cursor cursor = db.fullSearch(query);
			
		if(cursor.moveToFirst() == false){
			// no results
			myTextView.setText("No results!" + "\n"
					+ "Check your spelling and search again"
					+ "\n"
					+ "Or add the song"
					);
			//setContentView(R.layout.list_layout);
		}
		else{
			
			/*cursor.moveToFirst();
			int songnameCol = cursor.getColumnIndex("songname");
			int artistCol = cursor.getColumnIndex("artistname");
    		String resultSong = cursor.getString(songnameCol);
    		String resultArtist = cursor.getString(artistCol);
			
    		String display = "Name: " + resultSong + " by: " + resultArtist;
    		
    		mTextView.setText(display);*/
    		
			
			// display # of results
			int count = cursor.getCount();
			//String countString = getResources().getQuantityString(R.plurals.search_results, count, new Object[] {count, query});
			
			if (count == 1){
				myTextView.setText(count + " result" + "\n" + "Click to add tag");
			}
			else {
			myTextView.setText(count + " results" + "\n" + "Click to add tag");
			}
			
			
			// specify columns to display
			//String[] from = new String[] {db.SONG_COLUMN, db.ARTIST_COLUMN};
			String[] from = new String[] {"songname", "artistname"};
			
			
			
			// specify layout
			int[] to = new int[] { R.id.song, R.id.artist};
			
			// cursor adapter then apply results to ListView
			SimpleCursorAdapter display = new SimpleCursorAdapter(this, R.layout.list_layout, cursor, from, to, 0);
			myListView.setAdapter(display);	
			
			 
			// dealing with clicks in layout
			OnItemClickListener mMessageClickedHandler = new OnItemClickListener(){
				public void onItemClick(AdapterView parent, View v, int position, long id){
					
					/*MainActivity main = new MainActivity();
					boolean check = main.checkLoggedStatus();
					String user = main.getName();*/
					
					//Toast.makeText(SearchableActivity.this, "Check looged = " + check + "!" +"\n" + "Username: " + user, Toast.LENGTH_LONG).show();
					
					Intent intent = new Intent(v.getContext(), AddTag.class);
					intent.putExtra("ID", id);
					//intent.putExtra("CHECK", check);
					//intent.putExtra("USER", user);
					startActivity(intent);
					
				}
			};
			
			myListView.setOnItemClickListener(mMessageClickedHandler);
			
			 
		}
		
		
		
		
		
	}
}

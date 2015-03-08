package com.songsuggester;

import java.util.Arrays;
import java.util.Locale;

import Jama.Matrix;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.songsuggester.R;

public class ResultsTag extends Activity {
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results_tag);
		
		Bundle extras = getIntent().getExtras();
		
		String tagPicked = extras.getString("SELECTED_TAG");
		//String username = extras.getString("NAME");
		DatabaseHelper db = new DatabaseHelper(null);
		String username = db.getLoggedUser();
		
		tagResults(tagPicked, username);	
		
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
			finish();
			startActivity(logout);
			
			break;
			
		default:
			break;
		}
		return true;
	}
	
	protected void tagResults(String tag, String username){
		
		// get the tag num and username
		String rawtagDisplay = tag;
		
		String usernameDisplay = username;
		String output = "";
		
		String tagDisplay = rawtagDisplay.toLowerCase(Locale.CANADA);
		
		boolean tagInDB = false;
		
		// connect to the db
		DatabaseHelper db = new DatabaseHelper(this);
		int tagOutput;
		int userMatPos;
				
    	// new matrix class
		MatrixData myMatrix = new MatrixData();
		
		// get # users, songs & total num of items per row/col in matrix
		int users = myMatrix.getNumUsers();
		int songs = myMatrix.getNumSongs();
		int total = myMatrix.getTotalNum();
		
		// make vector (one row matrix), the length of the adjacency matrix
		Matrix vectorMatrix = new Matrix(1, total);

		// get position for tag
    	Cursor result = db.tagLookup(tagDisplay);
    	
    	
    	if(result.moveToFirst() == false){
    		// tag isn't in DB
    		// leave 0s in matrix
    		Toast.makeText(ResultsTag.this, "Tag not in db", Toast.LENGTH_LONG).show();
    		tagInDB = false; 
    	}
    	else{
    		// set one at the position
    		result.moveToFirst();
    		int pos = result.getColumnIndex("_id");
    		tagOutput = result.getInt(pos);
    		tagOutput = tagOutput - 1;
    		int tagMatPos = users + songs + tagOutput;
    		
    		vectorMatrix.set(0, tagMatPos, 1);
    		
    		tagInDB = true;
    		
    		//Toast.makeText(ResultsTag.this, "tag found", Toast.LENGTH_LONG).show();
    	}
    	
    	// get position for user
    	Cursor userresult = db.userLookup(usernameDisplay);
    	
    	if(userresult.moveToFirst() == false){
    		// user isn't in db
    		// this should be the case of a guest user
    		// don't add anything to vector matrix
    		// leave it as 0s for all user positions
    	}
    	else {
    		// set one at position for user
    		result.moveToFirst();
    		int posuser = userresult.getColumnIndex("_id");
    		int userID = userresult.getInt(posuser);
    		userMatPos = userID -1;
    		
    		vectorMatrix.set(0, userMatPos, 1);
    	}
    	
    	// get the modified adjacency matrix
    	Matrix adjMatFinal = myMatrix.finalAdjacencyMatrix();
    	
    	
    	// make new results matrix by multiplying the vector matrix with the adjacency matrix
    	Matrix results = adjMatFinal.times(vectorMatrix.transpose());
    	
    	
//		int limit = results.getRowDimension();
//		
//		// output the results
//		for(int j =0; j<limit; j++){
//			double value = results.get(j, 0);
//			String svalue = Double.toString(value);
//			output = output + svalue + " ";
//		}
//    	
//		TextView display = (TextView) findViewById(R.id.tagSelectedResults);
//		display.setText("Matrix result: " + output);
    	

		// user is picked
		// tag is picked
		// find a song suggestion
		
		// multidimensional array with rows, columns
		// row = song #
		// col 1 = song ID, col 2 = value from matrix
		double[][] data = new double[songs][2];
		
		int startSongs = users;
		// fill the array
		for(int k = 0; k < songs; k++){
			//int startSongs = users - 1;
			
			double currRow = k;
			double matrixValue = results.get(k + startSongs, 0);
			
			data[k][0] = currRow;
			data[k][1] = matrixValue;
		}
		
		// sort the array
		Arrays.sort(data, new java.util.Comparator<double[]>()
				{
					public int compare(final double[] entry1, final double[] entry2){
						final double value1 = entry1[1];
						final double value2 = entry2[1];
						return Double.compare(value2, value1);
					}
				});
		
		

		
/*		// print the sorted array to check
		for(int i = 0; i < songs; i++){
			for(int j = 0; j<2; j++){
				double val = data[i][j];
				String valS = Double.toString(val);
				output = output + valS;
				output = output + " ";
				// to match to songID add 1
				// the one is added when the songID is looked up to find the name
			}
			output = output + "\n";
		}
		
		TextView display = (TextView) findViewById(R.id.tagSelectedResults);
		display.setText("Matrix result: " + output);
		
		//String songNum = "# = " + songs;
		String printData = "Start songs @ " + startSongs;
		Toast.makeText(ResultsTag.this, printData, Toast.LENGTH_LONG).show();*/
		
		// look the first five songs up in the database
		for(int i = 0; i<5; i++){
			double songRankdoub = data[i][0];
			int songRank = (int) songRankdoub;
			
			songRank = songRank + 1;
			String songNameArtist = db.songLookup(songRank);
			ModifyTextCase modifyText = new ModifyTextCase();
			songNameArtist = modifyText.camelCase(songNameArtist);
			int j = i + 1;
			output = output + "#" + j + " " + songNameArtist + "\n";
		}
		
		String tagDisplayPreamble;
		
		if(tagInDB == true){
			tagDisplayPreamble = "Song suggestions: ";
		}
		else {
			tagDisplayPreamble = "The tag was not found!" + "\n" + "Results based on user info:";
		}
		
		TextView display = (TextView) findViewById(R.id.tagSelectedResults);
		display.setText(tagDisplayPreamble + "\n" + output);
		//display.setText("Tag: " + tagDisplay + "\n" + "User:" + usernameDisplay);
		
		//String songInfo = db.songLookup(0);
		//Toast.makeText(ResultsTag.this, songInfo, Toast.LENGTH_LONG).show();
		}

}

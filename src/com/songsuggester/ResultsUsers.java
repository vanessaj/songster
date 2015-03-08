package com.songsuggester;

import java.io.IOException;
import java.util.Arrays;

import com.example.songsuggester.R;

import Jama.Matrix;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

public class ResultsUsers extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results_users);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			
			String songID = extras.getString("SONGID");
			String tag = extras.getString("TAG");
			
			displayResults(songID, tag);
			
/*			TextView displaySongId = (TextView) findViewById(R.id.displaySongID);
			displaySongId.setText(songID);
			
			TextView displayTag = (TextView) findViewById(R.id.displayInputtedTag);
			displayTag.setText(tag);*/
		}

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
	
	
	// don't need to take in userString because finding similar users
	
	// make string of k 0s, k = # songs
	// get id of song selected from spinner
	// add 1 at this index number (the songid, they should go 0-n)
	
	// check if inputted tag is already in database
	// if (not) don't put number
	// else add 1 at index position of id #
	
	// take # of users, j
	// make string of length j # 0s
	
	
	protected void displayResults(String song, String tag){
		String songID = song;
		String tagName = tag;
		int tagID;
		
		//TextView displaySongId = (TextView) findViewById(R.id.displaySongID);
/*		displaySongId.setText(songID);
		
		TextView displayTag = (TextView) findViewById(R.id.displayInputtedTag);
		displayTag.setText(tagName);*/
		
		int songNum = Integer.parseInt(songID);
		
		songNum = songNum - 1;
		
		MatrixData myMatrix = new MatrixData();
		
		int users = myMatrix.getNumUsers();
		int songs = myMatrix.getNumSongs();
		int total = myMatrix.getTotalNum();
		
		int songPos = users + songNum;
		String output = "";
		
		Matrix userMatrix = new Matrix(1, total);
		
		// set the song
		userMatrix.set(0, songPos, 1);
		
		// Find if inputted tag is in DB
		DatabaseHelper db = new DatabaseHelper(this);
		
		try {
    		db.createDatabase();
    	} catch (IOException ioe) {
    		throw new Error("Unable to create database");
    		}
    	try {
			db.openDatabase();
    	}catch(SQLException sqle){
    		throw sqle;
    		}
		
		Cursor c = db.searchTag(tagName);
		
		if(c.moveToFirst() == false){
			// tag is not in database
			// use 0s for matrix, so don't change it
		}
		else{
			// tag is in database, get id
			c.moveToFirst();
			int colID = c.getColumnIndex("_id");
			tagID = c.getInt(colID);
			tagID = tagID - 1;
			int tagpos = users + songs + tagID;
			userMatrix.set(0, tagpos, 1);
		}
		c.close();
		//myDbHelper.close();
		
		// get the modified adjacency matrix
		Matrix adjMatFinal = myMatrix.finalAdjacencyMatrix();
		
		// multiply the one row made for users with the whole adjacency matrix
		Matrix results = adjMatFinal.times(userMatrix.transpose());
		
/*		int limit = results.getRowDimension();
		
		// output the results
		for(int j =0; j<limit; j++){
			double value = results.get(j, 0);
			String svalue = Double.toString(value);
			output = output + svalue + " ";
		}
		
		//String output = "Adjacency col dims: " + adjMat.getColumnDimension() + "Adj row dims: " + adjMat.getRowDimension() +" User col dims: " + userMatrix.getColumnDimension();
		
		displaySongId.setText("Matrix result : " + output);*/
		
		// tag is picked
		// song is picked
		// find the most similar user
		
		// multidimensional array with rows, columns
		// row = song #
		// col 1 = song ID, col 2 = value from matrix
		double[][] data = new double[users][2];
		
		// fill the array
		for(int k = 0; k < users; k++){
						
			double currRow = k;
			double matrixValue = results.get(k, 0);
			
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
		
		
		/*for(int i = 0; i < users; i++){
			for(int j = 0; j<2; j++){
				double val = data[i][j];
				String valS = Double.toString(val);
				output = output + valS;
				output = output + " ";
			}
			output = output + "\n";
		}*/
		
		
		// look the first five users up in the database
		for(int i = 0; i<5; i++){
			double userRankdoub = data[i][0];
			int userRank = (int) userRankdoub;
			userRank = userRank + 1;
			String nameUser = db.userLookupByID(userRank);
			int j = i + 1;
			output = output + "#" + j + " " + nameUser + "\n";
		}
		
		TextView display = (TextView) findViewById(R.id.displaySongID);
		display.setText("Simliar users: " + "\n" + output);
		
		
		
		
	}

}

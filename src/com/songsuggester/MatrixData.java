package com.songsuggester;


import java.io.IOException;

import android.database.Cursor;
import android.database.SQLException;
import Jama.Matrix;

public class MatrixData {

	
	
	protected int getNumUsers(){
		DatabaseHelper myDbHelper = new DatabaseHelper(null);
		int numUsers = myDbHelper.getNumUsers();
		return numUsers;
		
	}
	
	protected int getNumSongs(){
		DatabaseHelper myDbHelper = new DatabaseHelper(null);
    	int numSongs = myDbHelper.getNumSongs();
		return numSongs;
	}

	protected int getNumTags(){
		DatabaseHelper myDbHelper = new DatabaseHelper(null);
		int numTags = myDbHelper.getNumTags();
		return numTags;
	}
	
	protected int getTotalNum(){
		int totalnum;
		
		int numUsers = getNumUsers();
		int numSongs = getNumSongs();
		int numTags = getNumTags();
		
		totalnum = numUsers + numSongs + numTags;
		return totalnum;
	}
	
	// create the relationship matrix using the # of users, songs and tags
	protected Matrix createAdjacencyMatrix(){
		int totalnum = getTotalNum();
		
		Matrix rels = new Matrix(totalnum,totalnum);	
		return rels;
	}
	
	protected Matrix adjacencyMatrix(){
		
		int usersTotal = getNumUsers();
		int songsTotal = getNumSongs();
		int tagsTotal = getNumTags();
		//create matrix with dimensions of users+songs+tags filled with zeroes
		Matrix matrix = createAdjacencyMatrix();
		
		// connect to db
		DatabaseHelper db = new DatabaseHelper(null);
		// call matrix filling method
		Cursor results = db.getSampleData();
		
		results.moveToFirst();
		while(results.isAfterLast() == false){
			
			int userNumIndex = results.getColumnIndex("_id");
			int userNum = results.getInt(userNumIndex);
			int songNumIndex = results.getColumnIndex("song");
			int songNum = results.getInt(songNumIndex);
			int tagNumIndex = results.getColumnIndex("tag");
			int tagNum = results.getInt(tagNumIndex);
			
			// user-song insert
			// put 1 if song has been tagged by user (binary)
				// set quadrant 4 (user,song)
				matrix.set(usersTotal + songNum - 1, userNum -1 , 1);
				// set quadrant 2, reverse of previous (song,user)
				matrix.set(userNum -1 , usersTotal + songNum -1, 1);
			
			
			// user-tag insert
			// set frequency of amount of times specified user has assigned specified tag
				// set quadrant 7 (user,tag)
				double currVal = matrix.get(usersTotal + songsTotal + tagNum -1, userNum -1);
				double nextVal = currVal + 1;
				matrix.set(usersTotal + songsTotal + tagNum -1, userNum -1, nextVal);
				
				// set quadrant 3 (tag,user)
				double currVal2 = matrix.get(userNum -1, usersTotal+songsTotal+tagNum -1);
				double nextVal2 = currVal2 + 1;
				matrix.set(userNum -1, usersTotal+songsTotal+tagNum -1, nextVal2);
				
			// song-tag insert
			// set frequency of amount of times song has been tagged with this tag
				// set quadrant 8 (song,tag)
				double currVal3 = matrix.get(usersTotal+songsTotal+tagNum -1, usersTotal+songNum -1);
				double nextVal3 = currVal3 + 1;
				matrix.set(usersTotal+songsTotal+tagNum -1, usersTotal+songNum -1, nextVal3);
				
				// set quadrant 6
				double currVal4 = matrix.get(usersTotal+songNum -1, usersTotal+songsTotal+tagNum -1);
				double nextVal4 = currVal4 + 1;
				matrix.set(usersTotal+songNum -1, usersTotal+songsTotal+tagNum -1, nextVal4);
			
			// quadrants 1, 5 and 9 all remain set to 0s as they're user,user, song,song and tag,tag
				results.moveToNext();
		}
		
		results.close();
		return matrix;
	}
	
	public Matrix finalAdjacencyMatrix(){
		Matrix adjMat = adjacencyMatrix();
		
    	// get identity matrix of adjacency matrix
    	Matrix adjMatIdentity = identityMatrix();
    	
    	// multiply by alpha = 0.005
    	Matrix adjMatFinal = adjMat.times(0.005);
    	
    	// subtract (alpha * A) from I
    	adjMatFinal = adjMatIdentity.minus(adjMatFinal);
    	// find inverse of this matrix
    	adjMatFinal = adjMatFinal.inverse();
    	// subtract the inverse of A from (I - alphaA)^-1
    	adjMatFinal = adjMatFinal.minus(adjMatIdentity);
		
		return adjMatFinal;
	}
	
	protected String matrixDBResults(){
		// connect to db
		DatabaseHelper db = new DatabaseHelper(null);
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
		// call matrix filling method
		Cursor results = db.getSampleData();
		
		results.moveToFirst();
		
		String result = "";
		
		while(results.isAfterLast() == false){
			
			int userNumIndex = results.getColumnIndex("_id");
			int usernum = results.getInt(userNumIndex);
			int songNumIndex = results.getColumnIndex("song");
			int songNum = results.getInt(songNumIndex);
			int tagNumIndex = results.getColumnIndex("tag");
			int tagnum = results.getInt(tagNumIndex);
			
		String a = Integer.toString(usernum);
		String b = Integer.toString(songNum);
		String c = Integer.toString(tagnum);
			
		
		result =  result + "User: "+ a + " song: " + b + " tag: " + c + "\n";
		
		results.moveToNext();
		
		}
		return result;
	}
	
	protected Matrix identityMatrix(){
		int total = getTotalNum();
		Matrix idMatrix = new Matrix(total, total);
		idMatrix = idMatrix.identity(total, total);
		return idMatrix;
	}
	
	
	
	// when user assigns a new tag to a song the matrix is accessed & updated at the tag#, user# and item#
	
	// use (I - alphaA) on matrix, then take inverse
	
	// take in the vectorString the class was called with
	// multiply this vector with the matrix
	
	// get the results, pass this string to resultDisplay

	// in resultDisplay split it up into userString, tagString & songString
	// for whatever one isn't all 0s:
	
	// make an array that assigns the scores from the matrix multiplication with the index # of the row
	// order by score, display userName/tagName/songName by looking up id in DB
	
}

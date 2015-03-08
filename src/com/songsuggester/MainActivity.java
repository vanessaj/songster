package com.songsuggester;


import java.io.IOException;

import com.example.songsuggester.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*TextView passwordBox = (TextView) findViewById(R.id.password);
		passwordBox.setText("");*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// User login with info
	public void userLogin(View view){
		//Intent intent = new Intent(this, LoggedIn.class);
		
		
		EditText edittext = null;
		edittext = (EditText)findViewById(R.id.usernameInput);
		String username = edittext.getText().toString();
		

		
		// get password
		EditText passwordText = null;
		passwordText = (EditText)findViewById(R.id.password);
		String password = passwordText.getText().toString();
		
		
		DatabaseHelper myDbHelper = new DatabaseHelper(this);
         
        	try {
        		myDbHelper.createDatabase();
        	} catch (IOException ioe) {
        		throw new Error("Unable to create database");
        		}
        	try {
 			myDbHelper.openDatabase();
        	}catch(SQLException sqle){
        		throw sqle;
        		}
		
		Cursor cursorfoundPassword = myDbHelper.getPassword(username);
		
			
		if(cursorfoundPassword.moveToFirst() == true){
			
			/*if(foundPassword.equals("THE TABLE IS EMPTY")){
				Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
			}*/
			
			cursorfoundPassword.moveToFirst();
    		int placeColumn = cursorfoundPassword.getColumnIndex("password");
    		String foundPassword = cursorfoundPassword.getString(placeColumn);
			
			if(password.equals(foundPassword)){
				
				//checkLogged = true;
				//name_of_user = username;
				myDbHelper.createLogin();
				myDbHelper.loginUser(username);
				
				
				Intent intent = new Intent(this, LoggedIn.class);
				//intent.putExtra("text_user", username);
				//intent.putExtra("CHECKUSER", "user");
				startActivity(intent);
				}
			
			else{
				Toast.makeText(this, "Wrong password! Try again", Toast.LENGTH_LONG).show();

				}
			}
		
		else{
				Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
		}
	}
	
	public String getUsername(){
		EditText edittext = null;
		edittext = (EditText)findViewById(R.id.usernameInput);
		String username = edittext.getText().toString();
		return username;
	}
	
	
	// Guest login
	// don't have # for user part of vector
	public void loginClick(View view){
		
		DatabaseHelper myDbHelper = new DatabaseHelper(this);
        
    	try {
    		myDbHelper.createDatabase();
    	} catch (IOException ioe) {
    		throw new Error("Unable to create database");
    		}
    	try {
			myDbHelper.openDatabase();
    	}catch(SQLException sqle){
    		throw sqle;
    		}
    	
    	//checkLogged = false;
		String name_of_user = "Guest";
		
		myDbHelper.createLogin();
		myDbHelper.loginUser(name_of_user);
    	
		Intent intent = new Intent(this, LoggedIn.class);
		//intent.putExtra("text_user", "Guest");
		//intent.putExtra("CHECKUSER", "guest");
		startActivity(intent);
	}
	

}

package com.songsuggester;

public class ModifyTextCase {

	public String camelCase(String text){
				
		text = text.toUpperCase();
		
		StringBuffer sb = new StringBuffer();
	    for (String s : text.split(" ")) {
	    	sb = sb.append(" ");
	    	sb.append(Character.toUpperCase(s.charAt(0)));
	        if (s.length() > 1) {
	            sb.append(s.substring(1, s.length()).toLowerCase());
	        }
	    }
		
	    String modify = sb.toString();
	    
		return modify;
	}
}

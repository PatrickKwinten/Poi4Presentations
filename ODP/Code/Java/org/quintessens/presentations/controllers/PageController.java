package org.quintessens.presentations.controllers;

import java.util.ArrayList;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.domino.xsp.module.nsf.NotesContext;

public class PageController {

	public static ArrayList<JsonJavaObject> loadJSONObjects(String ServerName, String DatabaseName, String ViewName, String Key, Integer ColIdx) throws NotesException {
		ArrayList<JsonJavaObject> JSONObjects = new ArrayList<JsonJavaObject>();		
		NotesContext nct = NotesContext.getCurrent();
		Session session = nct.getCurrentSession();
		Database DB = session.getDatabase(ServerName, DatabaseName);		
		if (!(DB==null)) {
			View luView = DB.getView(ViewName); 
			if (!(luView == null)) {
				JsonJavaFactory factory = JsonJavaFactory.instanceEx;				
				ViewEntryCollection vec = luView.getAllEntries();
				ViewEntry entry = vec.getFirstEntry();
				while (entry != null) {					
					Vector<?> columnValues = entry.getColumnValues();
					String colJson = String.valueOf(columnValues.get(ColIdx));
					JsonJavaObject json = null;					
					try {
						json = (JsonJavaObject) JsonParser.fromJson(factory, colJson);
						if (json != null) {
							JSONObjects.add(json);
						}							
					} catch (JsonException e) {
						System.out.println("ERROR somewhere");
					}						
				   ViewEntry tempEntry = entry;
				   entry = vec.getNextEntry();
				   tempEntry.recycle();
				} 	
				luView.recycle();
			}
			DB.recycle();
		}
		return JSONObjects;
	}	
}
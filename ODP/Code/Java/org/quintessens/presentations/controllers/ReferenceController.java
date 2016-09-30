package org.quintessens.presentations.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;

import org.openntf.domino.xsp.XspOpenLogUtil;
import org.quintessens.JSFUtil;

import com.ibm.commons.util.io.json.JsonJavaObject;

public class ReferenceController extends PageController {
	
	private String objectId;
	private Date created;	
	private String name;
	private String sub;
	private Vector<String> strength;
	private Vector<String> weakness;
	
	public void reset() {
		created = null;
		name = null;
		sub = null;
		strength = null;
		weakness = null;
	}
	
	public void loadByUnid(String unid) throws NotesException{
		JSFUtil jsfUtil = new JSFUtil();
		Session session = jsfUtil.getSession();	
		Database db = session.getCurrentDatabase();
		Document doc = db.getDocumentByUNID(unid);		
		if (null == doc) {
			XspOpenLogUtil.logError(null, null, this.getClass().getSimpleName().toString() + ". loadByUnid(String unid). Document not found", Level.SEVERE, null);			
		} else {
			loadValues(doc);
		}
		doc.recycle();
		db.recycle();
	}
	
	public void loadValues(Document doc) throws NotesException {
		objectId = doc.getUniversalID();
		name = doc.getItemValueString("refName");
		sub = doc.getItemValueString("refSub");
		strength = doc.getItemValue("refStrength");
		weakness = doc.getItemValue("refWeakness");
		
		/*
		 * List<String> rtn = new ArrayList<>();
rtn.add(thisDoc.getItemValue("WFSOriginator").get(0));
		 * 
		 */
}

	public ArrayList<JsonJavaObject> loadObjects(String viewName, Integer colIdx) throws NotesException {
		JSFUtil jsfUtil = new JSFUtil();
		Session session = jsfUtil.getSession();		
		ArrayList<JsonJavaObject> jsonObjects = new ArrayList<JsonJavaObject>();		
		Database db = session.getCurrentDatabase();
		String ViewName = viewName;	
		if (!(db==null)) {
			if ((db.isOpen())) {
				//GOOD
				try {
					jsonObjects = PageController.loadJSONObjects(db.getServer(), db.getFilePath(), ViewName, "", colIdx);
				} catch (NotesException e) {
					XspOpenLogUtil.logError(null, e, this.getClass().getSimpleName().toString() + ". loadPictures().", Level.SEVERE, null);
				}
			}
			else{
				//BAD
				XspOpenLogUtil.logError(null, null, this.getClass().getSimpleName().toString() + ". loadPictures(). DB not found.", Level.SEVERE, null);		
			}
		}
		db.recycle();		
		return jsonObjects;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		sub = sub;
	}

	public Vector<String> getStrength() {
		return strength;
	}

	public void setStrength(Vector<String> strength) {
		this.strength = strength;
	}

	public Vector<String> getWeakness() {
		return weakness;
	}

	public void setWeakness(Vector<String> weakness) {
		this.weakness = weakness;
	}
	
	
	
}

package org.rebioma.server.upload;

public class TraitementEvent {
	String oldTraitement;
	long oldTotal;
	long oldDone;
	String newTraitement;
	long newTotal;
	long newDone;
	
	TraitementEvent(String oldTraitement,String newTraitement,long oldTotal,long newTotal,long oldDone,long newDone) {
		this.oldTraitement = oldTraitement;
		this.newTraitement = newTraitement;
		this.oldTotal = oldTotal;
		this.newTotal = newTotal;
		this.oldDone = oldDone;
		this.newDone = newDone;		
	}

	public String getOldTraitement() {
		return oldTraitement;
	}

	public long getOldTotal() {
		return oldTotal;
	}

	public long getOldDone() {
		return oldDone;
	}

	public String getNewTraitement() {
		return newTraitement;
	}

	public long getNewTotal() {
		return newTotal;
	}

	public long getNewDone() {
		return newDone;
	}	
}

package org.rebioma.server.upload;

import javax.swing.event.EventListenerList;

public class Traitement {
	private String traitement;
	private long total;
	private long done;
	private boolean cancel;
	private final EventListenerList listeners = new EventListenerList();
	public Traitement(String traitement,long total,long done){
		this.traitement = traitement;
		this.total = total;
		this.done = done;
		this.cancel = false;
	}
	
	public boolean getCancel(){
		return this.cancel;
	}
	
	public String getTraitement(){
		return traitement;
	}
	
	public void setTraitement(String traitement,long total,long done){
		String oldTraitement = this.traitement;
		long oldTotal = this.total;
		long oldDone = this.done;
		this.traitement = traitement;
		this.total = total;
		this.done = done;
		fireTraitementChanged(oldTraitement,traitement,oldTotal,total,oldDone,done);
	}
	
	public void addTraitementListener(TraitementListener listener){
		listeners.add(TraitementListener.class, listener);
	}
	
	public void removeListeners(TraitementListener listener){
		this.cancel = true;
		listeners.remove(TraitementListener.class, listener);
	}
	
	public void removeTraitementListener(TraitementListener listener){
		listeners.remove(TraitementListener.class, listener);		
	}
	
	public TraitementListener[] getTraitementListeners(){
		return listeners.getListeners(TraitementListener.class);
	}
	
	public TraitementListener getTraitementListener(){
		TraitementListener listener= null;		
		try{
			listener = listeners.getListeners(TraitementListener.class)[listeners.getListenerCount()-1];
		}catch(Exception e){}
		return listener;
	}
	
	protected void fireTraitementChanged(String oldTraitement,String newTraitement,long oldTotal,long total,long oldDone,long done) {
		TraitementEvent event = null;
		for(TraitementListener listener : getTraitementListeners()){
			if(event == null)
				event = new TraitementEvent(oldTraitement, newTraitement, oldTotal, total, oldDone, done);
			listener.valueChange(event);
		}
	}

	
}

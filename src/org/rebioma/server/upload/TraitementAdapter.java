package org.rebioma.server.upload;

public abstract class TraitementAdapter implements TraitementListener {
	
	TraitementEvent e;
	
	public void valueChange(TraitementEvent e) {this.e = e;}

	public String getTraitement() {
		try{
			return e.getNewTraitement();
		}catch(Exception e){
			return "...";
		}
	}

	public long getTotal() {
		try{
			return e.getNewTotal();
		}catch(Exception e){
			return 100;
		}
	}

	public long getDone() {
		try{
			return e.getNewDone();
		}catch(Exception e){
			return 0;
		}
	}

}

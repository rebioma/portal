package org.rebioma.server.upload;

import java.util.EventListener;

public interface TraitementListener extends EventListener{
	void valueChange(TraitementEvent e);	
	public String getTraitement();
	public long getTotal();
	public long getDone();
}

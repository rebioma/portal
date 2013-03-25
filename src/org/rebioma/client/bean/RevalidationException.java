package org.rebioma.client.bean;

public class RevalidationException extends Exception {

	private static final long serialVersionUID = 1779988796313065886L;
	
	private RevalidationResult result=new RevalidationResult();

	public RevalidationResult getResult() {
		return result;
	}

	public void setResult(RevalidationResult result) {
		this.result = result;
	}

}

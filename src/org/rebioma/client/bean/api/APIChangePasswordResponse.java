/**
 * 
 */
package org.rebioma.client.bean.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mikajy
 *
 */
@XmlRootElement(name="response")
@XmlAccessorType (XmlAccessType.FIELD)
public class APIChangePasswordResponse {
	private boolean success;
	private String message;
	private int changePassStatus;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getChangePassStatus() {
		return changePassStatus;
	}
	public void setChangePassStatus(int changePassStatus) {
		this.changePassStatus = changePassStatus;
	}
}

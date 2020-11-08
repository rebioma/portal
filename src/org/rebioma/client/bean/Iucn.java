package org.rebioma.client.bean;

@SuppressWarnings("serial")
public class Iucn  implements java.io.Serializable{

	private String name;
	private int id;

	public Iucn() {
		super();
	}

	public Iucn(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String toString(){ 
        return this.id + " " + this.name; 
    } 
}
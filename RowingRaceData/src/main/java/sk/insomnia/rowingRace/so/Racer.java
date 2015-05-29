package sk.insomnia.rowingRace.so;

import java.io.Serializable;

public class Racer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7384049549238140882L;
	
	private Long id;
	private String name;
	private String surname;
	private Integer yearOfBirth;
	
	
	public Racer(){}
	public Racer(String name, String surname){
		this.name = name;
		this.surname = surname;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	
	public Integer getYearOfBirth() {
		return yearOfBirth;
	}
	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	@Override
	public String toString(){
		return this.name+" "+this.surname;				
	}
	public String getFullName(){
		return this.name+" "+this.surname;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}

package org.pojava.persistence.examples;

import java.util.Date;

import org.pojava.datetime.DateTime;

public class PotpourriReadOnly {
	private String str = "hello";
	private int five = 5;
	private Date d = new Date(86400000);
	private DateTime dt = new DateTime(86400000);
	private Object bob = new Long(9876543210L);
	private PotpourriReadOnly confused=this;
	private int[] numbers={ 1, 2, 3 };

	public PotpourriReadOnly() {
		// Currently, this is a requirement.
	}

	public PotpourriReadOnly(String str, int five, Date d, DateTime dt, Object bob, PotpourriReadOnly confused) {
		this.str=str;
		this.five=five;
		this.d=d;
		this.dt=dt;
		this.bob=bob;
		this.confused=confused;
	}

	public String getStr() {
		return str;
	}

	public int getFive() {
		return five;
	}

	public Date getD() {
		return d;
	}

	public DateTime getDt() {
		return dt;
	}

	public Object getBob() {
		return bob;
	}
	
	public PotpourriReadOnly getConfused() {
		return confused;
	}

	public int[] getNumbers() {
		return numbers;
	}

}

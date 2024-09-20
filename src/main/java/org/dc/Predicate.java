package org.dc;

public class Predicate {
	String column1;
	String column2;
	String operator;
	public Predicate(String column1, String column2, String operator) {
		this.column1 = column1;
		this.column2 = column2;
		this.operator = operator;
	}
	
	@Override
	public String toString() {
		return "s." + this.column1 + " " + this.operator + " t." + this.column2;
	}
}

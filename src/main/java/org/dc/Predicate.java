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
}

package org.dc;

import java.util.ArrayList;
import java.util.List;

public class SingleConstraint {
	public List<String> homoEqualColumns = new ArrayList<>();
	public List<String> ineqColumns1 = new ArrayList<>();
	public List<String> ineqColumns2 = new ArrayList<>();
	public List<String> operators = new ArrayList<>();
	
	public SingleConstraint(List<Predicate> predicates) {
		 for (Predicate pred : predicates) {
			 if (pred.operator == "==" && pred.column1 == pred.column2) {
				 homoEqualColumns.add(pred.column1);
			 } else {
				 ineqColumns1.add(pred.column1);
				 ineqColumns2.add(pred.column2);
				 operators.add(pred.operator);
			 }
		 }
	}
}

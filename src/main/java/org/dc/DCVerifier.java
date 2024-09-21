package org.dc;

import java.util.ArrayList;

public class DCVerifier {
	ArrayList<Constraint> atomDCs = new ArrayList<Constraint>();
	InputTable input;
	
	public DCVerifier(Constraint DC, InputTable input) {
		this.atomDCs = DC.decompose();
		System.out.println("Constraints after decomposition:");
		for (Constraint atomDC : atomDCs) {
			System.out.println("- " + atomDC);
		}
		this.input = input;
	}
}

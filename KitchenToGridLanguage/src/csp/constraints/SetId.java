package csp.constraints;

import java.util.UUID;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class SetId extends TGGConstraintImpl {
		
	public void solve(Variable var_0){
    	String bindingStates = getBindingStates(var_0);
    	
    	switch(bindingStates){
    	case "F":
    		var_0.bindToValue(UUID.randomUUID().toString());
    		setSatisfied(true);
    		return;
    	case "B":
    		setSatisfied(true);
    		return;
    	default: 
    		throw new UnsupportedOperationException("This case in the constraint has not been implemented yet: " + bindingStates);
    	}
    	
  	}	  
}
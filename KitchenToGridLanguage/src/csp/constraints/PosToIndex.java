package csp.constraints;

import org.moflon.tgg.language.csp.Variable;
import org.moflon.tgg.language.csp.impl.TGGConstraintImpl;

public class PosToIndex extends TGGConstraintImpl {
	public void solve(Variable blockSize, Variable yPos, Variable yIndex){
    	String bindingStates = getBindingStates(blockSize, yPos, yIndex);
    	
    	double _blockSize = (double) blockSize.getValue();
    	
    	switch(bindingStates){
    	case "BBB":
    	{
    		int _yIndex = (int) yIndex.getValue(); 
    		double _yPos = (double) yPos.getValue();
    		setSatisfied(_yIndex == (int)(_yPos / _blockSize));
    		return;
    	}
    	case "BFB":
    	{
    		int _yIndex = (int) yIndex.getValue(); 
    		yPos.bindToValue(_blockSize * _yIndex);
    		setSatisfied(true);
    		return;
    	}
    	case "BBF":
    	{
    		double _yPos = (double) yPos.getValue();
    		yIndex.bindToValue((int)(_yPos / _blockSize));
    		setSatisfied(true);
    		return;
    	}
    	default: 
    		throw new UnsupportedOperationException("This case in the constraint has not been implemented yet: " + bindingStates);
    	}
  	}	
}
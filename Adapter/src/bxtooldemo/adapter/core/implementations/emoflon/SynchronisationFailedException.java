package bxtooldemo.adapter.core.implementations.emoflon;

import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

public class SynchronisationFailedException extends IllegalStateException {

	private Copier objectMapping;

	public SynchronisationFailedException(String string, Copier objectMapping) {
		this.setObjectMapping(objectMapping);
	}

	public Copier getObjectMapping() {
		return objectMapping;
	}

	public void setObjectMapping(Copier objectMapping) {
		this.objectMapping = objectMapping;
	}
	
	// TODO:  Add methods to update or access mappings
	
}

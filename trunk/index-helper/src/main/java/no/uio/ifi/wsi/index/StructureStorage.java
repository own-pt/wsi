package no.uio.ifi.wsi.index;

import lombok.Getter;
import no.uio.ifi.wsi.SemanticStructure;

public abstract class StructureStorage {

	@Getter
	private SemanticStructure structure;

	public abstract SemanticStructure getDocument(String id);

	public abstract int structureCount();

	public StructureStorage(SemanticStructure structure) {
		super();
		this.structure = structure;
	}
}

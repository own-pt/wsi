package no.uio.ifi.wsi.generator.structures;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.conll.ConllText;

public class CONLLStructureReader extends GenericStructureReader {

	public CONLLStructureReader(String filter, String svgDirectory_) {
		super(filter, svgDirectory_);
	}

	@Override
	public SemanticStructure instance() {
		return new ConllText();
	}

}

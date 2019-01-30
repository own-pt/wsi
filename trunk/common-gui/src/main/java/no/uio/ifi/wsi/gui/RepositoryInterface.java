package no.uio.ifi.wsi.gui;

import java.util.List;

import lombok.Data;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.gui.html.HTMLGenerator;
import no.uio.ifi.wsi.index.LuceneStructureStorage;
import no.uio.ifi.wsi.index.LuceneTermIndex;
import no.uio.ifi.wsi.index.TermIndex;
import no.uio.ifi.wsi.sparql.OntologySearcher;
import no.uio.ifi.wsi.sparql.jena.JenaSearcher;

@Data
public class RepositoryInterface {

	private final List<String> formats;
	private final TermIndex termIndex;
	private final LuceneStructureStorage documents;
	private final HTMLGenerator generator;
	private final OntologySearcher ontology;

	public RepositoryInterface(HTMLGenerator generator_, String path,
			SemanticStructure structure, List<String> formats_) {
		formats = formats_;
		generator = generator_;
		ontology = new JenaSearcher(path);
		documents = new LuceneStructureStorage(path, structure);
		termIndex = new LuceneTermIndex(path);
	}
}

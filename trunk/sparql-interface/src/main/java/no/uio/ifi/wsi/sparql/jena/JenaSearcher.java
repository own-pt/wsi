package no.uio.ifi.wsi.sparql.jena;

import java.io.File;
import java.util.List;

import no.uio.ifi.wsi.sparql.OntologySearcher;
import no.uio.ifi.wsi.sparql.SearchResults;

import com.google.common.collect.Lists;

public class JenaSearcher implements OntologySearcher {

	private final List<JENAConnect> connect;

	public JenaSearcher(String path) {
		System.out.println("INIT: " + path);
		connect = Lists.newArrayList();
		for (File f : new File(path + "tdb/").listFiles()) {
			connect.add(new JENAConnect(f.getAbsolutePath()));
		}
	}

	@Override
	public SearchResults searchResults(String sparql, int maxNumberOfResults) {
		return new JenaPagination(connect, sparql, maxNumberOfResults);
	}
}

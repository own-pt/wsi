package no.uio.ifi.wsi.sparql;

public interface OntologySearcher {

	public SearchResults searchResults(String sparql, int maxNumberOfResults);

}

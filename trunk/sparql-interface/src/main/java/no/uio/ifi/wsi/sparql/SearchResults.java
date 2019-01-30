package no.uio.ifi.wsi.sparql;

import java.util.List;

public interface SearchResults {

	public SearchInfo info();

	public List<Result> next(int results);

	public boolean hasNext();

	public boolean finnished();

}

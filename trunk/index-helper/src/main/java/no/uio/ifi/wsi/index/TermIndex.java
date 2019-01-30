package no.uio.ifi.wsi.index;

import java.util.List;

public interface TermIndex {

	public TermCount weight(String text, String type);

	public List<TermCount> searchTerm(String query, boolean regex,
			String field, int totalLimit);
}

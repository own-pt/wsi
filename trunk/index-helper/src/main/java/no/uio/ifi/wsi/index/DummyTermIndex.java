package no.uio.ifi.wsi.index;

import java.util.List;

import com.google.common.collect.Lists;

public class DummyTermIndex implements TermIndex {

	@Override
	public TermCount weight(String text, String type) {
		return new TermCount(1, text, type);
	}

	@Override
	public List<TermCount> searchTerm(String query, boolean regex,
			String field, int totalLimit) {
		List<TermCount> out = Lists.newArrayList();
		out.add(new TermCount(1, query, field));
		return out;
	}
}

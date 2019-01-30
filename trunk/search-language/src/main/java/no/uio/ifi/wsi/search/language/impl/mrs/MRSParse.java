package no.uio.ifi.wsi.search.language.impl.mrs;

import lombok.Getter;
import lombok.Setter;
import no.uio.ifi.wsi.search.language.NodeParse;

public class MRSParse extends NodeParse {
	@Getter
	@Setter
	private String carg;
	@Getter
	@Setter
	private String predicate;

	public String trimCarg(String text) {
		if (!text.endsWith(")") || text.endsWith("\\)"))
			return text;
		carg = text.substring(text.lastIndexOf("(") + 1, text.indexOf(")"));
		return text.substring(0, text.indexOf("("));
	}
}

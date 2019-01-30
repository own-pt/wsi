package no.uio.ifi.wsi.search.language.impl.generic;

import java.util.List;
import java.util.Map;

import no.uio.ifi.wsi.search.language.ExpressionParser;
import no.uio.ifi.wsi.search.language.Parse;

import com.google.common.collect.Maps;

public class GenericExpressionParser extends ExpressionParser {

	public GenericExpressionParser(List<GenericPattern> ps) {
		super();
		patterns = Maps.newHashMap();
		for (GenericPattern gp : ps)
			patterns.put(gp.getPatternChar(), gp);
	}

	private Map<Character, GenericPattern> patterns;

	@Override
	public Parse parseNode(String text) {
		return new GenericNodeParse(text, patterns);
	}

}

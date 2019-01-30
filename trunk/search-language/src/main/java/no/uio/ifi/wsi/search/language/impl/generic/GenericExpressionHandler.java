package no.uio.ifi.wsi.search.language.impl.generic;

import no.uio.ifi.wsi.search.language.ExpressionHandler;

public class GenericExpressionHandler extends ExpressionHandler {
	public GenericExpressionHandler(GenericConfig conf) {
		setGenerator(new GenericSparqlGenerator(conf));
		setParser(new GenericExpressionParser(conf.getPatterns()));
	}
}

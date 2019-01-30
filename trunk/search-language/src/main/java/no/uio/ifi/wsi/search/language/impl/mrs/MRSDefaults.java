package no.uio.ifi.wsi.search.language.impl.mrs;

import no.uio.ifi.wsi.search.language.ExpressionHandler;

public class MRSDefaults {

	public static ExpressionHandler hander() {
		return new ExpressionHandler(new MRSSparqlGenerator(),
				new MRSExpressionParser());
	}
}

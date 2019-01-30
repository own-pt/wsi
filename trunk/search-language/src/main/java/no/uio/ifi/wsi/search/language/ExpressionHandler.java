package no.uio.ifi.wsi.search.language;

import lombok.Getter;
import lombok.Setter;

public class ExpressionHandler {

	@Getter
	@Setter
	private SparqlGenerator generator;

	@Getter
	@Setter
	private ExpressionParser parser;

	public ExpressionHandler() {

	}

	public ExpressionHandler(SparqlGenerator generator, ExpressionParser parser) {
		super();
		this.generator = generator;
		this.parser = parser;
	}

	public String generateSparql(String text, String annotation) {
		Parse parse = parser.parse(text);
		return generator.toSparqlStructure(parse, annotation);
	}

}

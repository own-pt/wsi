package no.uio.ifi.wsi.gui;

import lombok.Value;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.search.language.ExpressionHandler;

@Value
public class FormatDescription {
	private String name;
	private ExpressionHandler expressionHandler;
	private SemanticStructure structure;
}

package no.uio.ifi.wsi.gui.html;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.sparql.Match;

public class GenericHTMLGenerator extends HTMLGenerator {
	public String anotate(String docID, String strString, Match match,
			String format) {

		if (format.equals("eds") || format.equals("mrs"))
			return annotate(docID, strString, match);

		if (strString.contains("svg")) {
			strString = strString.replace("<g id=\"node", "<g id=\"" + docID
					+ "node");
			for (Integer id : match.getIds()) {
				strString = strString.replace("<g id=\"" + docID + "node" + id
						+ "\"", "<g id=\"" + docID + "node" + id
						+ "\" fill=\"red\"");
			}
			return strString;
		}
		return annotate(docID, strString, match);
	}

	public String annotate(String idx, String strString, Match ss) {
		for (Integer id : ss.getIds())
			strString = strString.replace("\"" + idx + "-" + id + "\"", "\""
					+ idx + "-" + id + "\"" + " style=\"color:red;\"");
		return strString;
	}

	@Override
	public String toHTML(SemanticStructure structure) {
		return StructureToHTML.toHTML(structure);
	}
}

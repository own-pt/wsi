package no.uio.ifi.wsi.search.language.impl.mrs;

import no.uio.ifi.wsi.search.language.ExpressionParser;
import no.uio.ifi.wsi.search.language.OperatorParse;
import no.uio.ifi.wsi.search.language.Parse;

public class MRSExpressionParser extends ExpressionParser {

	@Override
	public Parse parse(String text) {

		text = text.trim();

		String qeq = null;
		if (text.endsWith("}")) {
			qeq = text.substring(text.lastIndexOf("{") + 1,
					text.lastIndexOf("}")).trim();
			text = text.substring(0, text.lastIndexOf("{")).trim();
		}
		Parse p = super.parse(text);

		if (qeq != null) {
			qeq = qeq.replace("  ", " ").replace("  ", " ").replace("  ", " ")
					.replace("  ", " ");
			qeq = qeq.replace(" ", "");
			String[] qs = qeq.split(",");
			QEQParse q = new QEQParse();
			for (String qi : qs) {
				int pos = qi.indexOf("=q");
				if (pos == -1 || pos == 0 || pos == qi.length() - 2)
					throw new RuntimeException("Invalid constrain expression "
							+ qeq);
				String s1 = qi.substring(0, pos);
				String s2 = qi.substring(pos + 2);
				q.getEqs().add(new String[] { s1, s2 });
			}
			OperatorParse px = new OperatorParse(ExpressionParser.ANDOP);
			px.setLeft(p);
			px.setRight(q);
			p = px;
		}

		return p;
	}

	@Override
	public Parse parseNode(String text) {
		MRSParse out = new MRSParse();
		text = out.defaultInit(text);

		text = out.trimCarg(text);

		if (text.length() > 0)
			out.setPredicate(text);
		return out;
	}

}

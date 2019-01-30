package no.uio.ifi.wsi.search.language;

import lombok.Setter;

import org.apache.log4j.Logger;

public abstract class ExpressionParser {

	public static char ANDOP = '\14';
	public static char NOTOP = '\15';
	public static char OROP = '\11';
	public static final char quote = '\\';
	public static final char QUOTE_REPLACE = '\3';

	private static int findEnd(char open, char close, String text, int i) {

		int count = 1;
		char prev = text.charAt(i);
		while (count != 0) {
			i++;
			if (i == text.length()) {
				return -1;
			}
			char ch = text.charAt(i);
			if (ch == close && prev != quote) {
				count--;
			}

			if (ch == open && prev != quote) {
				count++;
			}
			prev = ch;
		}
		return i;
	}

	private static String format(String text) {
		text = text.replace("\t", " ");
		text = text.replace("\n", " ");
		text = text.replace("\r", " ");

		String old = text;
		text = text.replace("  ", " ");
		while (old.length() > text.length()) {
			old = text;
			text = text.replace("  ", " ");
		}

		text = text.replace("| ", "|");
		text = text.replace("( ", "(");
		text = text.replace(" )", ")");
		text = text.replace(" |", "|");
		text = text.replace("& ", "&");
		text = text.replace(" &", "&");

		text = text.replace(", ", ",");
		text = text.replace(" ,", ",");

		text = text.replace("\\\\", "" + QUOTE_REPLACE);

		if (text.startsWith(" ")) {
			text = text.substring(1);
		}

		char prev = ' ';

		StringBuilder b = new StringBuilder();
		b.append(prev);

		boolean inBraks = false;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);

			if (prev == '\\') {
				b.append(ch);
				continue;
			}

			if (ch == '[') {
				inBraks = true;
				b.append(ch);
				continue;
			}

			if (ch == ']') {
				inBraks = false;
				b.append(ch);
				continue;
			}

			if (ch == ' ' && !inBraks) {
				b.append(ANDOP);
				continue;
			}

			if (ch == '&') {
				b.append(ANDOP);
				continue;
			}

			if (ch == '!') {
				b.append(NOTOP);
				continue;
			}

			if (ch == '|') {
				b.append(OROP);
				continue;
			}

			b.append(ch);

		}

		text = b.toString();

		text = text.replace(" " + OROP, "" + OROP);
		text = text.replace(OROP + " ", "" + OROP);
		text = text.replace(" " + ANDOP, "" + ANDOP);
		text = text.replace(ANDOP + " ", "" + ANDOP);
		text = text.replace(" " + NOTOP, "" + NOTOP);
		text = text.replace(NOTOP + " ", "" + NOTOP);

		if (text.charAt(0) == NOTOP) {
			return NOTOP + text.substring(1).trim();
		}

		return text.trim();
	}

	private static boolean okAnd(Parse rest) {
		return okNot(rest) || ((OperatorParse) rest).getOperator() == ANDOP;
	}

	private static boolean okNot(Parse rest) {
		return rest.isCompact() || rest.isLeaf()
				|| ((OperatorParse) rest).getOperator() == NOTOP;
	}

	private static Parse pushDown(Parse out) {
		if (out.isLeaf()) {
			OperatorParse p = new OperatorParse(NOTOP);
			p.setLeft(out);
			return p;
		}
		OperatorParse p = (OperatorParse) out;

		if (p.getOperator() == ANDOP) {
			p.setOperator(OROP);
		} else {
			if (p.getOperator() == OROP) {
				p.setOperator(ANDOP);
			} else {
				if (p.getOperator() == NOTOP) {
					return p.getLeft();
				} else {
					return p;
				}
			}
		}
		p.setLeft(pushDown(p.getLeft()));
		p.setRight(pushDown(p.getRight()));
		return p;
	}

	private static Parse pushNot(Parse out) {
		if (out.isLeaf()) {
			return out;
		}
		OperatorParse p = (OperatorParse) out;
		if (p.getOperator() != NOTOP) {
			p.setLeft(pushNot(p.getLeft()));
			p.setRight(pushNot(p.getRight()));
			return p;
		}

		if (p.getLeft().isLeaf()) {
			return p;
		}
		return pushDown(p.getLeft());

	}

	private static int searchForOpearator(String text) {
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == OROP || ch == ANDOP) {
				return i;
			}
		}
		return -1;
	}

	@Setter
	private Logger logger;

	public ExpressionParser() {
		logger = Logger.getRootLogger();
	}

	public Parse parse(String text) {
		text = format(text);
		logger.debug("Clean\t" + text);
		Parse out = recognize(text);
		logger.debug("Analysis\t" + out);
		out = pushNot(out);
		return out;
	}

	public abstract Parse parseNode(String text);

	private Parse recognize(String text) {
		char firstChar = text.charAt(0);

		if (firstChar == '(') {
			int end = findEnd('(', ')', text, 0);
			if (end == -1) {
				throw new RuntimeException("Bracket not closed in expression "
						+ text);
			}
			if (end == text.length() - 1) {
				Parse out = recognize(text.substring(1, text.length() - 1));
				out.setCompact(true);
				return out;
			}
			char oc = text.charAt(end + 1);

			Parse left = recognize(text.substring(1, end));
			left.setCompact(true);
			Parse right = recognize(text.substring(end + 2));
			OperatorParse out = new OperatorParse(oc);
			out.setLeft(left);
			out.setRight(right);
			return out;

		}
		if (firstChar == NOTOP) {
			Parse rest = recognize(text.substring(1));
			if (okNot(rest)) {
				OperatorParse out = new OperatorParse(NOTOP);
				out.setLeft(rest);
				return out;
			}
			Parse parent = rest;
			Parse child = ((OperatorParse) parent).getLeft();
			while (!okNot(child)) {
				parent = child;
				child = ((OperatorParse) parent).getLeft();
			}

			OperatorParse modif = new OperatorParse(NOTOP);
			modif.setLeft(child);
			((OperatorParse) parent).setLeft(modif);
			return rest;
		}

		int operator = searchForOpearator(text);

		if (operator == -1) {
			Parse out = parseNode(text);
			return out;
		}

		char oc = text.charAt(operator);
		Parse left = recognize(text.substring(0, operator));
		Parse rest = recognize(text.substring(operator + 1));

		if (oc == OROP || okAnd(rest)) {
			OperatorParse out = new OperatorParse(oc);
			out.setLeft(left);
			out.setRight(rest);
			return out;
		}
		Parse parent = rest;
		Parse child = ((OperatorParse) rest).getLeft();
		while (!okAnd(child)) {
			parent = child;
			child = ((OperatorParse) parent).getLeft();
		}
		OperatorParse modif = new OperatorParse(oc);
		modif.setRight(child);
		modif.setLeft(left);
		((OperatorParse) parent).setLeft(modif);
		return rest;
	}
}

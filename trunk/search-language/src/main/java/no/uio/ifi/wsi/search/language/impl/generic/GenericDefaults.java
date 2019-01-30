package no.uio.ifi.wsi.search.language.impl.generic;

import java.util.List;

import no.uio.ifi.wsi.search.language.ExpressionHandler;

import com.google.common.collect.Lists;

public class GenericDefaults {

	public static final char ID_CHAR = ':';

	public static final char NULL_CHAR = '\12';

	public static List<GenericPattern> eds() {
		List<GenericPattern> out = Lists.newArrayList();
		out.add(new GenericPattern('(', ')', "carg", false, 3));
		out.add(new GenericPattern('^', NULL_CHAR, "top", true, 4));
		return out;
	}

	public static List<GenericPattern> sdp() {
		List<GenericPattern> out = Lists.newArrayList();
		out.add(new GenericPattern('+', NULL_CHAR, "lemma", false, 2));
		out.add(new GenericPattern('/', NULL_CHAR, "pos", false, 4));
		out.add(new GenericPattern('^', NULL_CHAR, "top", true, 5));
		out.add(new GenericPattern('=', NULL_CHAR, "sense", true, 3));
		return out;
	}

	public static List<GenericPattern> conll() {
		List<GenericPattern> out = Lists.newArrayList();
		out.add(new GenericPattern('+', NULL_CHAR, "lemma", false, 2));
		out.add(new GenericPattern('/', NULL_CHAR, "pos", false, 3));
		out.add(new GenericPattern('~', NULL_CHAR, "top", true, 4));
		return out;
	}

	public static ExpressionHandler edsHandler() {

		GenericConfig conf = new GenericConfig(eds(), "eds", "eds", "role", 4,
				"predicate", 2);
		return new GenericExpressionHandler(conf);
	}

	public static ExpressionHandler sdpHandler(String format) {
		GenericConfig conf = new GenericConfig(sdp(), "sdp/" + format, format,
				"role", 6, "form", 1);
		return new GenericExpressionHandler(conf);
	}

	public static ExpressionHandler conllHandler() {
		GenericConfig conf = new GenericConfig(conll(), "conll", "conll",
				"edge", 5, "form", 1);
		return new GenericExpressionHandler(conf);
	}
}

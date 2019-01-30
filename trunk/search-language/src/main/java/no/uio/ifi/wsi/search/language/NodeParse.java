package no.uio.ifi.wsi.search.language;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.google.common.collect.Lists;

@Data
@EqualsAndHashCode(callSuper = false)
public class NodeParse extends Parse {

	private String id;

	private char operator;

	private List<RoleParse> roles;

	private boolean top = false;

	public NodeParse() {
		roles = Lists.newArrayList();
	}

	public String defaultInit(String text) {
		if (text.endsWith("]")) {
			text = trimRoles(text);
		}
		text = trimID(text);
		if (text.startsWith("^")) {
			this.setTop(true);
			text = text.substring(1);
		}
		return text;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public void parseRoles(String substring) {
		if (substring.length() == 0) {
			return;
		}
		int start = 0;
		int brak = 0;
		for (int i = 1; i < substring.length(); i++) {
			char ch = substring.charAt(i);
			if (ch == '{')
				brak++;
			if (ch == '}')
				brak--;
			if (brak < 0)
				throw new RuntimeException("Wrong brakets in " + substring);
			if (ch == ',' && brak == 0 && substring.charAt(i) != '\\') {
				String role = substring.substring(start, i);
				roles.add(new RoleParse(role));
				start = i + 1;
			}
		}
		if (brak != 0)
			throw new RuntimeException("Wrong brakets in " + substring);
		String role = substring.substring(start);
		roles.add(new RoleParse(role));
	}

	public String trimID(String text) {
		if (text.length() == 0)
			return "";

		if (text.startsWith(":"))
			return text.substring(1);

		int i = 1;
		for (; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == ':' && text.charAt(i - 1) != '\\') {
				break;
			}
		}
		if (i == text.length()) {
			return text;
		}
		id = text.substring(0, i);
		text = text.substring(i + 1);
		if (id.contains("^")) {
			throw new RuntimeException("Symbol '^' is not valid for node id!");
		}
		return text;
	}

	public String trimRoles(String text) {
		int end = text.length() - 2;
		for (; end >= 0; end--) {
			if (text.charAt(end) == '['
					&& (end == 0 || (text.charAt(end - 1) != '\\'))) {
				break;
			}
		}
		if (end == -1) {
			throw new RuntimeException("Invalid expression " + text);
		}
		parseRoles(text.substring(end + 1, text.length() - 1).trim());
		return text.substring(0, end);
	}
}

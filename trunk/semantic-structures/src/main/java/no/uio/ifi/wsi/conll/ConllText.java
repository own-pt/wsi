package no.uio.ifi.wsi.conll;

import java.util.List;
import java.util.StringTokenizer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.uio.ifi.wsi.SemanticStructure;

@Data
@EqualsAndHashCode(callSuper = false)
public class ConllText extends SemanticStructure {

	private String id;
	private String text;
	private List<ConllLine> line;

	@Override
	public int getNodesNumber() {
		return line.size();
	}

	@Override
	public void load() {
		StringBuilder b = new StringBuilder();
		StringTokenizer toker = new StringTokenizer(getStructure(), "\n", false);
		while (toker.hasMoreTokens()) {
			String token = toker.nextToken().trim();
			String[] s = token.split("\t");
			ConllLine l = new ConllLine(s[0], s[1], s[2], s[4], s[3], s[5],
					s[6], s[7]);
			b.append(s[1] + " ");
			line.add(l);
		}
		setText(b.toString().trim());
	}

	@Override
	public void load(List<String> lines) {
		StringBuilder b = new StringBuilder();
		for (String token : lines) {
			token = token.trim();
			String[] s = token.split("\t");
			ConllLine l = new ConllLine(s[0], s[1], s[2], s[4], s[3], s[5],
					s[6], s[7]);
			b.append(s[1] + " ");
			line.add(l);
		}
		setText(b.toString().trim());
	}
}

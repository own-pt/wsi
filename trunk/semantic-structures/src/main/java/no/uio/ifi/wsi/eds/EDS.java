package no.uio.ifi.wsi.eds;

import java.util.List;
import java.util.StringTokenizer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.uio.ifi.wsi.SemanticStructure;

import com.google.common.collect.Lists;

@Data
@EqualsAndHashCode(callSuper = false)
public class EDS extends SemanticStructure {
	private final List<EDSPredication> predications;
	private String top;

	public EDS() {
		predications = Lists.newArrayList();
	}

	@Override
	public int getNodesNumber() {
		return predications.size();
	}

	@Override
	public void load() {
		StringTokenizer toker = new StringTokenizer(getStructure(), "\n", false);
		List<String> temp = Lists.newArrayList();
		toker.nextToken();
		while (toker.hasMoreTokens()) {
			temp.add(toker.nextToken().trim());
		}
		EDSParser.readLines(temp, this);
	}

	@Override
	public void load(List<String> lines) {
		EDSParser.read(lines, this);
	}
}

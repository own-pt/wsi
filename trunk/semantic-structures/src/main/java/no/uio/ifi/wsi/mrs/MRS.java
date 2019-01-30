package no.uio.ifi.wsi.mrs;

import java.util.List;
import java.util.StringTokenizer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import no.uio.ifi.wsi.SemanticStructure;

import com.google.common.collect.Lists;

@Data
@EqualsAndHashCode(callSuper = false)
public class MRS extends SemanticStructure {

	private final List<Constraint> constraints;
	private Variable index;
	private final List<MRSPredication> predications;
	private Variable top;

	public MRS() {
		super();
		predications = Lists.newArrayList();
		constraints = Lists.newArrayList();
	}

	@Override
	public int getNodesNumber() {
		return predications.size();
	}

	@Override
	public void load() {
		StringTokenizer toker = new StringTokenizer(getStructure(), "\n", false);
		List<String> temp = Lists.newArrayList();
		while (toker.hasMoreTokens()) {
			String token = toker.nextToken().trim();
			if (token.length() == 0)
				continue;
			temp.add(token);
		}
		MRSParser.readLines(temp, this);
	}

	@Override
	public void load(List<String> lines) {
		MRSParser.read(lines, this);
	}
}

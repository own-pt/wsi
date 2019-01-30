package no.uio.ifi.wsi.search.language.impl.mrs;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import no.uio.ifi.wsi.search.language.NodeParse;

import com.google.common.collect.Lists;

public class QEQParse extends NodeParse {
	@Getter
	@Setter
	private List<String[]> eqs;

	public QEQParse() {
		eqs = Lists.newArrayList();
	}

}

package no.uio.ifi.wsi.search.language;

import lombok.Data;

@Data
public abstract class Parse {

	private boolean compact;

	public abstract boolean isLeaf();
}

package no.uio.ifi.wsi.mrs;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class MRSPredication {

	private String carg;

	private int end;

	private String id;

	private Variable lbl;

	private String predicate;

	private final List<MRSRole> roles;

	private int start;

	public MRSPredication() {
		start = -1;
		end = -1;
		roles = Lists.newArrayList();
	}

}

package no.uio.ifi.wsi.eds;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;

@Data
public class EDSPredication {

	private String carg;
	private int end;

	private String id;

	private String predicate;

	private final List<EDSRole> roles;

	private int start;

	private String type;

	public EDSPredication() {
		start = -1;
		end = -1;
		roles = Lists.newArrayList();
	}

}

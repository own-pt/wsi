package no.uio.ifi.wsi.sparql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.eds.EDS;
import no.uio.ifi.wsi.eds.EDSPredication;
import no.uio.ifi.wsi.mrs.MRS;
import no.uio.ifi.wsi.sdp.SDPGraph;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Data
public class Result {

	private String graph;
	private List<ResultInstance> instances;
	private Map<String, String> values;

	public Result() {
		values = Maps.newHashMap();
	}

	public Result(String graph_) {
		graph = graph_;
		instances = Lists.newArrayList();
	}

	public List<Match> getMatches() {
		List<Match> matches = Lists.newArrayList();

		for (ResultInstance ri : instances) {
			Match m = new Match();
			m.setMin(100000);
			for (String nodeid : ri.getValues()) {
				Integer value = null;
				try {
					value = Integer.parseInt(nodeid);
				} catch (Exception e) {
				}
				if (value == null)
					continue;
				if (value.intValue() < m.getMin()) {
					m.setMin(value);
				}
				m.getIds().add(value);
			}
			if (m.getIds().size() > 0)
				matches.add(m);
		}
		if (matches.size() > 0)
			Collections.sort(matches, matches.get(0));
		return matches;
	}

	public List<Match> getMatches(SemanticStructure structure) {
		List<Match> matches = Lists.newArrayList();

		Map<String, Integer> values = values(structure);

		Set<String> seen = Sets.newHashSet();

		for (ResultInstance ri : instances) {
			Match m = new Match();
			m.setMin(100000);
			for (String nodeid : ri.getValues()) {
				Integer value = values.get(nodeid);
				if (value == null)
					continue;
				if (value.intValue() < m.getMin()) {
					m.setMin(value);
				}
				m.getIds().add(value);
			}
			if (m.getIds().size() == 0)
				continue;
			Collections.sort(m.getIds());
			if (seen.contains(m.getIds().toString()))
				continue;
			seen.add(m.getIds().toString());
			matches.add(m);
		}
		if (matches.size() > 0)
			Collections.sort(matches, matches.get(0));
		else
			matches.add(new Match());
		return matches;
	}

	private Map<String, Integer> values(SemanticStructure structure) {
		Map<String, Integer> out = Maps.newHashMap();
		if (structure instanceof MRS) {
			for (int i = 0; i < structure.getNodesNumber(); i++)
				out.put("pdc" + (i + 1), i + 1);
		}
		if (structure instanceof EDS) {
			EDS s = (EDS) structure;
			int i = 1;
			for (EDSPredication p : s.getPredications()) {
				out.put(p.getId(), i);
				i++;
			}
		}
		if (structure instanceof SDPGraph) {
			SDPGraph s = (SDPGraph) structure;
			for (int i = 1; i < s.getNodesNumber(); i++) {
				out.put("" + i, i);
			}
		}
		return out;
	}
}

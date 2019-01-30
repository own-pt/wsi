/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.uio.ifi.wsi.sparql;

import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.google.common.collect.Lists;

/**
 * 
 * @author milen
 */
public class Match implements Comparator<Match> {

	@Getter
	private final List<Integer> ids;

	@Getter
	@Setter
	private int min;

	public Match() {
		ids = Lists.newArrayList();
	}

	@Override
	public int compare(Match o1, Match o2) {
		return Double.compare(o1.getMin(), o2.getMin());
	}

}

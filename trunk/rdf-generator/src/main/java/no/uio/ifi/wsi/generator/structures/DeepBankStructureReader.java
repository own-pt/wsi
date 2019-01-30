package no.uio.ifi.wsi.generator.structures;

import java.util.List;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.eds.EDS;
import no.uio.ifi.wsi.generator.LocalFile;
import no.uio.ifi.wsi.mrs.MRS;

import com.google.common.collect.Lists;

public class DeepBankStructureReader extends StructureReader {

	private boolean eds;
	private int filterUp = -1;

	public DeepBankStructureReader(boolean eds, String filter_) {
		super();
		this.eds = eds;
		if (filter_ != null)
			filterUp = Integer.parseInt(filter_);
	}

	@Override
	public List<SemanticStructure> read(List<String> lines, LocalFile file) {
		return eds ? readDeepBankFileEDS(lines) : readDeepBankFileMRS(lines);
	}

	private List<SemanticStructure> readDeepBankFileEDS(List<String> list) {
		List<SemanticStructure> out = Lists.newArrayList();
		List<String> currentList = Lists.newArrayList();
		for (String content : list) {
			if (content.contains("" + ((char) 4))) {
				EDS s = new EDS();
				try {
					s.load(currentList);
					if (!filter(s))
						out.add(s);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				currentList.clear();
			} else {
				currentList.add(content);
			}
		}
		if (currentList.size() > 0) {
			EDS s = new EDS();
			try {
				s.load(currentList);
				if (!filter(s))
					out.add(s);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
		return out;
	}

	private List<SemanticStructure> readDeepBankFileMRS(List<String> list) {
		List<SemanticStructure> out = Lists.newArrayList();
		List<String> currentList = Lists.newArrayList();
		for (String content : list) {
			if (content.contains("" + ((char) 4))) {
				MRS s = new MRS();
				try {
					s.load(currentList);
					if (!filter(s))
						out.add(s);
				} catch (Exception e) {
					e.printStackTrace();
					fail();
				}
				currentList.clear();
			} else {
				currentList.add(content);
			}
		}
		if (currentList.size() > 0) {
			MRS s = new MRS();
			try {
				s.load(currentList);
				if (!filter(s))
					out.add(s);
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}

		return out;
	}

	private boolean filter(SemanticStructure sx) {
		if (filterUp == -1)
			return false;
		try {
			return filterUp < Integer.parseInt(sx.getId());
		} catch (Exception e) {
		}
		return false;
	}

}

package no.uio.ifi.wsi.generator.rdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.jena.riot.RiotWriter;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.sparql.core.Quad;

public class RDFTempData {
	private int current = 0;
	private int pos = 1;

	private final List<Quad> quads;
	private int quadsPerFile;

	private final String rdfPath;

	private long total = 0;

	public RDFTempData(String rdfPath_) {
		rdfPath = rdfPath_;
		current = 0;
		quads = Lists.newArrayList();
		pos = 1;
		quadsPerFile = 50000000;
		total = 0;
	}

	public void addQuads(List<Quad> st) {
		quads.addAll(st);
		incrementCurrent(st.size());
		incrementTotal(st.size());
		if (current >= quadsPerFile)
			write();
	}

	public int getCurrent() {
		return current;
	}

	public int getPos() {
		return pos;
	}

	public List<Quad> getQuads() {
		return quads;
	}

	public long getTotal() {
		return total;
	}

	public void incrementCurrent(int value) {
		current += value;
	}

	public void incrementTotal(int value) {
		total += value;
	}

	public void reset() {

	}

	public void setQuadsPerFile(int quadsPerFile) {
		this.quadsPerFile = quadsPerFile;
	}

	public void write() {
		if (current > 0) {
			try {
				String file = rdfPath + "/" + pos + ".nq";
				System.out.println("Writing file " + file);
				RiotWriter.writeNQuads(new FileOutputStream(file),
						quads.iterator());
				System.out.println("Done");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		pos++;
		current = 0;
		quads.clear();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
	}
}

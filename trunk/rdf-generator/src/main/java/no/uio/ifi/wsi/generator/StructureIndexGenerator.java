package no.uio.ifi.wsi.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import no.uio.ifi.wsi.SemanticStructure;
import no.uio.ifi.wsi.generator.rdf.RDFTempData;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class StructureIndexGenerator {

	public static int filter = 22100001;

	public static void generateIndex(InputReader reader,
			CommandLineReader cmlReader) throws IOException {
		IndexWriter writer = createWriter(cmlReader.getIndexDirectory());
		RDFTempData td = new RDFTempData(cmlReader.getRdfDirectory());
		int indexed = 0;
		while (reader.hasNext()) {
			if (reader.isPrintQuads() && td.getCurrent() > 0) {
				System.out.println(td.getCurrent() + " quads");
				reader.setPrintQuads(false);
			}
			SemanticStructure semanticStructure = reader.next();
			Document doc = toDocument(semanticStructure);
			writer.addDocument(doc);
			td.addQuads(cmlReader.getRdfGenerator().convert(semanticStructure));
			indexed++;
		}
		td.write();
		writer.close(true);
		printInfo(indexed, cmlReader);
	}

	private static void printInfo(int indexed, CommandLineReader cmlReader) {
		System.out.println(indexed + " structures indexed\n");
		if (cmlReader.getStructureReader().getFailedToRead() > 0)
			System.out.println(cmlReader.getStructureReader().getFailedToRead()
					+ " structures failed to read\n");
		if (cmlReader.getStructureReader().getFailedFiles().size() > 0) {
			System.out.println("The following files could not be loaded:");
			for (String s : cmlReader.getStructureReader().getFailedFiles())
				System.out.println(s);
		}
	}

	public static String loadString(String filename, String encoding) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), encoding));
			String line = null;
			StringBuilder vud = new StringBuilder();

			while ((line = in.readLine()) != null) {
				vud.append(line + "\n");
			}
			in.close();
			return vud.toString();
		} catch (Exception e) {
			throw new RuntimeException("Could not load file " + filename);
		}
	}

	public static Document toDocument(SemanticStructure g) {
		Document doc = new Document();
		doc.add(new StringField("id", g.getId(), Store.YES));
		doc.add(new TextField("text", g.getText(), Store.YES));
		doc.add(new TextField("structure", g.getStructure(), Store.YES));
		if (g.getSvg() != null)
			doc.add(new TextField("svg", g.getSvg(), Store.YES));
		return doc;
	}

	public static IndexWriter createWriter(String savePath) throws IOException {
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_45,
				new WhitespaceAnalyzer(Version.LUCENE_45));
		conf.setOpenMode(OpenMode.CREATE);
		return new IndexWriter(FSDirectory.open(new File(savePath)), conf);
	}

}

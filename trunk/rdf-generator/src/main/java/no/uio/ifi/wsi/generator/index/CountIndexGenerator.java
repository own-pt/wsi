package no.uio.ifi.wsi.generator.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import no.uio.ifi.wsi.generator.StructureIndexGenerator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;

public class CountIndexGenerator {

	private WordCache cache;
	private String indexdir;

	public CountIndexGenerator(String indexDir_) {
		cache = new WordCache();
		indexdir = indexDir_;
	}

	public void writeCache() throws IOException {
		IndexWriter writer = StructureIndexGenerator.createWriter(indexdir);
		System.out.println("Indexing " + cache.size() + " categories");
		for (String type : cache.keySet()) {
			System.out.println("Indexing " + type);
			System.out.println(cache.get(type).keySet().size()
					+ " elements in this type");
			for (String key : cache.get(type).keySet()) {
				Document doc = new Document();
				doc.add(new StringField(type, key.toLowerCase(), Store.YES));
				doc.add(new StringField("value", key, Store.YES));
				doc.add(new LongField("count", cache.get(type).get(key),
						Store.YES));
				writer.addDocument(doc);
			}
		}
		writer.waitForMerges();
		writer.close(true);
	}

	public void index(String indexdir) throws Exception {
		System.out.println("Indexing directory " + indexdir);
		for (File f : new File(indexdir).listFiles()) {
			System.out.println(f.getAbsolutePath());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				String[] spl = line.split(" ");
				if (spl[1].startsWith("<http://www.w3.org/2000/01/rdf-schema"))
					continue;
				try {
					String key = spl[1].substring(1, spl[1].length() - 1);
					if (line.contains("^^<http://www.w3.org/2001/XMLSchema#")) {
						if (key.endsWith("start") || key.endsWith("end"))
							continue;
						String value = line.substring(line.indexOf("\"") + 1,
								line.lastIndexOf("\""));
						value = value.replace("\\\"", "\"");
						cache.increment(key, value);
					} else {
						String rel = key.substring(key.indexOf("#") + 1);
						key = key.substring(0, key.indexOf("#") + 1);
						cache.increment(key, rel);
					}
				} catch (Exception e) {
					System.out.println(line);

				}
			}
			in.close();
		}

	}
}

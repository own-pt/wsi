package no.uio.ifi.wsi.gui;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;

import no.uio.ifi.wsi.conll.ConllText;
import no.uio.ifi.wsi.eds.EDS;
import no.uio.ifi.wsi.gui.html.GenericHTMLGenerator;
import no.uio.ifi.wsi.gui.html.HTMLGenerator;
import no.uio.ifi.wsi.mrs.MRS;
import no.uio.ifi.wsi.sdp.SDPGraph;
import no.uio.ifi.wsi.search.language.impl.generic.GenericDefaults;
import no.uio.ifi.wsi.search.language.impl.mrs.MRSDefaults;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GenericSearchInterface extends SearchInterface {

	private static final long serialVersionUID = 1L;
	private List<String> formats;
	private Map<String, String> examples;

	@Override
	public void initInstance(ServletConfig config) {
		initDataPath(config);
		formats = Lists.newArrayList();
		for (String f : new File(getDataPath()).list())
			formats.add(f);

		System.out.println(formats);

		Collections.sort(formats(), new FormatSorter());

		examples = Maps.newHashMap();
		for (String format : formats) {
			String value = config.getInitParameter("example-" + format);
			if (value == null) {
				value = defaultValue(format);
			}
			examples.put(format, value);
		}
		super.initInstance(config);

	}

	private String defaultValue(String format) {
		if (format.equals("mrs")) {
			return "[ARG2 x, ARG3 h1]\nh2:*_v_*[ARG0 e, ARG1 x]\n{ h1 =q h2 }";
		}
		if (format.equals("eds")) {
			return "[ARG2 x, ARG* e]\ne:*_v_*[ARG1 x]";
		}
		if (format.equals("dm")) {
			return "[ARG2 x, ARG* e]\ne:/v*[ARG1 x]";
		}
		return "";
	}

	@Override
	public List<String> formats() {
		return formats;
	}

	@Override
	public List<FormatDescription> description() {

		List<FormatDescription> out = Lists.newArrayList();

		for (String format : formats()) {
			if (format.equals("mrs")) {
				FormatDescription fd = new FormatDescription("mrs",
						MRSDefaults.hander(), new MRS());
				fd.getExpressionHandler().getParser().setLogger(log());
				out.add(fd);
				continue;
			}
			if (format.equals("eds")) {
				FormatDescription fd = new FormatDescription("eds",
						GenericDefaults.edsHandler(), new EDS());
				fd.getExpressionHandler().getParser().setLogger(log());
				out.add(fd);
				continue;
			}
			if (format.equals("conll")) {
				FormatDescription fd = new FormatDescription("conll",
						GenericDefaults.conllHandler(), new ConllText());
				fd.getExpressionHandler().getParser().setLogger(log());
				out.add(fd);
				continue;
			}
			boolean hasSense = new File(getDataPath() + format + "/sense.txt")
					.exists();

			System.out.println("SENSE " + hasSense + " " + getDataPath()
					+ format + "/sense.txt");
			FormatDescription fd = new FormatDescription(format,
					GenericDefaults.sdpHandler(format), new SDPGraph(hasSense));
			fd.getExpressionHandler().getParser().setLogger(log());
			out.add(fd);
		}

		return out;
	}

	@Override
	public HTMLGenerator htmlGenerator() {
		return new GenericHTMLGenerator();
	}

	@Override
	public Map<String, String> examples() {
		return examples;
	}
}

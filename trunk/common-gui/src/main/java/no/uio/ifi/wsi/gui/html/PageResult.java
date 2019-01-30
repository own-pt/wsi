package no.uio.ifi.wsi.gui.html;

import lombok.Getter;
import no.uio.ifi.wsi.gui.html.HTMLPaginator;

public class PageResult {

    @Getter
    private String errorMessage;
    @Getter
    private HTMLPaginator paginator;
    @Getter
    private String sparql;

    public PageResult(HTMLPaginator paginator) {
        super();
        this.paginator = paginator;
    }

    public PageResult(String errorMessage, String sparql) {
        super();
        this.errorMessage = errorMessage;
        this.sparql = sparql;
    }

    public PageResult(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

}

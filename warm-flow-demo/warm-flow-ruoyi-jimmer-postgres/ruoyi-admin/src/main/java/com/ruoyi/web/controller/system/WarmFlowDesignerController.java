package com.ruoyi.web.controller.system;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the application-owned Warm-Flow designer shell.
 * <p>
 * Warm-Flow also contributes a packaged Vue designer under
 * {@code META-INF/resources/warm-flow-ui/}. This demo needs a tiny bootstrap in
 * {@code index.html} so the embedded designer reuses RuoYi's {@code Admin-Token}
 * cookie as Warm-Flow's {@code Authorization} header. An exact controller
 * mapping wins over the dependency resource handler for the HTML entrypoint,
 * while the dependency/static resource handlers continue serving JS, CSS and
 * images below {@code /warm-flow-ui/**}.
 */
@Controller
public class WarmFlowDesignerController
{
    private static final Resource DESIGNER_INDEX =
            new ClassPathResource("static/warm-flow-ui/index.html");

    @GetMapping(value = "/warm-flow-ui/index.html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> index()
    {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.TEXT_HTML)
                .body(DESIGNER_INDEX);
    }
}

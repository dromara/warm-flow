package com.ruoyi.framework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.ruoyi.framework.security.filter.SpaRouteRequestMatcher;

/**
 * Serves the packaged Vue admin shell for browser history-mode route refreshes.
 * <p>
 * The fallback is intentionally limited to browser HTML navigations. JSON/API
 * calls such as {@code /monitor/server} keep reaching their controllers because
 * axios sends a JSON-preferred {@code Accept} header rather
 * than a browser document-navigation {@code Accept: text/html,...} header.
 */
@Component
public class SpaHistoryFallbackFilter extends OncePerRequestFilter
{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        if (SpaRouteRequestMatcher.matches(request))
        {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Vary", "Accept");
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}

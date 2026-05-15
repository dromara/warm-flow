package com.ruoyi.framework.security.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

/**
 * Shared predicate for browser document navigations that should load the Vue
 * admin shell while JSON API calls with the same path keep normal auth rules.
 */
public final class SpaRouteRequestMatcher
{
    private static final Set<String> ROUTE_PREFIXES = new HashSet<>(Arrays.asList(
            "/index",
            "/user",
            "/system",
            "/monitor",
            "/tool",
            "/flow",
            "/form",
            "/done",
            "/test"
    ));

    private SpaRouteRequestMatcher()
    {
    }

    public static boolean matches(HttpServletRequest request)
    {
        if (!HttpMethod.GET.matches(request.getMethod()))
        {
            return false;
        }
        String path = request.getRequestURI();
        if (path == null || path.indexOf('.') >= 0 || path.startsWith("/warm-flow-ui/"))
        {
            return false;
        }
        if (isApiPath(path))
        {
            return false;
        }
        boolean matchedPrefix = ROUTE_PREFIXES.stream()
                .anyMatch(prefix -> path.equals(prefix) || path.startsWith(prefix + "/"));
        if (!matchedPrefix)
        {
            return false;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }

    private static boolean isApiPath(String path)
    {
        return path.matches("^/system/[^/]+/.+")
                || path.matches("^/monitor/[^/]+/.+")
                || path.matches("^/tool/[^/]+/.+")
                || path.matches("^/flow/[^/]+/.+")
                || path.matches("^/form/[^/]+/.+")
                || path.matches("^/test/[^/]+/.+")
                || path.matches("^/user/[^/]+/.+");
    }
}

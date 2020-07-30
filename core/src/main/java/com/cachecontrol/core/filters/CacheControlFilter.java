package com.cachecontrol.core.filters;

import com.day.cq.commons.jcr.JcrConstants;
import com.cachecontrol.config.CacheControlConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Filter that adds cache-control max-age response headers
 */
@Component(service = Filter.class, property = {"sling.filter.scope=REQUEST", "sling.filter.scope=FORWARD", "sling.filter.methods=GET",
        org.osgi.framework.Constants.SERVICE_RANKING + ":Integer=-1100", "sling.filter.extensions=html"})
public class CacheControlFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheControlFilter.class);
    private static final String NO_CACHE_MAX_AGE = "0";

    @Reference
    private CacheControlConfiguration config;

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;

        Resource resource = slingRequest.getResource();
        Resource childResource = resource.getChild(JcrConstants.JCR_CONTENT);

        if (childResource != null) {
            String template = childResource.getValueMap().get("cq:template", String.class);
            String maxAgePageTemplateGroup1 = config.getConfig().max_age_page_template_group_1();
            String maxAgePageTemplateGroup2 = config.getConfig().max_age_page_template_group_2();

            if (StringUtils.isEmpty(template) || StringUtils.isEmpty(maxAgePageTemplateGroup1)) {
                chain.doFilter(slingRequest, slingResponse);
                return;

            } else if (checkTemplate(template, config.getConfig().page_template_group_1())) {
                addCacheControlHeader(slingRequest, slingResponse, maxAgePageTemplateGroup1);

            } else if (checkTemplate(template, config.getConfig().page_template_group_2())) {
                addCacheControlHeader(slingRequest, slingResponse, maxAgePageTemplateGroup2);

            } else if (checkTemplate(template, config.getConfig().no_cache_page_templates())) {
                addCacheControlHeader(slingRequest, slingResponse, NO_CACHE_MAX_AGE);

            } else {
                LOGGER.warn("cq:template for the page did not match any templates in Cache Control Configuration");
            }
        }
        chain.doFilter(slingRequest, slingResponse);
    }

    private boolean checkTemplate(String pageTemplate, String[] templates) {
        for (String template : templates) {
            if (StringUtils.equals(pageTemplate, template)) {
                return true;
            }
        }
        return false;
    }

    private void addCacheControlHeader(SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse, String maxAge) {
        Enumeration<String> agentsEnum = slingRequest.getHeaders("Server-Agent");
        List<String> serverAgents = agentsEnum != null ? Collections.list(agentsEnum) : Collections.emptyList();

        if (serverAgents.contains("Communique-Dispatcher") && !slingResponse.containsHeader(HttpHeaders.CACHE_CONTROL)) {
            if (!StringUtils.equals(maxAge, "0")) {
                slingResponse.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=" + maxAge);
            } else {
                slingResponse.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=0, no-cache, no-store, must-revalidate");
            }
            LOGGER.debug("Adding header Cache-Control: max-age={} to response", maxAge);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Leave empty
    }

    @Override
    public void destroy() {
        // Leave empty
    }
}

package com.cachecontrol.config;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(service = CacheControlConfiguration.class, immediate = true)
@Designate(ocd = CacheControlConfiguration.Config.class)
public class CacheControlConfiguration {
    private CacheControlConfiguration.Config config;

    @ObjectClassDefinition(name = "Cache Control Configuration", description = "Cache Control Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Page Template Group 1", description = "Group 1 of page templates", type = AttributeType.STRING)
        String[] page_template_group_1();

        @AttributeDefinition(name = "Max Age for Template Group 1", description = "The length of time (in seconds) to cache pages created with templates part of group 1", type = AttributeType.LONG)
        String max_age_page_template_group_1();

        @AttributeDefinition(name = "Page Template Group 2", description = "Group 2 of page templates", type = AttributeType.STRING)
        String[] page_template_group_2();

        @AttributeDefinition(name = "Max Age for Template Group 2", description = "The length of time (in seconds) to cache pages created with templates part of group 2", type = AttributeType.LONG)
        String max_age_page_template_group_2();

        @AttributeDefinition(name = "No-Cache Page Templates", description = "The cq:template values of pages that should not be cached", type = AttributeType.STRING)
        String[] no_cache_page_templates();
    }

    @Activate
    @Modified
    protected void activate(CacheControlConfiguration.Config config) {
        this.config = config;
    }

    @Deactivate
    protected void deactivate(CacheControlConfiguration.Config config) {
        config = null;
    }

    public CacheControlConfiguration.Config getConfig() {
        return config;
    }
}

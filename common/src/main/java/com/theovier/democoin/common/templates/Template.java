package com.theovier.democoin.common.templates;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;

public abstract class Template implements FillableTemplate {

    protected VelocityEngine               ve      = new VelocityEngine();
    protected VelocityContext              context = new VelocityContext();
    protected org.apache.velocity.Template template;

    public Template() {
        init();
    }

    private void init() {
        initVelocityEngine();
        initVelocityTemplate();
    }

    private void initVelocityEngine() {
        this.ve.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        this.ve.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() ); //$NON-NLS-1$
        this.ve.setProperty( RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, "true" );
        this.ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute" );
        this.ve.init();
    }

    protected abstract void initVelocityTemplate();

    protected abstract void fillPlaceholder();

    protected String createFilledTemplate() {
        StringWriter writer = new StringWriter();
        this.template.merge( this.context, writer );
        return writer.toString();
    }
}

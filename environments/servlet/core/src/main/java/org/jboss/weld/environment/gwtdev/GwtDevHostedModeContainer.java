/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.weld.environment.gwtdev;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import org.jboss.weld.environment.Container;
import org.jboss.weld.environment.ContainerContext;
import org.jboss.weld.environment.jetty.AbstractJettyContainer;
import org.jboss.weld.environment.jetty.JettyWeldInjector;
import org.jboss.weld.environment.servlet.util.Reflections;
import org.jboss.weld.manager.api.WeldManager;

/**
 *
 */
public class GwtDevHostedModeContainer extends AbstractJettyContainer {
    public static Container INSTANCE = new GwtDevHostedModeContainer();

    // The gwt-dev jar is never in the project classpath (only in the maven/eclipse/intellij plugin classpath)
    // except when GWT is being run in hosted mode.
    private static final String GWT_DEV_HOSTED_MODE_REQUIRED_CLASS_NAME = "com.google.gwt.dev.HostedMode";

    protected String classToCheck() {
        return GWT_DEV_HOSTED_MODE_REQUIRED_CLASS_NAME;
    }

    protected Class<?> getWeldServletHandlerClass() {
        return MortbayWeldServletHandler.class;
    }

    public void initialize(ContainerContext context) {
        // Try pushing a Jetty Injector into the servlet context
        try {
            Class<?> clazz = Reflections.classForName(JettyWeldInjector.class.getName());
            Object injector = clazz.getConstructor(WeldManager.class).newInstance(context.getManager());
            context.getServletContext().setAttribute(INJECTOR_ATTRIBUTE_NAME, injector);
            log.info("GWTHostedMode detected, JSR-299 injection will be available in Servlets and Filters. Injection into Listeners is not supported.");

            Class<?> decoratorClass = getWeldServletHandlerClass();
            Method processMethod = decoratorClass.getMethod("process", ServletContext.class);
            processMethod.invoke(null, context.getServletContext());
        } catch (Exception e) {
            log.error("Unable to create JettyWeldInjector. CDI injection will not be available in Servlets, Filters or Listeners", e);
        }
    }
}

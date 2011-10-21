package org.jboss.weld.environment.servlet.test.examples;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import static org.jboss.weld.environment.servlet.test.util.JettyDeployments.JETTY_ENV;

@RunWith(Arquillian.class)
public class MockExampleTest extends MockExampleTestBase {

    @Deployment
    public static WebArchive deployment() {
        return MockExampleTestBase.deployment().addAsWebInfResource(JETTY_ENV, "jetty-env.xml");
    }

}

package br.com.caelum.vraptor.view.jsp;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.PathResolver;

public class JspViewTest {

    private Mockery mockery;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private ResourceMethod method;
    private Resource resource;
    private PathResolver fixedResolver;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        request = mockery.mock(HttpServletRequest.class);
        response = mockery.mock(HttpServletResponse.class);
        dispatcher = mockery.mock(RequestDispatcher.class);
        method = mockery.mock(ResourceMethod.class);
        resource = mockery.mock(Resource.class);
        fixedResolver = new PathResolver() {
            public String pathFor(ResourceMethod method, String result) {
                return "fixed";
            }
        };
    }

    @Test
    public void shouldUseDefaultPathResolverWhileForwarding() throws ServletException, IOException, NoSuchMethodException {
        JspView view = new JspView(request, response, method);
        mockery.checking(new Expectations() {
            {
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getRequestDispatcher("/DogController/bark.ok.jsp");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        view.forward("ok");
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseDefaultPathResolverWhileIncluding() throws ServletException, IOException, NoSuchMethodException {
        JspView view = new JspView(request, response, method);
        mockery.checking(new Expectations() {
            {
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getRequestDispatcher("/DogController/bark.notOk.jsp");
                will(returnValue(dispatcher));
                one(dispatcher).include(request, response);
            }
        });
        view.include("notOk");
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAllowCustomPathResolverWhileForwarding() throws ServletException, IOException {
        JspView view = new JspView(request, response, method, fixedResolver);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestDispatcher("fixed");
                will(returnValue(dispatcher));
                one(dispatcher).forward(request, response);
            }
        });
        view.forward("ok");
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAllowCustomPathResolverWhileIncluding() throws ServletException, IOException {
        JspView view = new JspView(request, response, method, fixedResolver);
        mockery.checking(new Expectations() {
            {
                one(request).getRequestDispatcher("fixed");
                will(returnValue(dispatcher));
                one(dispatcher).include(request, response);
            }
        });
        view.include("ok");
        mockery.assertIsSatisfied();
    }

}

package kazu.kazlet;

import kazu.kazlet.annotation.View;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public abstract class Servlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleReturn(request, response, this.get(request, response));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleReturn(request, response, this.post(request, response));
    }

    protected void handleReturn(HttpServletRequest request, HttpServletResponse response, Optional<Map<String, Object>> opt) throws ServletException, IOException {
        opt.ifPresent(map -> map.forEach(request::setAttribute));

        Optional<View> viewOpt = Optional.empty();

        try {
            viewOpt = Optional.ofNullable(this.getClass().getMethod("get", HttpServletRequest.class, HttpServletResponse.class).getAnnotation(View.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        viewOpt.filter(view -> !view.value().isEmpty());

        if (viewOpt.isPresent()) {
            View view = viewOpt.get();
            String viewPath = "/WEB-INF/views/" + view.value();

            if (this.getServletContext().getResourceAsStream(viewPath) != null) {
                this.getServletContext().getRequestDispatcher(viewPath).forward(request, response);
            } else {
                throw new IllegalArgumentException("The view " + viewPath + " could not be found");
            }

            return;
        }

        throw new IllegalStateException("No annotation");
    }

    abstract public Optional<Map<String, Object>> get(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException;
    abstract public Optional<Map<String, Object>> post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}

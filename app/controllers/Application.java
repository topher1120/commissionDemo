package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result javascriptRoutes(){
        response().setContentType("text/javascript");
        return ok(
                Routes.javascriptRouter("myJsRoutes",
                        routes.javascript.Commissioning.checkConfig(),
                        routes.javascript.Commissioning.performCommission(),
                        routes.javascript.Commissioning.findDiscoveredDevices())
        );
    }

}

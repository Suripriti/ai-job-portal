package jar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReactController {

    @RequestMapping(value = {
            "/",
            "/login",
            "/register",
            "/jobs",
            "/create-job",
            "/apply/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
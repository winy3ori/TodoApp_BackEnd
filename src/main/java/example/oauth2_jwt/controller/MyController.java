package example.oauth2_jwt.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class MyController {

    @GetMapping("/my")
    public String myAPI() {

        return "my route";
    }
}
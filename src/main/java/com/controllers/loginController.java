package com.controllers;

import com.beans.UserServices;
import com.repo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The type Login controller.
 */
@Controller
public class loginController {
    private final UserServices userServices;

    @Resource(name = "id")
    private UserData sessionScopeId;

    /**
     * Instantiates a new Login controller.
     *
     * @param userServices the user services
     */
    @Autowired
    public loginController(UserServices userServices) {
        this.userServices = userServices;
    }

    /**
     * Gets login page.
     *
     * @param model   the model
     * @param request the request
     * @return the login page
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getLoginPage(Model model, HttpServletRequest request) {

        if(request.getSession(false)!=null) {
            ModelAndView modelAndView = new ModelAndView("redirect:/chat");
            return modelAndView;
        }
        return new ModelAndView("login");
    }

    /**
     * Sets user.
     *
     * @param first_name the first name
     * @param last_name  the last name
     * @return the user
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView setUser(@RequestParam(name = "firstName") String first_name,
                                @RequestParam(name = "lastName") String last_name) {
        var user=userServices.findByFirstNameAndLastName(first_name, last_name);
        if ( user == null)
        {
            user = new User(first_name, last_name);
            long id = this.userServices.addUser(user, true);
            sessionScopeId.setId(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/chat");
            return modelAndView;

        }else if(user.getAliveState() == false)
        {
            long id= userServices.addUser(user,true);
            sessionScopeId.setId(id);
            ModelAndView modelAndView = new ModelAndView("redirect:/chat");
            return modelAndView;
        }
        else {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject("dupUser",
                    "The name is registered in the system. Please select a different name");
            return modelAndView;
        }
    }
}

package com.controllers;


import com.beans.MessageServices;
import com.beans.UserServices;
import com.repo.Message;
import com.repo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

/**
 * The type Chat controller.
 */
@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    private final UserServices userServices;
    private final MessageServices messageServices;
    @Resource(name = "id")
    UserData sessionScopeId;

    /**
     * Instantiates a new Chat controller.
     *
     * @param userServices    the user services
     * @param messageServices the message services
     */
    @Autowired
    public ChatController(UserServices userServices, MessageServices messageServices) {
        this.userServices = userServices;
        this.messageServices = messageServices;
    }

    /**
     * Chat page model and view.
     *
     * @param model the model
     * @return the model and view
     * @throws IOException the io exception
     */
    @GetMapping
    public ModelAndView chatPage(Model model) throws IOException {
        insert_name_user(model);
        return new ModelAndView("chatPage");
    }

    /**
     * New message list.
     *
     * @param message the message
     * @param request the request
     * @return the list
     */
    @RequestMapping(value = "/newMessage", method = RequestMethod.POST)
    @ResponseBody
    public List<MessagePair> new_message(@RequestBody String message, HttpServletRequest request) {
        long id = sessionScopeId.getId();
        Message new_message = new Message(message, id);
        messageServices.addMessage(new_message);
        return add_authors(messageServices.get5Message());
    }

    /**
     * Gets connected users.
     *
     * @return the connected users
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/getConnectedUsers", method = RequestMethod.GET)
    @ResponseBody
    public List<User> getConnectedUsers() throws IOException {

        return userServices.findAll();
    }

    /**
     * Gets all messages.
     *
     * @return the all messages
     * @throws IOException the io exception
     */
    @RequestMapping(value = "/getAllMessages", method = RequestMethod.GET)
    @ResponseBody
    public List<MessagePair> getAllMessages() throws IOException {
        userServices.UpdateUser(sessionScopeId.getId());
        return add_authors(messageServices.get5Message());
    }

    /**
     * Gets all messages.
     *
     * @param message the message
     * @return the all messages
     */
    @RequestMapping(value = "/searchByMessage", method = RequestMethod.POST)
    @ResponseBody
    public List<MessagePair> getAllMessages(@RequestBody String message) {
        return add_authors(messageServices.findAllByMessage(message));
    }

    /**
     * Search by user list.
     *
     * @param userName the user name
     * @return the list
     */
    @RequestMapping(value = "/searchByUser", method = RequestMethod.POST)
    @ResponseBody
    public List<List<MessagePair>> searchByUser(@RequestBody String userName) {

       List<List<MessagePair>>  result = new Vector<>();

        var list = userName.split(" ");
        if (list.length < 2)
        {
            List<User> a = userServices.findByFirstName(list[0]);
            for(var i :a)
                result.add( add_authors(messageServices.getUserMessages(i.getId())));
            return result;
        } else {
            List<User> b =userServices.findAllByFirstNameAndLastName(list[0], list[1]);
            for(var i :b)
                result.add( add_authors(messageServices.getUserMessages(i.getId())));
            return result;
        }
    }


    /**
     * Log out model and view.
     *
     * @param req the req
     * @return the model and view
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logOut(HttpServletRequest req) {
        var s=userServices.findById(sessionScopeId.getId()).get();
        s.setAliveState(false);
        userServices.addUser(s,false);
        req.getSession(false).invalidate();
        return new ModelAndView("redirect:" + "/");
    }

    private void insert_name_user(Model model) {
        Optional<User> s = this.userServices.findById(sessionScopeId.getId());
        model.addAttribute("f_name", s.get().getFirstName());
        model.addAttribute("l_name", s.get().getLastName());
        model.addAttribute("id_user",sessionScopeId.getId());
    }

    private List<MessagePair> add_authors(List<Message> s) {
        List<MessagePair> authorAndMessage = new Vector<MessagePair>();
        var fiveMessages = s;
        for (var message : fiveMessages) {
            var author = userServices.findById(message.getuserId());
            var ma = new MessagePair(message.getMessage(), author.get().toString(),message.getuserId());
            authorAndMessage.add(ma);
        }
        return authorAndMessage;
    }

}

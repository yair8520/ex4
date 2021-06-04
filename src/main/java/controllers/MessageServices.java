package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServices {
    private final MessageRepo messageRepo;

    @Autowired
    public MessageServices(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public void addMessage(Message message){messageRepo.save(message);}

    public List<Message> get5Message() {
        return messageRepo.findAllByOrderById();
    }

    //public List<Message> getUserMessages(long id) {return messageRepo.findAllByUser_id(id);}

    public Message getMessage(String message){return messageRepo.findByMessage(message);}
}

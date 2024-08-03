package org.example.whatsappchat;

import org.example.whatsappchat.server.Group;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class ChatController {

    private SimpMessagingTemplate messagingTemplate;
    private Map<String, Group> groups = new HashMap<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void sendMessage(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        String groupId = headerAccessor.getSessionAttributes().get("groupId").toString();
        messagingTemplate.convertAndSend("/topic/" + groupId, message);
    }

    @PostMapping("/create-group")
    public void createGroup(@RequestParam String groupName) {
        if (!groups.containsKey(groupName)) {
            groups.put(groupName, new Group(groupName));
            messagingTemplate.convertAndSend("/topic/groups", "Group " + groupName + " created");
        }
    }

    @PostMapping("/join-group")
    public void joinGroup(@RequestParam String groupName, SimpMessageHeaderAccessor headerAccessor) {
        if (groups.containsKey(groupName)) {
            headerAccessor.getSessionAttributes().put("groupId", groupName);
            messagingTemplate.convertAndSend("/topic/groups", "User joined group " + groupName);
        } else {
            messagingTemplate.convertAndSend("/topic/errors", "Group " + groupName + " not found");
        }
    }
}

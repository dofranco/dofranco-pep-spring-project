package com.example.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@RequestMapping
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    // Constructor-based dependency injection of services.
    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /**
     * Registers a new account.
     * 
     * @param account The account details provided in the request body.
     * @return A ResponseEntity containing the newly created Account and a 200 OK status.
     *         Returns 409 Conflict if the username already exists.
     *         Returns 400 Bad Request if validation fails.
     */
    @PostMapping(path = "register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        try {
            Account newAccount = accountService.registerAccount(account);
            return ResponseEntity.ok(newAccount);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Authenticates a user login.
     * 
     * @param account The account details (username and password) provided in the request body.
     * @return A ResponseEntity containing the authenticated Account and a 200 OK status.
     *         Returns 401 Unauthorized if the login fails.
     */
    @PostMapping(path = "login")
    public ResponseEntity<Account> accountLogin(@RequestBody Account account){
        try{
            Account loggedIn = accountService.findByUsernameAndPassword(account.getUsername(), account.getPassword());
            return ResponseEntity.ok(loggedIn);
        } catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Creates a new message.
     * 
     * @param message The message details provided in the request body.
     * @return A ResponseEntity containing the newly created Message and a 200 OK status.
     *         Returns 400 Bad Request if validation fails or if the account is not found.
     */
    @PostMapping(path = "messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message){
        try{
            accountService.findById(message.getPostedBy());
            Message savedMessage = messageService.addMessage(message);
            return ResponseEntity.ok(savedMessage);
        } catch(IllegalArgumentException | NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves all messages.
     * 
     * @return A ResponseEntity containing a list of all messages and a 200 OK status.
     */
    @GetMapping(path = "messages")
    public ResponseEntity<List<Message>> getMessages(){
        List<Message> allMessages = messageService.getAllMessages();

        return ResponseEntity.ok(allMessages);
    }

    /**
     * Retrieves a message by its ID.
     * 
     * @param messageId The ID of the message to retrieve.
     * @return A ResponseEntity containing the requested Message and a 200 OK status.
     *         Returns an empty body with a 200 OK status if the message is not found.
     */
    @GetMapping(path = "messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable("messageId") Integer messageId) {
        Optional<Message> messageOptional = messageService.getMessageById(messageId);
        
        if (messageOptional.isPresent()) {
            return ResponseEntity.ok(messageOptional.get());
        } else {
            // Returns a 200 OK with an empty body
            return ResponseEntity.ok().build();  
        }
    }

    /**
     * Deletes a message by its ID.
     * 
     * @param messageId The ID of the message to delete.
     * @return A ResponseEntity with a 200 OK status and a body containing 
     *         the number of deleted messages if the deletion was successful.
     *         Returns an empty body with a 200 OK status if the message is not found.
     */
    @DeleteMapping(path = "messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable("messageId") Integer messageId){
        Optional<Message> messageOptional = messageService.getMessageById(messageId);
        
        if (messageOptional.isPresent()) {
            messageService.deleteMessageById(messageId);
            return ResponseEntity.ok().body(1);
        } else {
            return ResponseEntity.ok().build();  
        }
    }

    /**
     * Updates the text of a message by its ID.
     * 
     * @param messageId The ID of the message to update.
     * @param newMessageText The new message text provided in the request body.
     * @return A ResponseEntity with a 200 OK status and a body containing the number of updated
     *         messages if the update was successful.
     *         Returns 400 Bad Request if validation fails or if the message is not found.
     */
    @PatchMapping(path = "messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable("messageId") Integer messageId, @RequestBody Message newMessageText){
        try{
            messageService.updateMessageTextById(messageId, newMessageText.getMessageText());
            return ResponseEntity.ok(1);
        }catch(IllegalArgumentException | NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Retrieves all messages posted by a specific user.
     * 
     * @param accountId The ID of the user whose messages to retrieve.
     * @return A ResponseEntity containing a list of messages posted by the specified user and a 200 OK status.
     *         Returns an empty list with a 200 OK status if no messages are found.
     */
    @GetMapping(path = "accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable("accountId") Integer accountId) {
        List<Message> messages = messageService.getAllMessagesById(accountId);
        
        return ResponseEntity.ok(messages);
    }
}

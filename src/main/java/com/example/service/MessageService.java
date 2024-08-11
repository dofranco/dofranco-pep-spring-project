package com.example.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    // Constructor-based dependency injection for the MessageRepository.
    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Adds a new message after validating it.
     *
     * @param message The message to be added.
     * @return The saved Message object.
     * @throws IllegalArgumentException if the message text is invalid (e.g., blank or too long).
     */
    public Message addMessage(Message message){
        // Validate the new message text before adding
        validateMessage(message);

        return messageRepository.save(message);
    }
    
    /**
     * Validates the message object to ensure it meets the required constraints.
     *
     * @param message The message object to validate.
     * @throws IllegalArgumentException if the message text is blank or exceeds 255 characters.
     */
    private void validateMessage(Message message) {
        if (message.getMessageText().length() == 0 || message.getMessageText().length() > 255 ) {
            throw new IllegalArgumentException("Message cannot be blank or more than 255 characters");
        }
    }

    /**
     * Validates the message text to ensure it meets the required constraints.
     *
     * @param messageText The message text to validate.
     * @throws IllegalArgumentException if the message text is null, blank, or exceeds 255 characters.
     */
    private void validateMessageText(String messageText) {
        if (messageText == null || messageText.length() == 0 || messageText.length() > 255) {
            throw new IllegalArgumentException("Message cannot be blank or more than 255 characters");
        }
    }

    /**
     * Retrieves all messages from the repository for one account.
     *
     * @return A list of all Message objects.
     */
    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    /**
     * Retrieves messages by their ID.
     *
     * @param messageId The ID of the messages to retrieve.
     * @return A list of messages matching the given ID.
     */
    public List<Message> getMessagesById(Integer messageId){
        return messageRepository.findAllById(List.of(messageId));
    }

    /**
     * Retrieves a single message by its ID, wrapped in an Optional.
     *
     * @param messageId The ID of the message to retrieve.
     * @return An Optional containing the message if found, or empty if not.
     */
    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * Deletes a message by its ID.
     *
     * @param messageId The ID of the message to delete.
     */
    public void deleteMessageById(Integer messageId){
        messageRepository.deleteById(messageId);
    }

    /**
     * Updates the text of a message identified by its ID after validating the new text.
     *
     * @param messageId The ID of the message to update.
     * @param messageText The new text to set for the message.
     * @throws IllegalArgumentException if the new message text is invalid.
     * @throws NoSuchElementException if the message with the specified ID is not found.
     */
    @Transactional
    public void updateMessageTextById(Integer messageId, String messageText){
        try{
            // Validate the new message text before updating
            validateMessageText(messageText);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Message cannot be blank or more than 255 characters");
        }

        // Ensure the message with the given ID exists before attempting to update it
        if(messageRepository.findById(messageId).isEmpty()){
            throw new NoSuchElementException("Message not found with ID: " + messageId);
        }

        // Perform the update
        messageRepository.updateMessageTextById(messageId, messageText);
    }

    /**
     * Retrieves all messages posted by a specific user, identified by their account ID.
     *
     * @param accountId The ID of the account whose messages to retrieve.
     * @return A list of messages posted by the specified account.
     */
    public List<Message> getAllMessagesById(Integer accountId){
        return messageRepository.getMessagesByAccountId(accountId);
    }
}

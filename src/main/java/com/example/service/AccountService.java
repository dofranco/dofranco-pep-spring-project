package com.example.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    // Constructor-based dependency injection for the AccountRepository
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Registers a new account after validating the account details.
     *
     * @param account The Account object containing the account details.
     * @return The saved Account object, including the generated account ID.
     * @throws DataIntegrityViolationException if the username already exists.
     */
    public Account registerAccount(Account account) {
        validateAccount(account);

        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists");
        }

        return accountRepository.save(account);
    }

    /**
     * Validates the account details to ensure they meet the required criteria.
     *
     * @param account The Account object to validate.
     * @throws IllegalArgumentException if the username is blank or the password is too short.
     */
    private void validateAccount(Account account) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
    }

    /**
     * Finds an account by username and password, used for authentication.
     *
     * @param username The username of the account.
     * @param password The password of the account.
     * @return The Account object if found.
     * @throws NoSuchElementException if no account is found with the provided credentials.
     */
    public Account findByUsernameAndPassword(String username, String password){
        return accountRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new NoSuchElementException());

    }

    /**
     * Finds an account by username.
     *
     * @param username The username of the account.
     * @return The Account object if found.
     * @throws NoSuchElementException if no account is found with the provided username.
     */
    public Account findByUsername(String username){
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException());
    }

    /**
     * Finds an account by its unique ID.
     *
     * @param accountId The ID of the account.
     * @return The Account object if found.
     * @throws NoSuchElementException if no account is found with the provided ID.
     */
    public Account findById(Integer accountId){
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException());
    }
}

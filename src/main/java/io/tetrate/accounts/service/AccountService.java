package io.tetrate.accounts.service;

import java.util.List;
import java.util.Optional;

import io.tetrate.accounts.domain.Account;
import io.tetrate.accounts.domain.AccountType;
import io.tetrate.accounts.exception.NoRecordsFoundException;
import io.tetrate.accounts.repository.AccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The service in the accounts microservice.
 * 
 * @author David Ferreira Pinto
 *
 */
@Service
public class AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	/**
	 * The accounts repository.
	 */
	@Autowired
	AccountRepository accounts;

	/**
	 * Retrieve an account with given id. The id here is the unique id value of
	 * the account managed by the repository (auto-increment).
	 * 
	 * @param id
	 *            The id of the account.
	 * @return The account object if found or throws a NoRecordsFoundException.
	 */
	public Account findAccount(Integer id) {

		logger.debug("AccountService.findAccount: id=" + id);

		Optional<Account> account = accounts.findById(id);
		if (!account.isPresent()) {
			logger.warn("AccountService.findAccount: could not find account with id: " + id);
			throw new NoRecordsFoundException();
		}

		logger.info(String.format("AccountService.findAccount - retrieved account with id: %s. Payload is: %s", id, account.get()));

		return account.get();
	}

	/**
	 * Retrieve a list of accounts for a given user. The id here is the unique user id
	 * value of the account, ie the username.
	 * @return The account object if found
	 */
	public List<Account> findAccounts() {

		List<Account> account = accounts.findByUserid();
		
		logger.debug("Found " + account.size() + " account(s).");
		
		logger.info(String.format("AccountService.findAccount - retrieved account for user  Payload is: %s", account));

		return account;
	}
	
	/**
	 * Retrieve a list of accounts for a given user. The id here is the unique user id
	 * value of the account, ie the username.
	 *
	 * @param type The type of the account to return.
	 * @return The account object if found
	 */
	public List<Account> findAccountsByType(AccountType type) {

		logger.debug("AccountService.findAccount: and type: " + type.toString());

		List<Account> account = accounts.findByUseridAndType(type);
		
		logger.debug("Found " + account.size() + " account(s).");
		
		logger.info(String.format("AccountService.findAccount - retrieved account with Payload is: %s",  account));

		return account;
	}

	/**
	 * Saves the given account in the repository.
	 * 
	 * @param accountRequest
	 *            The account to save.
	 * @return the id of the account.
	 */
	public Integer saveAccount(Account accountRequest) {

		logger.debug("AccountService.saveAccount:" + accountRequest.toString());
		// need to set some stuff that cannot be null!
		Account account = accounts.save(accountRequest);
		logger.info("AccountService.saveAccount: account saved: " + account);
		return account.getId();
	}
}

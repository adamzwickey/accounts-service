package io.tetrate.accounts.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import io.tetrate.accounts.domain.Account;
import io.tetrate.accounts.domain.AccountType;
import io.tetrate.accounts.domain.RegistrationRequest;
import io.tetrate.accounts.domain.Transaction;
import io.tetrate.accounts.domain.TransactionType;
import io.tetrate.accounts.domain.User;
import io.tetrate.accounts.service.AccountService;
import io.tetrate.accounts.service.KeyCloakService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * REST controller for the accounts microservice. Provides the following
 * endpoints:
 * <p>
 * <ul>
 * <li>GET <code>/accounts/{id}</code> retrieves the account with given id.
 * <li>POST <code>/accounts</code> stores the account object passed in body.
 * <li>POST <code>/accounts/transaction</code> receives a transaction to
 * process.
 * </ul>
 * <p>
 * 
 * @author Adam Zwickey
 *
 */
@RestController
public class AccountController {

	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

	/**
	 * The service to delegate calls to.
	 */
	@Autowired
	private AccountService service;
	@Autowired
	private KeyCloakService userService;
	/**
	 * REST call to retrieve the account with the given id.
	 * 
	 * @param id The id of the account to retrieve the account for.
	 * @return The account object if found.
	 */
	@GetMapping(value = "/accounts/{id}")
	public ResponseEntity<Account> find(@PathVariable("id") Integer id) {

		LOG.info("AccountController.find: id=" + id);

		Account accountResponse = this.service.findAccount(id);
		return new ResponseEntity<Account>(accountResponse,
				getNoCacheHeaders(), HttpStatus.OK);

	}

	@GetMapping(value = "/accounts")
	public ResponseEntity<List<Account>> findAccounts(
			@RequestParam(value = "type", required = false) final String type) {
		if (type == null) {
			List<Account> accountResponse = this.service.findAccounts();
			return new ResponseEntity<List<Account>>(accountResponse,
					getNoCacheHeaders(), HttpStatus.OK);
		} else {
			List<Account> accountResponse = this.service.findAccountsByType(
					AccountType.valueOf(type));
			return new ResponseEntity<List<Account>>(accountResponse,
					getNoCacheHeaders(), HttpStatus.OK);
		}
	}

	/**
	 * REST call to save the account provided in the request body.
	 * 
	 * @param accountRequest The account to save.
	 * @param builder
	 * @return
	 */
	@PostMapping(value = "/accounts")
	@ResponseStatus(HttpStatus.CREATED)
	public Boolean save(@Valid @RequestBody Account accountRequest, UriComponentsBuilder builder) {
		LOG.debug("Account Request: {}" + accountRequest);
		
		LOG.debug("AccountController.save: userId="	+ accountRequest.getUserid());
		Integer accountProfileId = this.service.saveAccount(accountRequest);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setLocation(builder.path("/account/{id}")
				.buildAndExpand(accountProfileId).toUri());
		return Boolean.TRUE;
	}

	/**
	 * REST call to process a transaction of an account.
	 * 
	 * @param transaction the transaction to process.
	 * @return a response entity with either SUCCESS or FAILED.
	 */
	@PostMapping(value = "/accounts/transaction")
	public ResponseEntity<String> transaction(
			@RequestBody Transaction transaction) {
				LOG.debug("AccountController.transaction: " + transaction.toString());
		if (transaction.getType().equals(TransactionType.DEBIT)) {
			LOG.debug("debit transaction");
			Account accountResponse = this.service.findAccount(transaction
					.getAccountId());

			BigDecimal currentBalance = accountResponse.getBalance();

			BigDecimal newBalance = currentBalance.subtract(transaction
					.getAmount());

			if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
				accountResponse.setBalance(newBalance);
				this.service.saveAccount(accountResponse);
				// TODO save transaction?
				LOG.debug("transaction processed.");
				return new ResponseEntity<String>("SUCCESS",
						getNoCacheHeaders(), HttpStatus.OK);

			} else {
				// no sufficient founds available
				return new ResponseEntity<String>("FAILED",
						getNoCacheHeaders(), HttpStatus.EXPECTATION_FAILED);
			}

		} else if (transaction.getType().equals(TransactionType.CREDIT)) {
			LOG.debug("credit transaction");
			Account accountResponse = this.service.findAccount(transaction.getAccountId());

			BigDecimal currentBalance = accountResponse.getBalance();

			LOG.debug("AccountController.transaction: current balance='"
					+ currentBalance + "'.");

			if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {

				BigDecimal newBalance = currentBalance.add(transaction.getAmount());
				LOG.debug("AccountController.increaseBalance: new balance='"
						+ newBalance + "'.");

				accountResponse.setBalance(newBalance);
				this.service.saveAccount(accountResponse);
				// TODO save transaction?
				return new ResponseEntity<String>("SUCCESS", getNoCacheHeaders(), HttpStatus.OK);

			} else {
				// amount can not be negative for increaseBalance, please use
				// decreaseBalance
				return new ResponseEntity<String>("FAILED", getNoCacheHeaders(),
						HttpStatus.EXPECTATION_FAILED);
			}

		}
		return null;
	}

	@PostMapping(value = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@Valid @RequestBody RegistrationRequest registrationRequest) {
        LOG.debug("Attempting to register user with email address=" + registrationRequest.getEmail());
        
		//create acccount in keycloak
		User u = userService.registerUser(registrationRequest);

		//create an initial account request
		Account a = new Account();
		a.setOpenbalance(new BigDecimal(100000));
		a.setBalance(new BigDecimal(100000));
		a.setCurrency("usd");
		a.setName(registrationRequest.getEmail());
		a.setUserid(registrationRequest.getEmail());
		a.setType(AccountType.CURRENT);
		a.setCreationdate(new Date());
		service.saveAccount(a);

		return u;
    }

	private HttpHeaders getNoCacheHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Cache-Control", "no-cache");
		return responseHeaders;
	}
}

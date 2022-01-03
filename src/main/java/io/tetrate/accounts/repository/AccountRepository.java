package io.tetrate.accounts.repository;

import io.tetrate.accounts.domain.Account;
import io.tetrate.accounts.domain.AccountType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account,Integer> {

	@Query("from Account where type = :type and userid = ?#{principal.claims['email']}")
	List<Account> findByUseridAndType(@Param("type") AccountType type);

	@Query("from Account where userid = ?#{principal.claims['email']}")
    List<Account> findByUserid();
}

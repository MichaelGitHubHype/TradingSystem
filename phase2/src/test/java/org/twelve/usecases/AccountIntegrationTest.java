package org.twelve.usecases;

import junit.framework.TestCase;
import org.twelve.entities.Account;
import org.twelve.entities.Item;
import org.twelve.entities.Permissions;
import org.twelve.gateways.AccountGateway;
import org.twelve.gateways.ram.InMemoryAccountGateway;
import org.twelve.gateways.ram.InMemoryGatewayPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountIntegrationTest extends TestCase {
    AccountGateway inMemoryAccountGateway;
    AccountRepository accountRepository;

    private void setUpAccountRepository() {
        List<Permissions> Perm = new ArrayList<>();

        Account initial = new Account("User1", "Potato", Perm, 0, "M");
        HashMap<Integer, Account> h = new HashMap<>();
        h.put(0, initial);

        inMemoryAccountGateway = new InMemoryAccountGateway(h);
        accountRepository = new AccountRepository(inMemoryAccountGateway);
    }
    
    public void testInitialization() {
        setUpAccountRepository();
        assertNotNull(accountRepository);
    }

    public void testSavingItems() {
        setUpAccountRepository();
        List<Permissions> Perm = new ArrayList<>();
        Account account = new Account("User2", "12345", Perm, 1, "UTM");
        Account account2 = new Account("User3", "12345", Perm, 2, "UTM");
        List<String> permsAsStrings = new ArrayList<>();
        for (Permissions perms: account.getPermissions()){
            permsAsStrings.add(perms.name());
        }
        inMemoryAccountGateway.save(account.getAccountID(), account.getUsername(), account.getPassword(),
                account.getWishlist(), permsAsStrings, account.getLocation(), true);
        inMemoryAccountGateway.populate(accountRepository);
        assertEquals(accountRepository.getAccountFromID(1).getUsername(), account.getUsername());
        assertNull(accountRepository.getAccountFromID(2));
    }
}

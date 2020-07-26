package org.twelve.gateways.experimental;

import org.twelve.usecases.AccountRepository;

import java.util.List;

public interface AccountGateway {

    void populate(AccountRepository accountRepository);

    // account_id,username,password,wishlist,permissions
    //
    // ask Lindsey if controllers/gateways can interact with enums(I think enums aren't entities)
    // todo: even if we could, i doubt gson would play nice (I'll check later)
    void save(int accountId, String username, String password, List<Integer> wishlist, List<String> permissions);
}

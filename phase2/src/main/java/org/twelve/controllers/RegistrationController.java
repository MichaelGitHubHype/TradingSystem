package org.twelve.controllers;

import org.twelve.entities.Permissions;
import org.twelve.entities.Roles;
import org.twelve.gateways.AccountGateway;
import org.twelve.gateways.CitiesGateway;
import org.twelve.gateways.GatewayPool;
import org.twelve.presenters.RegistrationPresenter;
import org.twelve.usecases.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for registering an account
 * @author Ethan (follow him on instagram @ethannomiddlenamelam)
 */
public class RegistrationController {

    /**
     * An instance of AccountRepository to deal with creating accounts
     */
    private final AccountRepository accountRepository;

    /**
     * An instance of SessionManager to deal with the current session
     */
    private final SessionManager sessionManager;

    private RegistrationPresenter registrationPresenter;

    private List<Roles> availableTypes;

    private final CityManager cityManager;

    private final InputHandler inputHandler;

    private final AccountGateway accountGateway;

    private final CitiesGateway citiesGateway;

    /**
     * Initializer for RegistrationController
     * @param useCasePool used to get all the use cases.
     */
    public RegistrationController(UseCasePool useCasePool, GatewayPool gatewayPool) {
        this.accountRepository = useCasePool.getAccountRepository();
        this.sessionManager = useCasePool.getSessionManager();
        this.cityManager = useCasePool.getCityManager();
        this.accountGateway = gatewayPool.getAccountGateway();
        this.citiesGateway = gatewayPool.getCitiesGateway();

        inputHandler = new InputHandler();
    }

    /**
     * A method for updating if user can create admins
     */
    public void updateOptions() {
        availableTypes = new ArrayList<>();

        if (sessionManager.getCurrAccountID() == -1) {

            availableTypes.add(Roles.DEMO);
            availableTypes.add(Roles.NORMAL);

        } else {

            availableTypes.add(Roles.ADMIN);

        }

        registrationPresenter.setAvailableTypes(availableTypes);
        registrationPresenter.setExistingCities(cityManager.getAllCities());

    }

    /**
     * A method to create an account
     * @param username the username of the account
     * @param password the password of the account
     * @param typeIndex index corresponding to account type of the account
     * @return true if the account has been successfully created.
     */
    public boolean createAccount(String username, String password, String location, int typeIndex) {
        List<Permissions> perms = null;
        switch (availableTypes.get(typeIndex)) {
            case ADMIN:
                perms = Arrays.asList(Permissions.LOGIN,
                        Permissions.FREEZE,
                        Permissions.UNFREEZE,
                        Permissions.CREATE_ITEM,
                        Permissions.CONFIRM_ITEM,
                        Permissions.ADD_TO_WISHLIST,
                        Permissions.TRADE,
                        Permissions.BROWSE_INVENTORY,
                        Permissions.CHANGE_THRESHOLDS,
                        Permissions.ADD_ADMIN,
                        Permissions.REQUEST_VACATION,
                        Permissions.CAN_BAN,
                        Permissions.MAKE_TRUSTED);
                break;
            case NORMAL:
                perms = Arrays.asList(Permissions.LOGIN,
                        Permissions.CREATE_ITEM,
                        Permissions.ADD_TO_WISHLIST,
                        Permissions.TRADE,
                        Permissions.BROWSE_INVENTORY,
                        Permissions.REQUEST_VACATION);
                break;
            case DEMO:
                perms = Arrays.asList(Permissions.LOGIN,
                        Permissions.ADD_TO_WISHLIST,
                        Permissions.BROWSE_INVENTORY);
                break;
        }

        if (!inputHandler.isValidUsername(username)) {
            registrationPresenter.setError("badUsername");
            return false;
        }

        accountGateway.populate(accountRepository);
        if (accountRepository.getIDFromUsername(username) != -1) {
            registrationPresenter.setError("usernameTaken");
            return false;
        }

        if (password.isBlank()) {
            registrationPresenter.setError("badPassword");
            return false;
        }

        citiesGateway.populate(cityManager);
        if (!cityManager.getAllCities().contains(location)) {
            if (inputHandler.isValidLocation(location)) {
                cityManager.createCity(location);
            } else {
                registrationPresenter.setError("badLocation");
                return false;
            }
        }

        accountRepository.createAccount(username, password, perms, location);
        if (sessionManager.getCurrAccountID() == -1) sessionManager.login(username);
        registrationPresenter.setError("");
        return true;
    }

    public void setRegistrationPresenter(RegistrationPresenter registrationPresenter) {
        this.registrationPresenter = registrationPresenter;
    }
}

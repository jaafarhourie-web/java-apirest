package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.model.Account;
import com.letocart.java_apirest_2026.model.Address;
import com.letocart.java_apirest_2026.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AddressValidationService addressValidationService;

    @Autowired
    public AccountService(AccountRepository accountRepository,
                          AddressValidationService addressValidationService) {
        this.accountRepository = accountRepository;
        this.addressValidationService = addressValidationService;
    }

    /**
     * Créer un nouveau compte avec validation d'adresse
     */
    public Account createAccount(Account account) throws Exception {
        // Vérifier si l'email existe déjà
        if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
            throw new Exception("Un compte avec cet email existe déjà");
        }

        // Valider l'adresse si elle est fournie
        if (account.getAddress() != null) {
            boolean isAddressValid = addressValidationService.validateAddress(account.getAddress());

            if (!isAddressValid) {
                throw new Exception("L'adresse fournie n'est pas valide ou n'existe pas");
            }
        }

        // Sauvegarder le compte (l'adresse sera automatiquement sauvegardée grâce à CascadeType.ALL)
        return accountRepository.save(account);
    }

    /**
     * Récupérer tous les comptes
     */
    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepository.findAll();
    }

    /**
     * Récupérer un compte par ID
     */
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    /**
     * Récupérer un compte par email
     */
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    /**
     * Mettre à jour un compte
     */
    public Account updateAccount(Long id, Account accountDetails) throws Exception {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new Exception("Compte non trouvé avec l'ID: " + id));

        account.setFirstName(accountDetails.getFirstName());
        account.setLastName(accountDetails.getLastName());
        account.setEmail(accountDetails.getEmail());

        // Valider la nouvelle adresse si elle est modifiée
        if (accountDetails.getAddress() != null) {
            boolean isAddressValid = addressValidationService.validateAddress(accountDetails.getAddress());

            if (!isAddressValid) {
                throw new Exception("La nouvelle adresse fournie n'est pas valide");
            }

            account.setAddress(accountDetails.getAddress());
        }

        return accountRepository.save(account);
    }

    /**
     * Supprimer un compte
     */
    public void deleteAccount(Long id) throws Exception {
        if (!accountRepository.existsById(id)) {
            throw new Exception("Compte non trouvé avec l'ID: " + id);
        }
        accountRepository.deleteById(id);
    }
}

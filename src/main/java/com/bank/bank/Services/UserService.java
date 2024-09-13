package com.bank.bank.Services;

import com.bank.bank.Entities.UserEntity;
import com.bank.bank.Repositories.UserRepository;
import com.bank.bank.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String saveUser(UserEntity userEntity) {
        if (userRepository.findByAfm(userEntity.getAfm()).isPresent()) {
            // Return message if the user with the same AFM already exists
            return "User with AFM " + userEntity.getAfm() + " already exists.";
        } else {
            // Save the new user and return a success message
            userRepository.save(userEntity);
            return "User with AFM " + userEntity.getAfm() + " has been successfully added.";
        }

    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public String deleteUser(String afm) {
        if (userRepository.findByAfm(afm).isPresent()) {
            userRepository.deleteByAfm(afm);

            return "User with AFM " + afm + " has been successfully deleted.";
        } else {
            throw new ResourceNotFoundException("User not found with AFM: " + afm);
            //return"User not found with AFM: " + afm;
        }
    }

    @Transactional
    public String createAccount(String afm) {
        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);
        if(user.get().getHas_account()){
            return"User with AFM " + afm + " already has an account";
        }
        user.get().setHas_account(true);
        userRepository.save(user.get());
        return "User with AFM " + afm + " created an account";
    }

    public String deleteAccount(String afm) {
        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);
        if(!user.get().getHas_account()){
            throw new ResourceNotFoundException("User with AFM " + afm + " already hasn't an account");
        }
        user.get().setHas_account(false);
        userRepository.save(user.get());
        return "User with AFM " + afm + " closed the account";
    }

    public String getLoan(String afm,BigDecimal amount) {

        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);

        if(!user.get().getHas_account()){
            return"User with AFM " + afm + " hasn't an account";
        }

        BigDecimal value;

        value = user.get().getLoanDebt().add(amount);


        user.get().setLoanDebt(value);
        userRepository.save(user.get());
        return "Loan of user with AFM: " + afm + " is " + value;
    }

    public String payLoan(String afm, BigDecimal amount) {

        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);

        if(!user.get().getHas_account()){
            return"User with AFM " + afm + " hasn't an account";
        }

        BigDecimal value=user.get().getLoanDebt();
        boolean isEqualInt = value.compareTo(BigDecimal.valueOf(0.0)) == 0;

        if(isEqualInt){
            return "The debt is 0.0";
        }else if (value.compareTo(amount) < 0){
            return "The debt is less than the amount you entered.";
        }else{
            user.get().setLoanDebt(value.subtract(amount));
            userRepository.save(user.get());
            return "The amount of your debt is " +user.get().getLoanDebt();
        }
    }

    public String deposit(String afm,BigDecimal amount) {
        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);

        if(!user.get().getHas_account()){
            return"User with AFM " + afm + " hasn't an account";
        }

        UserEntity user1 = user.get();
        user1.setMoneyDeposited(user1.getMoneyDeposited().add(amount));
        userRepository.save(user.get());
        return "Your amount of savings is: " + user1.getMoneyDeposited();
    }

    public void userNotFound(Optional user,String afm){
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with AFM: " + afm);
        }
    }

    public String withdraw(String afm, BigDecimal amount) {
        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);

        if(!user.get().getHas_account()){
            return"User with AFM " + afm + " hasn't an account";
        }

        UserEntity user1 = user.get();
        if(user1.getMoneyDeposited().compareTo(amount)<0){
            return "You don't have so much money to withdraw";
        }
        user1.setMoneyDeposited(user1.getMoneyDeposited().subtract(amount));
        userRepository.save(user.get());
        return "Your amount of savings is: " + user1.getMoneyDeposited();
    }

    public String getSavings(String afm) {
        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);

        if(!user.get().getHas_account()){
            return"User with AFM " + afm + " hasn't an account";
        }
        return "Your savings are: "+user.get().getMoneyDeposited();
    }

    public String getDebt(String afm) {
        Optional<UserEntity> user = userRepository.findByAfm(afm);
        userNotFound(user,afm);

        if(!user.get().getHas_account()){
            return"User with AFM " + afm + " hasn't an account";
        }
        return"Your debt is: "+user.get().getLoanDebt();
    }
}


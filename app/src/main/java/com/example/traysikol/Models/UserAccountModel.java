package com.example.traysikol.Models;

import android.accounts.Account;

import com.example.traysikol.Enums.AccountType;
import com.google.firebase.database.Exclude;

public class UserAccountModel {
    private String uid;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;
    private String profilePicture;
    private AccountType accountType;
    private String dateofBirth;
    private String address;
    private boolean IsApproved;

    public UserAccountModel(){}

    public UserAccountModel(String uid, String firstname, String lastname, String email, String phoneNumber, String username, String password, String profilePicture, AccountType accountType, String dateofBirth, String address) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
        this.accountType = accountType;
        this.dateofBirth = dateofBirth;
        this.address = address;
    }
    public UserAccountModel(boolean IsApproved, String uid, String firstname, String lastname, String email, String phoneNumber, String username, String password, String profilePicture, AccountType accountType, String dateofBirth, String address) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
        this.accountType = accountType;
        this.dateofBirth = dateofBirth;
        this.address = address;
        this.IsApproved = IsApproved;
    }
    public boolean getIsApproved() {
        return IsApproved;
    }

    public void setIsApproved(boolean approved) {
        this.IsApproved = approved;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture == null? "" : profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Exclude
    public String getFullName()
    {
        return firstname + " " + lastname;
    }
}

package com.econova.service;

import com.econova.entity.Address;
import com.econova.entity.User;

import java.util.List;

public interface AddressService {
    Address saveAddress(Address address);
    List<Address> getAddressesByUser(User user);
    Address getAddressById(Long id);
}

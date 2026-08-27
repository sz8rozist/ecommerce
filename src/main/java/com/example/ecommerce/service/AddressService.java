package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.AddressRepository;
import com.example.ecommerce.request.AddressRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> findAllForUser(User user) {
        return addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(user.getId());
    }

    @Transactional
    public Address create(User user, AddressRequest request) {
        if (request.isDefault()) {
            clearExistingDefault(user);
        } else if (findAllForUser(user).isEmpty()) {
            request.setDefault(true);
        }

        Address address = new Address();
        address.setUser(user);
        applyRequest(address, request);
        return addressRepository.save(address);
    }

    @Transactional
    public Address update(User user, Long id, AddressRequest request) {
        Address address = getOwned(user, id);
        if (request.isDefault() && !address.isDefault()) {
            clearExistingDefault(user);
        }
        applyRequest(address, request);
        return addressRepository.save(address);
    }

    @Transactional
    public void delete(User user, Long id) {
        Address address = getOwned(user, id);
        addressRepository.delete(address);

        if (address.isDefault()) {
            List<Address> remaining = findAllForUser(user);
            if (!remaining.isEmpty()) {
                Address newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    private Address getOwned(User user, Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("A cím nem található."));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnathorizedException("Ez a cím nem a bejelentkezett felhasználóhoz tartozik.");
        }
        return address;
    }

    private void clearExistingDefault(User user) {
        findAllForUser(user).stream()
                .filter(Address::isDefault)
                .forEach(existing -> {
                    existing.setDefault(false);
                    addressRepository.save(existing);
                });
    }

    private void applyRequest(Address address, AddressRequest request) {
        address.setLabel(request.getLabel());
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setDefault(request.isDefault());
    }
}

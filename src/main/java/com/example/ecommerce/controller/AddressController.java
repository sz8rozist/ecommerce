package com.example.ecommerce.controller;

import com.example.ecommerce.model.Address;
import com.example.ecommerce.model.User;
import com.example.ecommerce.request.AddressRequest;
import com.example.ecommerce.service.AddressService;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService) {
        this.addressService = addressService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Address>> findMyAddresses() {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(addressService.findAllForUser(user));
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@Valid @RequestBody AddressRequest request) {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(addressService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        addressService.delete(user, id);
    }
}

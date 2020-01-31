package rs.ac.uns.ftn.sep.bitcoin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.sep.bitcoin.model.Seller;
import rs.ac.uns.ftn.sep.bitcoin.service.SellerService;

import java.security.Principal;

@RestController
@RequestMapping("/api/credentials")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost", "https://dev.local"})
@RequiredArgsConstructor
public class CredentialsApiController {
    private final SellerService sellerService;

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Seller addCredentials(Principal principal, @RequestBody Seller seller) {
        seller.setEmail(principal.getName());
        return sellerService.create(seller);
    }

}

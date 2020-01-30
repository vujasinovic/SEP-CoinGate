package rs.ac.uns.ftn.sep.bitcoin.service;

import rs.ac.uns.ftn.sep.bitcoin.model.Seller;

public interface SellerService {
    Seller findByEmail(String email);

    Seller findByApiToken(String apiToken);

    Seller create(Seller seller);

}

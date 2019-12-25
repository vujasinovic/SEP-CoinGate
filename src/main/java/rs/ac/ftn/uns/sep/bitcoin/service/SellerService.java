package rs.ac.ftn.uns.sep.bitcoin.service;

import rs.ac.ftn.uns.sep.bitcoin.model.Seller;

public interface SellerService {
    Seller findByEmail(String email);

    Seller findByApiToken(String apiToken);
}

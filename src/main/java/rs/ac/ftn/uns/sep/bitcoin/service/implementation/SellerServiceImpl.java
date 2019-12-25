package rs.ac.ftn.uns.sep.bitcoin.service.implementation;

import org.springframework.stereotype.Service;
import rs.ac.ftn.uns.sep.bitcoin.model.Seller;
import rs.ac.ftn.uns.sep.bitcoin.repository.SellerRepository;
import rs.ac.ftn.uns.sep.bitcoin.service.SellerService;

@Service
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;

    public SellerServiceImpl(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Seller findByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }

    @Override
    public Seller findByApiToken(String apiToken) {
        return sellerRepository.findByApiToken(apiToken);
    }
}

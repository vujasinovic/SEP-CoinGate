package rs.ac.uns.ftn.sep.bitcoin.service.implementation;

import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.sep.bitcoin.model.Seller;
import rs.ac.uns.ftn.sep.bitcoin.repository.SellerRepository;
import rs.ac.uns.ftn.sep.bitcoin.service.SellerService;

import java.util.Objects;

import static java.util.Objects.nonNull;

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

    @Override
    public Seller create(Seller request) {
        Seller seller = sellerRepository.findByEmail(request.getEmail());

        if (nonNull(seller)) {
            seller.setApiToken(request.getApiToken());
        } else {
            seller = request;
        }

        return sellerRepository.save(seller);
    }

}

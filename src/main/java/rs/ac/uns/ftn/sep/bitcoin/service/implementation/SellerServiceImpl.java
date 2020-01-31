package rs.ac.uns.ftn.sep.bitcoin.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.sep.bitcoin.model.Seller;
import rs.ac.uns.ftn.sep.bitcoin.repository.SellerRepository;
import rs.ac.uns.ftn.sep.bitcoin.service.SellerService;
import rs.ac.uns.ftn.sep.commons.client.SellerClient;
import rs.ac.uns.ftn.sep.commons.dto.seller.CreatePaymentMethodDto;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final SellerClient sellerClient;

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

        CreatePaymentMethodDto createPaymentMethodDto = CreatePaymentMethodDto.builder()
                .email(seller.getEmail())
                .externalId(seller.getId())
                .build();
        sellerClient.createPaymentMethod(createPaymentMethodDto);

        return sellerRepository.save(seller);
    }

}

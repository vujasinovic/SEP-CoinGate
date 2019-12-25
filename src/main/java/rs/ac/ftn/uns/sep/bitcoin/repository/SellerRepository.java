package rs.ac.ftn.uns.sep.bitcoin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.ftn.uns.sep.bitcoin.model.Seller;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);

    Seller findByApiToken(String apiToken);
}

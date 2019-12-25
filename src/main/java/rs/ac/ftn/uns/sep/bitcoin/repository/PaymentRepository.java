package rs.ac.ftn.uns.sep.bitcoin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.uns.sep.bitcoin.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(String orderId);
}

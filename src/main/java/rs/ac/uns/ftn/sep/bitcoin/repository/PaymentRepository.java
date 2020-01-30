package rs.ac.uns.ftn.sep.bitcoin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.sep.bitcoin.model.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(String orderId);

    @Query("SELECT p FROM Payment p WHERE p.status NOT LIKE 'paid' AND p.createdOn > SYSDATE - 14400")
    List<Payment> findAllByStatusNotPaid();
}

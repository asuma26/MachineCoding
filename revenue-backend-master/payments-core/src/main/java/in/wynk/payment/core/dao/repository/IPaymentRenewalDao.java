package in.wynk.payment.core.dao.repository;

import in.wynk.payment.core.constant.BeanConstant;
import in.wynk.payment.core.dao.entity.PaymentRenewal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

@Repository(BeanConstant.PAYMENT_RENEWAL_DAO)
public interface IPaymentRenewalDao extends JpaRepository<PaymentRenewal, String> {

    @Query("SELECT p FROM PaymentRenewal p WHERE p.day BETWEEN :currentDay AND :currentDayWithOffset AND p.hour BETWEEN :currentTime AND :currentTimeWithOffset")
    Stream<PaymentRenewal> getRecurrentPayment(@Param("currentDay") Calendar currentDay,
                                               @Param("currentDayWithOffset") Calendar currentDayWithOffset,
                                               @Param("currentTime") Date currentTime,
                                               @Param("currentTimeWithOffset") Date currentTimeWithOffset);

}

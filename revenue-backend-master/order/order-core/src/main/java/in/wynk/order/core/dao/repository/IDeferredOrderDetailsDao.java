package in.wynk.order.core.dao.repository;

import in.wynk.order.core.dao.entity.DeferredOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface IDeferredOrderDetailsDao extends JpaRepository<DeferredOrderDetail, String> {

    Optional<DeferredOrderDetail> findById(@Param("orderId") String id);


    @Query("SELECT d FROM DeferredOrderDetail d WHERE d.untilDate BETWEEN :currentDay AND :currentDayWithOffset")
    Stream<DeferredOrderDetail> getScheduledDeferredOrdersPaginated(@Param("currentDay") Calendar currentDay,
                                                                    @Param("currentDayWithOffset") Calendar currentDayWithOffset);

}

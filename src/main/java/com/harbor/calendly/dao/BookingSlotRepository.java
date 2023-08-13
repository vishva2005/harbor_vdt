package com.harbor.calendly.dao;

import com.harbor.calendly.entities.BookingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingSlotRepository extends JpaRepository<BookingSlot, Integer> {

    @Query("select count(*) from BookingSlot bs where bs.userId = :userId and not (bs.endDateTimeInEpoch <= :startEpoch or bs.startDateTimeInEpoch >= :endEpoch)")
    int getCountOfOverlappingBookingSlots(Integer userId, Long startEpoch, Long endEpoch);
}

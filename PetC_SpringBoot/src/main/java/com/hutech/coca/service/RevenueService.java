package com.hutech.coca.service;

import com.hutech.coca.common.BookingStatus;
import com.hutech.coca.model.Booking;
import com.hutech.coca.repository.IBookingRepository;
import com.hutech.coca.repository.IPaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RevenueService {

    private final IPaymentTransactionRepository paymentTransactionRepository;
    private final IBookingRepository bookingRepository;

    /**
     * Lấy tổng quan doanh thu: tổng kỳ này, tổng kỳ trước, % tăng trưởng
     */
    public Map<String, Object> getRevenueSummary(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.plusDays(1).atStartOfDay();

        long days = to.toEpochDay() - from.toEpochDay() + 1;
        LocalDateTime prevFromDt = from.minusDays(days).atStartOfDay();

        double currentRevenue = paymentTransactionRepository.sumRevenueByPeriod(fromDt, toDt);
        double prevRevenue    = paymentTransactionRepository.sumRevenueByPeriod(prevFromDt, fromDt);
        long cancelledBookings = bookingRepository.countByBookingStatusAndCreateAtGreaterThanEqualAndCreateAtLessThanAndIsDeletedFalse(
            BookingStatus.CANCELLED,
            fromDt,
            toDt
        );

        double growth = prevRevenue == 0 ? 0 : ((currentRevenue - prevRevenue) / prevRevenue) * 100;

        // Breakdown theo phương thức thanh toán
        List<Object[]> methodRows = paymentTransactionRepository.revenueGroupByPaymentMethod(fromDt, toDt);
        Map<String, Double> byMethod = new LinkedHashMap<>();
        for (Object[] row : methodRows) {
            byMethod.put(row[0].toString(), ((Number) row[1]).doubleValue());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRevenue", currentRevenue);
        result.put("prevPeriodRevenue", prevRevenue);
        result.put("growthPercent", Math.round(growth * 10.0) / 10.0);
        result.put("byPaymentMethod", byMethod);
        result.put("cancelledBookings", cancelledBookings);
        return result;
    }

    /**
     * Doanh thu theo ngày trong kỳ
     */
    public List<Map<String, Object>> getRevenueByDay(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.plusDays(1).atStartOfDay();

        List<Object[]> rows = paymentTransactionRepository.revenueGroupByDay(fromDt, toDt);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("date", row[0].toString());
            entry.put("revenue", ((Number) row[1]).doubleValue());
            result.add(entry);
        }
        return result;
    }

    /**
     * Doanh thu theo tháng trong kỳ
     */
    public List<Map<String, Object>> getRevenueByMonth(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.plusDays(1).atStartOfDay();

        List<Object[]> rows = paymentTransactionRepository.revenueGroupByMonth(fromDt, toDt);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", row[0].toString());
            entry.put("revenue", ((Number) row[1]).doubleValue());
            result.add(entry);
        }
        return result;
    }

    /**
     * Danh sách giao dịch chi tiết
     */
    public List<Map<String, Object>> getTransactionDetails(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.plusDays(1).atStartOfDay();

        return paymentTransactionRepository.findSuccessTransactions(fromDt, toDt)
                .stream()
                .map(tx -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("id", tx.getId());
                    entry.put("transactionRef", tx.getTransactionRef());
                    entry.put("bookingId", tx.getBooking().getId());
                    entry.put("bookingCode", tx.getBooking().getBookingCode());
                    entry.put("paymentMethod", tx.getPaymentMethod().name());
                    entry.put("amount", tx.getAmount());
                    entry.put("voucherDiscount", tx.getVoucherDiscount() != null ? tx.getVoucherDiscount() : 0);
                    double netRevenue = tx.getAmount() - (tx.getVoucherDiscount() != null ? tx.getVoucherDiscount() : 0);
                    entry.put("netRevenue", Math.max(0, netRevenue));
                    entry.put("completedAt", tx.getUpdatedAt() != null ? tx.getUpdatedAt().toString() : null);
                    return entry;
                })
                .toList();
    }

    /**
     * Danh sách booking đã hủy trong kỳ
     */
    public List<Map<String, Object>> getCancelledBookings(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt   = to.plusDays(1).atStartOfDay();

        return bookingRepository.findCancelledBookingsInPeriod(BookingStatus.CANCELLED, fromDt, toDt)
                .stream()
                .map(booking -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("id", booking.getId());
                    entry.put("bookingCode", booking.getBookingCode());
                    entry.put("customerName", booking.getUser() != null ? booking.getUser().getUsername() : "-");
                    entry.put("customerEmail", booking.getUser() != null ? booking.getUser().getEmail() : "-");
                    entry.put("totalPrice", booking.getTotalPrice() != null ? booking.getTotalPrice() : 0);
                    entry.put("scheduledAt", booking.getScheduledAt() != null ? booking.getScheduledAt().toString() : null);
                    entry.put("cancelledAt", booking.getCreateAt() != null ? booking.getCreateAt().toString() : null);
                    entry.put("status", booking.getBookingStatus().name());
                    return entry;
                })
                .toList();
    }
}

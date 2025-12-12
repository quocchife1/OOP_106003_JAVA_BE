package com.example.rental.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    
    @NotNull(message = "Vui lòng chọn phòng")
    private Long roomId;
    
    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    private LocalDateTime startDate;
    
    @NotNull(message = "Vui lòng chọn ngày kết thúc")
    private LocalDateTime endDate;
    
    @Size(max = 500, message = "Ghi chú không được quá 500 ký tự")
    private String notes;
}
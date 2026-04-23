package com.sport.managementsport.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface HorasOcupadasPorDia {
    LocalDate getDia();
    BigDecimal getHorasOcupadas();
}

package ru.yandex.practicum.tests.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierCreateResponse {
    private Boolean ok;
    private String message;
}

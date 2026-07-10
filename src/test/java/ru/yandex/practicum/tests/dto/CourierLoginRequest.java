package ru.yandex.practicum.tests.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierLoginRequest {
    private String login;
    private String password;
}

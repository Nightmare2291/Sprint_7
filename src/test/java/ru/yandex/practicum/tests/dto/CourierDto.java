package ru.yandex.practicum.tests.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourierDto {
    private String login;
    private String password;
    private String firstName;
}

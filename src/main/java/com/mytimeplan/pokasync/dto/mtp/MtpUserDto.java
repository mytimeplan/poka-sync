package com.mytimeplan.pokasync.dto.mtp;

import com.mytimeplan.pokasync.dto.poka.PokaUserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MtpUserDto {
    private Long id;
    private String email;

    public MtpUserDto(PokaUserResponseDto.User pokaUser) {
        this(pokaUser.getId(), pokaUser.getEmail());
    }
}
package com.mytimeplan.pokasync.dto.poka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PokaResultDto<T> {
    private int count;
    @JsonProperty("results")
    private List<T> result;
}

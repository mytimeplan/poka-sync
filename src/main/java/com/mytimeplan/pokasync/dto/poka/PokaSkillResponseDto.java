package com.mytimeplan.pokasync.dto.poka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class PokaSkillResponseDto extends PokaResultDto<PokaSkillResponseDto.Skill> {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Skill extends Generic {
        private Category category;
    }

    @Setter
    @NoArgsConstructor
    public static class Category extends Generic {

        protected static final Pattern UNIT_POSITION_PATTERN = Pattern.compile("^(0*[1-9][0-9]?)");

        public String getUnitPosition() {
            return Optional.ofNullable(getName())
                    .map(UNIT_POSITION_PATTERN::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> matcher.group(1).replaceFirst("^0+", ""))
                    .orElse(null);
        }
    }

    @Setter
    @NoArgsConstructor
    public static class Generic {
        @Getter
        private Long id;
        @JsonProperty("name")
        private DictionaryNames dictionaryNames;

        public String getName() {
            if (dictionaryNames == null) return null;
            if (dictionaryNames.getNameUS() != null) {
                return dictionaryNames.getNameUS();
            } else if (dictionaryNames.getNameIS() != null) {
                return dictionaryNames.getNameIS();
            } else if (dictionaryNames.getNamePL() != null) {
                return dictionaryNames.getNamePL();
            } else return null;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DictionaryNames {
        @JsonProperty("en-US")
        private String nameUS;
        @JsonProperty("is-IS")
        private String nameIS;
        @JsonProperty("pl-PL")
        private String namePL;
    }
}

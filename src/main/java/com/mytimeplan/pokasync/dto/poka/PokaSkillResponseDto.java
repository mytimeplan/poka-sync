package com.mytimeplan.pokasync.dto.poka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PokaSkillResponseDto {
    private int count;
    @JsonProperty("results")
    private List<Skill> skills;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Skill extends Generic {
        private Category category;
    }

    @Setter
    @NoArgsConstructor
    public static class Category extends Generic {

        public Set<Integer> getCategoryIds() {
            String categoryFullName = getName();
            if (categoryFullName == null || categoryFullName.isEmpty())
                return Set.of();
            String[] splitCategory = categoryFullName.split("-");
            return Arrays.stream(splitCategory)
                    .filter(split -> split.matches("\\d+"))
                    .map(Integer::parseInt)
                    .filter(categoryId -> categoryId > 0)
                    .collect(Collectors.toSet());
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

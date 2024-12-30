package com.mytimeplan.pokasync.dto.poka;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PokaSkillResponseDtoTest {

    private static final PokaSkillResponseDto.Category CATEGORY = new PokaSkillResponseDto.Category();

    @AfterEach
    public void afterEach() {
        CATEGORY.setDictionaryNames(null);
    }

    @Test
    public void testGetUnitPosition_WithLeadingZero() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS("01-00- SKA Skautsmiðja");
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertEquals("1", position, "Expected position to be '1' after removing leading zero.");
    }

    @Test
    public void testGetUnitPosition_WithoutLeadingZero() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS("12-34- SKA Skautsmiðja");
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertEquals("12", position, "Expected position to be '12' as no leading zero exists.");
    }
    @Test
    public void testGetUnitPosition_WithMultipleLeadingZeros() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS("000123-00- SKA Skautsmiðja");
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertEquals("12", position,
                "Expected position to be '12' after removing leading zeros, because we take only first 2 numbers different from 0.");
    }

    @Test
    public void testGetUnitPosition_NoMatch() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS("SKA Skautsmiðja");
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertNull(position, "Expected position to be null as the string does not match the pattern.");
    }

    @Test
    public void testGetUnitPosition_WithEmptyString() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS("");
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertNull(position, "Expected position to be null as the string is empty.");
    }

    @Test
    public void testGetUnitPosition_WithNull() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS(null);
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertNull(position, "Expected position to be null as the string is null.");
    }

    @Test
    public void testGetUnitPosition_OnlyZeros() {
        //GIVEN
        PokaSkillResponseDto.DictionaryNames dictionaryNames = new PokaSkillResponseDto.DictionaryNames();
        dictionaryNames.setNameIS("0000-00- SKA Skautsmiðja");
        CATEGORY.setDictionaryNames(dictionaryNames);
        //WHEN
        String position = CATEGORY.getUnitPosition();
        //THEN
        assertNull(position, "Expected position null.");
    }
}
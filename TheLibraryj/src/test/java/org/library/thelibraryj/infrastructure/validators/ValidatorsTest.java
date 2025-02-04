package org.library.thelibraryj.infrastructure.validators;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.library.thelibraryj.infrastructure.validators.batchSize.ValidBatchSize;
import org.library.thelibraryj.infrastructure.validators.passwordCharacters.ValidPasswordCharacters;
import org.library.thelibraryj.infrastructure.validators.titleCharacters.ValidTitleCharacters;
import org.library.thelibraryj.infrastructure.validators.usernameCharacters.ValidUsernameCharacters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ValidatorsTest {

    @Validated
    static class ToValidate {
        public void checkUsername(@ValidUsernameCharacters String username){}
        public void checkTitle(@ValidTitleCharacters String title){}
        public void checkPassword(@ValidPasswordCharacters String password){}
        public void checkPassword(@ValidPasswordCharacters char[] password){}
        public void checkBatchSize(@ValidBatchSize List<?> list){}
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {
        @Bean
        ToValidate getToValidateBean(){
            return new ToValidate();
        }

    }

    @Autowired
    private ToValidate toValidate;

    @Test
    public void testBatchSizeValidation() {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < 55; i++) list.add(i);
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkBatchSize(list));
        List<Integer> list2 = new ArrayList<>();
        for(int i = 0; i < 30; i++) list2.add(i);
        assertDoesNotThrow(() -> toValidate.checkBatchSize(list2));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkBatchSize(list));
    }

    @Test
    public void testUsernameValidation() {
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkUsername("usern@me"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkUsername("user name"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkUsername("---_---_---"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkUsername("Lucy's"));
        assertDoesNotThrow(() -> toValidate.checkUsername("USER"));
        assertDoesNotThrow(() -> toValidate.checkUsername("user23"));
        assertDoesNotThrow(() -> toValidate.checkUsername("user_name"));
        assertDoesNotThrow(() -> toValidate.checkUsername("user-name"));
    }

    @Test
    public void testTitleValidation() {
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkTitle("@!*()"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkTitle("title@"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkTitle("     "));
        assertDoesNotThrow(() -> toValidate.checkTitle("titleTITLE"));
        assertDoesNotThrow(() -> toValidate.checkTitle("title title"));
        assertDoesNotThrow(() -> toValidate.checkTitle("title 23"));
        assertDoesNotThrow(() -> toValidate.checkTitle("Book's title"));
    }

    @Test
    public void testPasswordValidation() {
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("password"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("PASSWORD"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("12341234"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("password@"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword(""));
        assertDoesNotThrow(() -> toValidate.checkPassword("pass Word123@"));
        assertDoesNotThrow(() -> toValidate.checkPassword("aPASSWORD23@"));
        assertDoesNotThrow(() -> toValidate.checkPassword("P@ssword9"));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword(("password").toCharArray()));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("PASSWORD".toCharArray()));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("12341234".toCharArray()));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("password@".toCharArray()));
        assertThrows(ConstraintViolationException.class, () -> toValidate.checkPassword("".toCharArray()));
        assertDoesNotThrow(() -> toValidate.checkPassword("pass Word123@".toCharArray()));
        assertDoesNotThrow(() -> toValidate.checkPassword("aPASSWORD23@".toCharArray()));
        assertDoesNotThrow(() -> toValidate.checkPassword("P@ssword9".toCharArray()));
    }


}

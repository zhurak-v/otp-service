package com.security.otp.adapters.http.controllers;

import com.security.otp.adapters.http.handlers.OtpHandler;
import com.security.otp.core.dtos.otp.CreateOtpReq;
import com.security.otp.core.dtos.otp.CreateOtpRes;
import com.security.otp.core.dtos.otp.DeleteOtpReq;
import com.security.otp.core.dtos.otp.VerifyOtpReq;
import com.security.otp.core.dtos.otp.VerifyOtpRes;
import com.security.otp.core.exceptions.conflict.ConflictException;
import com.security.otp.core.exceptions.expired.ExpiredOtpException;
import com.security.otp.core.exceptions.internal.InternalException;
import com.security.otp.core.exceptions.not_found.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OtpController.class)
class OtpControllerTest {

    private static final String VALID_CREATE_BODY = """
            {
              "id": "user-1",
              "lifetime": "PT5M",
              "attempts": 3,
              "generateCodeReq": {
                "length": 6,
                "characterTypes": ["DIGITS"]
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OtpHandler otpHandler;

    @Test
    void createReturns201WithPassword() throws Exception {
        when(this.otpHandler.createOtp(any(CreateOtpReq.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new CreateOtpRes("483920")));

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").value("483920"));
    }

    @Test
    void createPassesParsedRequestToHandler() throws Exception {
        when(this.otpHandler.createOtp(any(CreateOtpReq.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new CreateOtpRes("483920")));

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isCreated());

        verify(this.otpHandler).createOtp(org.mockito.ArgumentMatchers.argThat(req ->
                req.id().equals("user-1")
                        && req.lifetime().equals(java.time.Duration.ofMinutes(5))
                        && req.attempts() == 3
                        && req.generateCodeReq().length() == 6
        ));
    }

    @Test
    void createReturns409WhenIdAlreadyExists() throws Exception {
        when(this.otpHandler.createOtp(any(CreateOtpReq.class)))
                .thenThrow(new ConflictException("OTP already exists"));

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("OTP already exists"));
    }

    @Test
    void createRejectsBlankId() throws Exception {
        final String body = VALID_CREATE_BODY.replace("\"user-1\"", "\"\"");

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.id").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createRejectsTooLongId() throws Exception {
        final String body = VALID_CREATE_BODY.replace("user-1", "a".repeat(129));

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.id").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createRejectsAttemptsOutOfRange() throws Exception {
        for (final int attempts : new int[]{0, 11}) {
            this.mockMvc.perform(post("/otp/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CREATE_BODY.replace("\"attempts\": 3", "\"attempts\": " + attempts)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.attempts").exists());
        }

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createRejectsLifetimeOutOfRange() throws Exception {
        for (final String lifetime : new String[]{"PT0S", "PT24H1S", "P2D"}) {
            this.mockMvc.perform(post("/otp/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CREATE_BODY.replace("PT5M", lifetime)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        }

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createAcceptsLifetimeBoundaries() throws Exception {
        when(this.otpHandler.createOtp(any(CreateOtpReq.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(new CreateOtpRes("483920")));

        for (final String lifetime : new String[]{"PT1S", "PT24H"}) {
            this.mockMvc.perform(post("/otp/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_CREATE_BODY.replace("PT5M", lifetime)))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void createRejectsMissingLifetime() throws Exception {
        final String body = """
                {
                  "id": "user-1",
                  "attempts": 3,
                  "generateCodeReq": { "length": 6, "characterTypes": ["DIGITS"] }
                }
                """;

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.lifetime").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createRejectsMissingGenerateCodeReq() throws Exception {
        final String body = """
                {
                  "id": "user-1",
                  "lifetime": "PT5M",
                  "attempts": 3
                }
                """;

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.generateCodeReq").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createValidatesNestedGenerateCodeReq() throws Exception {
        final String body = """
                {
                  "id": "user-1",
                  "lifetime": "PT5M",
                  "attempts": 3,
                  "generateCodeReq": { "length": 33, "characterTypes": [] }
                }
                """;

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['generateCodeReq.length']").exists())
                .andExpect(jsonPath("$.errors['generateCodeReq.characterTypes']").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createRejectsMalformedJson() throws Exception {
        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
                .andExpect(jsonPath("$.message").value("Malformed request body"));

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createRejectsUnknownCharacterType() throws Exception {
        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY.replace("DIGITS", "SYMBOLS")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void createHidesUnexpectedErrorDetails() throws Exception {
        when(this.otpHandler.createOtp(any(CreateOtpReq.class)))
                .thenThrow(new IllegalStateException("db password leaked"));

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("Internal Error"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("leaked"))));
    }

    @Test
    void createReturns500ForInternalException() throws Exception {
        when(this.otpHandler.createOtp(any(CreateOtpReq.class)))
                .thenThrow(new InternalException());

        this.mockMvc.perform(post("/otp/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));
    }

    @Test
    void deleteReturns204() throws Exception {
        when(this.otpHandler.deleteOtp(any(DeleteOtpReq.class)))
                .thenReturn(ResponseEntity.noContent().build());

        this.mockMvc.perform(delete("/otp/delete/{id}", "user-1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(this.otpHandler).deleteOtp(new DeleteOtpReq("user-1"));
    }

    @Test
    void deleteReturns404WhenOtpMissing() throws Exception {
        when(this.otpHandler.deleteOtp(any(DeleteOtpReq.class)))
                .thenThrow(new NotFoundException("OTP not found"));

        this.mockMvc.perform(delete("/otp/delete/{id}", "missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("OTP not found"));
    }

    @Test
    void deleteRejectsTooLongId() throws Exception {
        this.mockMvc.perform(delete("/otp/delete/{id}", "a".repeat(129)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(this.otpHandler, never()).deleteOtp(any());
    }

    @Test
    void deleteAcceptsMaxLengthId() throws Exception {
        final String id = "a".repeat(128);
        when(this.otpHandler.deleteOtp(any(DeleteOtpReq.class)))
                .thenReturn(ResponseEntity.noContent().build());

        this.mockMvc.perform(delete("/otp/delete/{id}", id))
                .andExpect(status().isNoContent());

        verify(this.otpHandler).deleteOtp(eq(new DeleteOtpReq(id)));
    }

    @Test
    void verifyReturns200WithResult() throws Exception {
        when(this.otpHandler.verifyOtp(any(VerifyOtpReq.class)))
                .thenReturn(ResponseEntity.ok(new VerifyOtpRes(true, 2)));

        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": "user-1", "password": "483920" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true))
                .andExpect(jsonPath("$.attempts").value(2));

        verify(this.otpHandler).verifyOtp(new VerifyOtpReq("user-1", "483920"));
    }

    @Test
    void verifyReturnsFalseForWrongPassword() throws Exception {
        when(this.otpHandler.verifyOtp(any(VerifyOtpReq.class)))
                .thenReturn(ResponseEntity.ok(new VerifyOtpRes(false, 1)));

        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": "user-1", "password": "000000" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(false))
                .andExpect(jsonPath("$.attempts").value(1));
    }

    @Test
    void verifyReturns404WhenOtpMissing() throws Exception {
        when(this.otpHandler.verifyOtp(any(VerifyOtpReq.class)))
                .thenThrow(new NotFoundException("OTP not found"));

        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": "user-1", "password": "483920" }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void verifyReturns400WhenOtpExpired() throws Exception {
        when(this.otpHandler.verifyOtp(any(VerifyOtpReq.class)))
                .thenThrow(new ExpiredOtpException("OTP expired"));

        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": "user-1", "password": "483920" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("OTP_EXPIRED"))
                .andExpect(jsonPath("$.message").value("OTP expired"));
    }

    @Test
    void verifyRejectsBlankFields() throws Exception {
        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "id": "", "password": "" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.id").exists())
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void verifyRejectsTooLongPassword() throws Exception {
        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": \"user-1\", \"password\": \"" + "9".repeat(129) + "\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());

        verifyNoInteractions(this.otpHandler);
    }

    @Test
    void verifyRejectsMissingBody() throws Exception {
        this.mockMvc.perform(post("/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));

        verifyNoInteractions(this.otpHandler);
    }
}
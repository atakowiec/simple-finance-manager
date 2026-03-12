package pl.pollub.backend.terrible;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.backend.terrible.dto.UserOnboardingDto;
import pl.pollub.backend.terrible.dto.UserOnboardingResultDto;

/**
 * Controller for user onboarding: create user, group, and first expense.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
@Tag(name = "Onboarding", description = "Tworzenie użytkownika, grupy i pierwszego wydatku")
public class UserOnboardingController {
    private final UserOnboardingService onboardingService;

    @Operation(summary = "Utwórz użytkownika, grupę i pierwszy wydatek")
    @ApiResponse(responseCode = "201", description = "Utworzono użytkownika, grupę i wydatek")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserOnboardingResultDto createAll(@Valid @RequestBody UserOnboardingDto createDto) {
        return onboardingService.createUserGroupAndExpense(createDto);
    }
}

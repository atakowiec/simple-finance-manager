package pl.pollub.backend.incomes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.pollub.backend.auth.user.User;
import pl.pollub.backend.categories.IncomeCategory;
import pl.pollub.backend.categories.IncomeCategoryRepository;
import pl.pollub.backend.exception.HttpException;
import pl.pollub.backend.group.GroupService;
import pl.pollub.backend.group.model.Group;
import pl.pollub.backend.incomes.dto.IncomeCreateDto;
import pl.pollub.backend.incomes.dto.IncomeUpdateDto;

import java.util.List;

@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;
    private final IncomeCategoryRepository categoryRepository;
    private final GroupService groupService;

    @GetMapping("/{groupId}")
    public List<Income> getIncomes(@PathVariable Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return incomeService.getAllIncomesForGroup(user, groupId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Income createIncome(@Valid @RequestBody IncomeCreateDto incomeCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        IncomeCategory category = categoryRepository.findById(incomeCreateDto.getCategoryId())
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "Nie znaleziono kategorii o podanym identyfikatorze: " + incomeCreateDto.getCategoryId()));

        Group group = groupService.getGroupByIdOrThrow(incomeCreateDto.getGroupId());
        groupService.checkMembershipOrThrow(user, group);

        Income income = new Income();
        income.setName(incomeCreateDto.getName());
        income.setAmount(incomeCreateDto.getAmount());
        income.setCategory(category);
        income.setUser(user);
        income.setDate(incomeCreateDto.getDate());
        income.setGroup(group);

        return incomeService.addIncome(income);
    }

    @PutMapping("/{id}")
    public Income updateIncome(@PathVariable Long id, @Valid @RequestBody IncomeUpdateDto incomeUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return incomeService.updateIncome(id, incomeUpdateDto, user);
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        incomeService.deleteIncome(id, user);
    }
}
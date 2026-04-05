package com.example.springgame.service;

import com.example.springgame.entity.ActionLog;
import com.example.springgame.entity.Choice;
import com.example.springgame.entity.Scenario;
import com.example.springgame.entity.Trait;
import com.example.springgame.entity.User;
import com.example.springgame.repository.ActionLogRepository;
import com.example.springgame.repository.ChoiceRepository;
import com.example.springgame.repository.ScenarioRepository;
import com.example.springgame.repository.TraitRepository;
import com.example.springgame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final ScenarioRepository scenarioRepository;
    private final ChoiceRepository choiceRepository;
    private final TraitRepository traitRepository;
    private final ActionLogRepository actionLogRepository;
    private final UserRepository userRepository;

    public record EndingResult(Trait topTrait, Long totalPoint) {}

    @Transactional
    public User getOrCreateFixedUser() {
        return userRepository.findById(1L).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setCurrentScenarioId(1L);
            return userRepository.save(newUser);
        });
    }

    @Transactional(readOnly = true)
    public Optional<Scenario> getScenario(Long scenarioId) {
        return scenarioRepository.findById(scenarioId);
    }
    
    @Transactional(readOnly = true)
    public EndingResult getEndingResult(Long userId) {
        List<Object[]> topTraitData = actionLogRepository.findTopTraitByUserId(userId);
        
        if (!topTraitData.isEmpty() && topTraitData.get(0) != null) {
            Object[] result = topTraitData.get(0);
            Long traitId = ((Number) result[0]).longValue();
            Long totalPoint = ((Number) result[1]).longValue();
            
            Trait trait = traitRepository.findById(traitId).orElse(null);
            return new EndingResult(trait, totalPoint);
        }
        
        return new EndingResult(null, 0L);
    }

    @Transactional
    public void resetScenarioForNextPlay(User user) {
        user.setCurrentScenarioId(1L);
        userRepository.save(user);
    }

    @Transactional
    public void processChoice(User user, Long choiceId) {
        Optional<Choice> choiceOpt = choiceRepository.findById(choiceId);
        if (choiceOpt.isEmpty()) {
            return;
        }

        Choice choice = choiceOpt.get();

        Long traitId = choice.getTraitId();
        if (traitId != null) {
            ActionLog log = new ActionLog();
            log.setUserId(user.getId());
            log.setTraitId(traitId);
            log.setPoints(choice.getTraitPoint());
            actionLogRepository.save(log);
        }

        user.setCurrentScenarioId(choice.getNextScenarioId());
        userRepository.save(user);
    }
}

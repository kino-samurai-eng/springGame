package com.example.springgame.controller;

import com.example.springgame.entity.Scenario;
import com.example.springgame.entity.User;
import com.example.springgame.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/")
    public String index() {
        return "redirect:/play";
    }

    @GetMapping("/play")
    public String play(Model model) {
        User user = gameService.getOrCreateFixedUser();
        Long currentScenarioId = user.getCurrentScenarioId() != null ? user.getCurrentScenarioId() : 1L;
        
        Optional<Scenario> scenarioOpt = gameService.getScenario(currentScenarioId);
        
        if (scenarioOpt.isEmpty()) {
            return "redirect:/";
        }

        Scenario scenario = scenarioOpt.get();

        if (scenario.isEnding()) {
            GameService.EndingResult result = gameService.getEndingResult(user.getId());

            model.addAttribute("scenario", scenario);
            model.addAttribute("topTrait", result.topTrait());
            model.addAttribute("totalPoint", result.totalPoint());
            
            gameService.resetScenarioForNextPlay(user);

            return "ending";
        }

        model.addAttribute("scenario", scenario);
        return "game";
    }

    @PostMapping("/play/choose")
    public String choose(@RequestParam Long choiceId) {
        User user = gameService.getOrCreateFixedUser();
        gameService.processChoice(user, choiceId);
        
        return "redirect:/play";
    }
}

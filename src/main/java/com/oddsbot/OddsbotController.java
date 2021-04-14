package com.oddsbot;

import com.oddsbot.exceptions.ElementNotFoundException;
import com.oddsbot.exceptions.LineGroupNotFoundException;
import com.oddsbot.exceptions.MatchNotFoundException;
import com.oddsbot.forms.LineForm;
import com.oddsbot.forms.TeamForm;
import com.oddsbot.model.line.Handicap;
import com.oddsbot.model.line.Moneyline;
import com.oddsbot.model.line.Total;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(path = "/bot")
public class OddsbotController {

    private final OddsbotService oddsbotService;

    public OddsbotController(OddsbotService oddsbotService) {
        this.oddsbotService = oddsbotService;
    }

    @GetMapping(path = "/setup")
    public @ResponseBody
    Void setup() throws ElementNotFoundException {
        return oddsbotService.setup();
    }

    @GetMapping(path = "/get-urls")
    public @ResponseBody
    Void addMatchesToRepo() throws InterruptedException, ElementNotFoundException {
        return oddsbotService.addMatchesToRepo();
    }

    @PostMapping(path = "/ml")
    public @ResponseBody
    List<Moneyline> getMoneyline(@RequestBody TeamForm form) throws
            LineGroupNotFoundException, ElementNotFoundException, MatchNotFoundException, ExecutionException,
            InterruptedException {
        return oddsbotService.getMoneyline(form.getTeam());
    }

    @PostMapping(path = "/hc")
    public @ResponseBody
    List<Handicap> getHandicap(@RequestBody LineForm form) throws
            LineGroupNotFoundException, ElementNotFoundException, MatchNotFoundException, ExecutionException,
            InterruptedException {
        return oddsbotService.getHandicap(form.getTeam(), form.getValue());
    }

    @PostMapping(path = "/t")
    public @ResponseBody
    List<Total> getTotal(@RequestBody LineForm form) throws
            LineGroupNotFoundException, ElementNotFoundException, MatchNotFoundException, ExecutionException,
            InterruptedException {
        return oddsbotService.getTotal(form.getTeam(), form.getValue());
    }
}
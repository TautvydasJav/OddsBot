package com.oddsbot;

import com.oddsbot.exceptions.ElementNotFoundException;
import com.oddsbot.exceptions.LineGroupNotFoundException;
import com.oddsbot.exceptions.MatchNotFoundException;
import com.oddsbot.model.line.Handicap;
import com.oddsbot.model.line.Moneyline;
import com.oddsbot.model.line.Total;
import com.oddsbot.model.match.MatchRepository;
import com.oddsbot.services.BookmakerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class OddsbotService {

    private List<BookmakerService> bookmakerServices;

    private final MatchRepository matchRepository;

    private final Set<String> activeBookmakers;

    public OddsbotService(List<BookmakerService> bookmakerServices,
                          MatchRepository matchRepository, @Value("${bookmaker.active-bookmakers}") List<String> activeBookmakers) {
        this.bookmakerServices = bookmakerServices;
        this.matchRepository = matchRepository;
        this.activeBookmakers = activeBookmakers.stream().map(String::toUpperCase).collect(toSet());
        filterBookmakerList();
    }

    public Void setup() throws ElementNotFoundException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (var service : bookmakerServices) {
            futures.add(service.setup());
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public Void addMatchesToRepo() throws InterruptedException, ElementNotFoundException {
        matchRepository.deleteAll();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (var service : bookmakerServices) {
            futures.add(service.addMatchesToRepo());
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public List<Moneyline> getMoneyline(String team) throws
            ExecutionException, InterruptedException, ElementNotFoundException, MatchNotFoundException,
            LineGroupNotFoundException {
        List<CompletableFuture<Optional<Moneyline>>> futures = new ArrayList<>();
        for (var service : bookmakerServices) {
            futures.add(service.getMoneyline(team));
        }
        return getResult(futures);
    }

    public List<Handicap> getHandicap(String team, String value) throws
            LineGroupNotFoundException, ElementNotFoundException, MatchNotFoundException, ExecutionException,
            InterruptedException {
        List<CompletableFuture<Optional<Handicap>>> futures = new ArrayList<>();
        for (var service : bookmakerServices) {
            futures.add(service.getHandicap(team, value));
        }
        return getResult(futures);
    }

    public List<Total> getTotal(String team, String value) throws LineGroupNotFoundException,
            ElementNotFoundException, MatchNotFoundException, ExecutionException, InterruptedException {
        List<CompletableFuture<Optional<Total>>> futures = new ArrayList<>();
        for (var service : bookmakerServices) {
            futures.add(service.getTotal(team, value));
        }
        return getResult(futures);
    }

    private void filterBookmakerList() {
        bookmakerServices = bookmakerServices.stream()
                .filter(service -> activeBookmakers.contains(service.getBookmaker().name()))
                .collect(toList());
    }


    private <T> List<T> getResult(List<CompletableFuture<Optional<T>>> futures) throws InterruptedException, ExecutionException {
        var toReturn = convertToFutureOfStream(futures).thenApply(stream ->
                stream.filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
        );
        return toReturn.get();
    }

    private <T> CompletableFuture<Stream<T>> convertToFutureOfStream(List<CompletableFuture<T>> toConvert) {
        return CompletableFuture.allOf(toConvert.toArray(CompletableFuture[]::new))
                .thenApply(
                        v -> toConvert.stream()
                                .map(CompletableFuture::join)
                );
    }
}

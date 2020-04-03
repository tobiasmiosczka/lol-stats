package com.github.tobiasmiosczka.lolstats;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.constant.LeagueQueue;
import net.rithms.riot.api.endpoints.league.dto.LeagueItem;
import net.rithms.riot.api.endpoints.league.dto.LeagueList;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static final int TIME_SLEEP = 1500;
    private static final Platform PLATFORM = Platform.EUW;





    public static void main(String[] args) throws IOException, InterruptedException {
        loadMatchInfos();
    }

    public static void loadMatchInfos() throws IOException, InterruptedException {
        ApiConfig config = new ApiConfig().setKey(ApiKeyHelper.getApiKey());
        RiotApi api = new RiotApi(config);

        Set<Long> matchIds = new HashSet<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("matchIds.txt"));
        String line = bufferedReader.readLine();
        while (line != null) {
            matchIds.add(Long.valueOf(line));
            // read next line
            line = bufferedReader.readLine();
        }
        bufferedReader.close();



        for (long matchId : matchIds) {
            //api.getMatch(PLATFORM, matchId);

        }

        //api.getMatch(PLATFORM, 0L).getParticipants().get(0).getStats().
    }

    public static void loadMatches() throws IOException, InterruptedException {
        ApiConfig config = new ApiConfig().setKey(ApiKeyHelper.getApiKey());
        RiotApi api = new RiotApi(config);

        //load all top 300 players
        LeagueList l = null;
        try {
            l = api.getChallengerLeagueByQueue(PLATFORM, LeagueQueue.RANKED_SOLO_5x5);
        } catch (RiotApiException e) {
            e.printStackTrace();
        }

        //get summoners
        List<Summoner> summoners = new LinkedList<>();
        for (LeagueItem leagueItem : l.getEntries()) {
            System.out.println("Loading: " + leagueItem.getSummonerName());
            try {
                summoners.add(api.getSummoner(PLATFORM, leagueItem.getSummonerId()));
            } catch (RiotApiException e) {
                e.printStackTrace();
            }
            Thread.sleep(TIME_SLEEP);
        }

        //load all ranked matches by these players
        Set<Long> matchIds = new HashSet<>();
        for (Summoner summoner : summoners) {
            System.out.println("Loading: " + summoner.getName());
            Set<Long> playersMatchIds = null;
            try {
                playersMatchIds = api.getMatchListByAccountId(PLATFORM, summoner.getAccountId()).getMatches().stream()
                        .filter(match -> match.getQueue() == 420)
                        .map(match -> match.getGameId()).collect(Collectors.toSet());
                matchIds.addAll(playersMatchIds);
            } catch (RiotApiException e) {
                e.printStackTrace();
            }
            Thread.sleep(TIME_SLEEP);
        }

        FileWriter  fileWriter = new FileWriter("matchIds.txt");
        for (Long id : matchIds) {
            fileWriter.append(id +"\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }
}

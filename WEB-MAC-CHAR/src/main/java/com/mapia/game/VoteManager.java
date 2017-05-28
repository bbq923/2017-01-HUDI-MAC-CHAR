package com.mapia.game;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mapia.domain.Player;
import com.mapia.websocket.VoteMessage;

public class VoteManager {
    private static final Logger log = LoggerFactory.getLogger(VoteManager.class);

    private Map<Player, Player> voteStatus;
    private GamePlayers players;

    public VoteManager(GamePlayers players) {
        this.players = players;
        this.voteStatus = new HashMap<>(this.players.countOfPlayers());
    }

    public boolean handleVote(VoteMessage voteMessage) {
        log.debug("handleVote: {}", voteMessage);
        Player playerVoting = this.players.getPlayer(voteMessage.getUserName());
        Player playerVoted = this.players.getPlayer(voteMessage.getTheVoted());
        
        if (playerVoting == null || playerVoted == null) {
        	return false;
        }
        
        voteStatus.put(playerVoting, playerVoted);
        //TODO Below code is TEST CODE, DELETE or COMMENT this code before commit.
//        if (this.players.getPlayer("testUser1") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser1"), this.players.getPlayer("testUser1"));
//        }
//        if (this.players.getPlayer("testUser2") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser2"), this.players.getPlayer("testUser2"));
//        }
//        if (this.players.getPlayer("testUser3") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser3"), this.players.getPlayer("testUser3"));
//        }
//        if (this.players.getPlayer("testUser4") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser4"), this.players.getPlayer("testUser4"));
//        }
//        if (this.players.getPlayer("testUser5") != null) {
//            this.voteStatus.put(this.players.getPlayer("testUser5"), this.players.getPlayer("testUser5"));
//        }
        // test room 에 미리 들어가 있던 세명의 testUser 는 각각 자신을 vote 한다.
        
        log.debug("VOTESTATUS.SIZE: {}       PLAYERS: {}", voteStatus.size(), this.players.countOfPlayers());
        if (voteStatus.size() == this.players.countOfPlayers()) {
            return true;
        }
        return false;
//        return true;
    }

    public GameResult returnGameResult(String stage) {
        log.debug("returnGameResult:stage: {}", stage);
        String selectedUserNickName;
        if (stage.equals("day")) {
            selectedUserNickName = determineResultOfDay(countVoteOfDay());
            log.debug("returnGameResult:Day logic:selectedUserNickName: {}", selectedUserNickName);
        } else {
            selectedUserNickName = determineResultOfNight(countVoteOfMafia(), countVoteOfDoctor());
            log.debug("returnGameResult:Night logic:selectedUserNickName: {}", selectedUserNickName);
        }
        GameResultType gameResultType = this.players.judgementPlayersCount();
        switch (gameResultType) {
            case MAFIA_WIN:
                log.debug("마피아가 승리하였습니다.");
                return GameResult.returnMafiaWin();
            case CITIZEN_WIN:
                log.debug("시민이 승리하였습니다.");
                return GameResult.returnCitizenWin();
            case KEEP_GOING:
                log.debug("selectedUser: {}", selectedUserNickName);
                return GameResult.returnSeletedUser(selectedUserNickName);
            default:
                throw new RuntimeException("Unexpected Error!");
        }
    }

    private Map<Player, Integer> countVoteOfDay() {
        Map<Player, Integer> countStatus = new HashMap<>();
        voteStatus.keySet().forEach(player -> countStatus.put(player, 0));
        voteStatus.values().forEach(player -> {
            if (player != null) { //기권표를 걸러낸다.
                countStatus.put(player, countStatus.get(player) + 1);
            }
        });
        log.debug("countStatus setting: {}", countStatus);
        return countStatus;
    }

    private Map<Player, Integer> countVoteOfMafia() {
        Map<Player, Integer> countStatusOfMafia = new HashMap<>();
        voteStatus.keySet().stream()
//            .filter(player -> player.isMafia())
            .forEach(player -> countStatusOfMafia.put(player, 0));
        voteStatus.keySet().stream()
            .filter(player -> player.isMafia())
            .forEach(player -> {
                if (player != null) { //기권표를 걸러낸다.
                    countStatusOfMafia.put(voteStatus.get(player), countStatusOfMafia.get(voteStatus.get(player)) + 1);
                }
            });
        log.debug("countVoteOfMafia:countStatusOfMafia: {}", countStatusOfMafia);
        return countStatusOfMafia;
    }

    private Map<Player, Integer> countVoteOfDoctor() {
        Map<Player, Integer> countStatusOfDoctor = new HashMap<>();
        voteStatus.keySet().stream()
//            .filter(player -> player.isDoctor())
            .forEach(player -> countStatusOfDoctor.put(player, 0));
        voteStatus.keySet().stream()
            .filter(player -> player.isDoctor())
            .forEach(player -> {
                if (player != null) { //기권표를 걸러낸다.
                    countStatusOfDoctor.put(voteStatus.get(player), countStatusOfDoctor.get(voteStatus.get(player)) + 1);
                }
            });
        log.debug("countVoteOfDoctor:countStatusOfDoctor: {}", countStatusOfDoctor);
        return countStatusOfDoctor;
    }

    private String determineResultOfDay(Map<Player, Integer> countStatus) {
        Player selectedPlayer = null;
        int base = 0;
        for (Map.Entry<Player, Integer> entry : countStatus.entrySet()) {
            //TODO 동률일 때 로직 추가
            if (entry.getValue() > base) {
                selectedPlayer = entry.getKey();
                base = entry.getValue();
            } else if (base == entry.getValue()) {
                selectedPlayer = null;
            }
        }
        log.debug("determineResultOfDay:resultSelectedPlayer: {}", selectedPlayer);
        if (selectedPlayer != null) {
            selectedPlayer.kill();
            this.players.removeDeadPlayer(selectedPlayer); //투표의 결과로 사망시 players에서 제외
            voteStatus.clear();
            return selectedPlayer.getUser().getNickname();
        }
        voteStatus.clear(); //투표가 종료된 뒤 voteStatus 초기화
        return "";
    }

    private String determineResultOfNight(Map<Player, Integer> countStatusOfMafia, Map<Player, Integer> countStatusOfDoctor) {
        Player mafiaSelectPlayer = null;
        Player doctorSelectPlayer = null;
        int base = 0;
        for (Map.Entry<Player, Integer> entry : countStatusOfMafia.entrySet()) {
            if (entry.getValue() > base) {
                mafiaSelectPlayer = entry.getKey();
                base = entry.getValue();
            } else if (base == entry.getValue()) {
                mafiaSelectPlayer = null;
            }
        }
        base = 0;
        for (Map.Entry<Player, Integer> entry : countStatusOfDoctor.entrySet()) {
            if (entry.getValue() > base) {
                doctorSelectPlayer = entry.getKey();
                base = entry.getValue();
            }
        }
        log.debug("determineResultOfNight:mafiaSelectPlayer: {}", mafiaSelectPlayer);
        log.debug("determineResultOfNight:doctorSelectPlayer: {}", doctorSelectPlayer);

        if (mafiaSelectPlayer != null && doctorSelectPlayer != null) {
            mafiaSelectPlayer.kill();
            doctorSelectPlayer.safe();

            if (!mafiaSelectPlayer.isAlive()) {
                this.players.removeDeadPlayer(mafiaSelectPlayer); //투표의 결과로 사망시 players에서 제외
                return mafiaSelectPlayer.getUser().getNickname();
            }
            return "";
        } else if (mafiaSelectPlayer != null) {
            mafiaSelectPlayer.kill();
            this.players.removeDeadPlayer(mafiaSelectPlayer);
            return mafiaSelectPlayer.getUser().getNickname();
        }
        return "";
    }
}

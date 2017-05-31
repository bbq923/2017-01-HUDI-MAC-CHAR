import timeCalculator from "./timeCalculator";
import {printMessage} from "./room_util";

export default class nightTime {
    constructor(voteSocket, investSocket) {
        this.timeCalculator = new timeCalculator();
        this.userName = document.getElementById("userName").textContent;
        this.slot_box = document.getElementsByClassName("slot_box")[0];
        this.voteSocket = voteSocket;
        this.investSocket = investSocket;

        this.voted = null;
        this.NIGHT_TIME = 30;
        this.nightTime = this;
        this.voteFunction = this.nightTimeVote.bind(this);
    }

    setDay(dayTime) {
        this.dayTime = dayTime;
    }

    setRole(role) {
        this.role = role;
    }

    start() {
        document.querySelector('body').classList.remove('dayTime');
        document.querySelector('body').classList.add('main');
        printMessage("밤이 되었습니다 각자 역할에 맞게 투표해주세요");
        this.slot_box.addEventListener("click", this.voteFunction);
        this.nightTimerStart();
    }

    nightTimeVote({target}) {
        if (target.tagName === "DIV" && this.role !== "Citizen") {
            this.clearBoard();
            target.parentElement.getElementsByClassName("player_status")[0].textContent = "VOTED";
            this.voted = target.parentElement;
        }
        
        Array.from(document.getElementsByClassName("dead")).forEach(slot => {
            if (slot.getAttribute("data-id") === this.userName) {
                this.voted.getElementsByClassName("player_status")[0].textContent = "";
                this.voted = null;
            }
            if (slot.getElementsByClassName("player_status")[0].textContent !== "") { //죽은 player에 투표한 경우
                slot.getElementsByClassName("player_status")[0].textContent = "";
                this.voted = null;
            }
        });
    }

    nightTimerStart() {
        this.timeCalculator.startTimer(this.NIGHT_TIME, this.sendNightTimeVoteResult.bind(this.nightTime));
    }

    sendNightTimeVoteResult() {
        let victim = this.voted === null ? "undefined" : this.voted.getElementsByClassName("player_name")[0].textContent;
        if (this.role === "Mafia" || this.role === "Doctor") {
        	this.voteSocket.sendVoteResult(this.userName, victim, "night");
        }
        if (this.role === "Police") {
        	this.voteSocket.sendVoteResult(this.userName, "undefined", "night");
        	this.investSocket.sendInvest(victim);
        }
        if (this.role === "Citizen") {
        	this.voteSocket.sendVoteResult(this.userName, "undefined", "night");
        }
        this.slot_box.removeEventListener("click", this.voteFunction);
        printMessage("경기 결과를 처리 중입니다");
        this.clearBoard();
        this.voted = null;
        setTimeout(() => {
            if (!this.voteSocket.gameIsFinished()) {
                this.dayTime.start();
            }
        }, 5000);
    }

    clearBoard() {
        if (this.voted !== null) {
            this.voted.getElementsByClassName("player_status")[0].textContent = "";
        }
    }
}

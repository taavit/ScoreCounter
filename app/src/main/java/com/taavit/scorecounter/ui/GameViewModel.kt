package com.taavit.scorecounter.ui

import androidx.lifecycle.ViewModel
import com.taavit.scorecounter.Algos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.ceil

enum class Player {
    Player1,
    Player2,
}

enum class ActiveScreen {
    SELECT_SCREEN,
    MATCH_SCREEN,
}

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    val algos: Algos = Algos()

    init {
        resetGame()
    }

    fun updatePlayerScore(player: Player, scored: Byte) {
        _uiState.update {
            currentState ->
                val p1Score = if (player == Player.Player1) { scored } else { currentState.p1Score }
                val p2Score = if (player == Player.Player2) { scored } else { currentState.p2Score }
                val startingPlayer: Byte = if (currentState.startingPlayer == Player.Player1) { 1 } else { 2 }
                val currentServe = if (algos.determine_player(
                    p1Score,
                    p2Score,
                    startingPlayer,
                ) == 1.toByte()
                ) { Player.Player1 } else { Player.Player2 }
                currentState.copy(
                    p1Score = p1Score,
                    p2Score = p2Score,
                    currentServe = currentServe,
                )
            }
    }

    fun resetGame() {
        _uiState.value = GameUiState()
    }

    fun resetScore() {
        _uiState.update {
            currentState -> currentState.copy(
                p1Score = 0,
                p2Score = 0,
                currentServe = currentState.startingPlayer
            )
        }
    }

    /*internal fun determinePlayer(p1Score: Int, p2Score: Int, startingPlayer: Player): Player {
        if (p1Score + p2Score == 0) {
            return startingPlayer
        }
        if (p1Score + p2Score < 20) {
            return if (ceil((p1Score + p2Score + 1) / 2.0).toInt() % 2 == 0) {
                when (startingPlayer) {
                    Player.Player1 -> Player.Player2;
                    Player.Player2 -> Player.Player1;
                }
            } else {
                startingPlayer
            }
        }
        return if ((p1Score + p2Score + 1) % 2 == 0) {
            when (startingPlayer) {
                Player.Player1 -> Player.Player2;
                Player.Player2 -> Player.Player1;
            }
        } else {
            startingPlayer
        }
    }*/
}
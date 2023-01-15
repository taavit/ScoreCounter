package com.taavit.scorecounter.ui

data class GameUiState(
    val p1Score: Byte = 0,
    val p2Score: Byte = 0,
    val startingPlayer: Player = Player.Player1,
    val currentServe: Player = Player.Player1,
    val isGameFinished: Boolean = false,
    val activeScreen: ActiveScreen = ActiveScreen.MATCH_SCREEN
)

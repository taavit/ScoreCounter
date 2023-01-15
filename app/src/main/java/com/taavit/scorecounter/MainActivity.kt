package com.taavit.scorecounter

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taavit.scorecounter.ui.theme.ScoreCounterTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taavit.scorecounter.ui.ActiveScreen
import com.taavit.scorecounter.ui.GameViewModel
import com.taavit.scorecounter.ui.Player

class Algos {
    external fun determine_player(
        p1Score: Byte,
        p2Score: Byte,
        starting: Byte
    ): Byte

    init {
        System.loadLibrary("android_rust")
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val algo = Algos()
        Log.d("MATCH_APP", "Player" + algo.determine_player(1, 2, 1))
        setContent {
            ScoreCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScoreCounterApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Select first player",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Row (modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)) {
            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1F)
                    .padding(all = 8.dp)
            ) {
                Text("Player 1")
            }
            Button(
                modifier = Modifier
                    .weight(1F)
                    .padding(all = 8.dp),
                onClick = {}
            ) {
                Text("Player 2")
            }
        }
    }
}

@Composable
fun MatchScreen(
    p1Score: Byte,
    p2Score: Byte,
    servingPlayer: Player,
    updateScore: (Player, Byte) -> Unit,
    resetScore: () -> Unit,
) {

    val p1ScoreString = if (servingPlayer == Player.Player1) {
        "\uD83C\uDFD3 $p1Score"
    } else {
        p1Score.toString()
    }

    val p2ScoreString = if (servingPlayer == Player.Player2) {
        "\uD83C\uDFD3 $p2Score"
    } else {
        p2Score.toString()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Match",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Row (modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)) {
            Column (modifier = Modifier.weight(1F)) {
                Button(
                    modifier = Modifier.padding(all=8.dp),
                    onClick = { updateScore(Player.Player1, (p1Score + 1).toByte()) }
                ) {
                    Text(
                        text=p1ScoreString,
                        fontSize = 36.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { if (p1Score > 0 ) { updateScore(Player.Player1,
                        (p1Score - 1).toByte()
                    ) } },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text("-1")
                }
            }
            Column (modifier = Modifier.weight(1F)) {
                Button(
                    modifier = Modifier.padding(all=8.dp),
                    onClick = { updateScore(Player.Player2, (p2Score + 1).toByte()) }
                ) {
                    Text(
                        text=p2ScoreString,
                        fontSize = 36.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { if (p2Score > 0 ) updateScore(Player.Player2,
                        (p2Score - 1).toByte()
                    ) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text("-1")
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .wrapContentHeight(Alignment.Bottom)
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { resetScore() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Reset", modifier = Modifier.padding(horizontal = 16.dp))
            }

            Button(
                onClick = { resetScore() },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "Finish game", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun ScoreCounterApp(gameViewModel: GameViewModel = viewModel()) {
    val gameUiState by gameViewModel.uiState.collectAsState()

    when (gameUiState.activeScreen) {
        ActiveScreen.SELECT_SCREEN -> SelectScreen()
        ActiveScreen.MATCH_SCREEN -> MatchScreen(
            p1Score = gameUiState.p1Score,
            p2Score = gameUiState.p2Score,
            updateScore = { player, score -> gameViewModel.updatePlayerScore(player, score) },
            servingPlayer = gameUiState.currentServe,
            resetScore = { gameViewModel.resetScore() },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ScoreCounterTheme {
        ScoreCounterApp()
    }
}
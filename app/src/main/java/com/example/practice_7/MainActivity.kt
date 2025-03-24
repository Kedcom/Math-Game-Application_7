package com.example.practice_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practice_7.ui.theme.Practice_7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Practice_7Theme {
                MathGameApp()
            }
        }
    }
}


@Composable
fun MathGameApp() {
    var screen by remember { mutableStateOf("start") }
    var numQuestions by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var wrongQuestions by remember { mutableStateOf(listOf<Pair<String, Int>>()) } //used to store the failed questions and answers...

    when (screen) {
        "start" -> StartScreen(
            onStart = { questions ->
                numQuestions = questions
                screen = "game"
            }
        )
        "game" -> GameScreen(
            numQuestions = numQuestions,
            onGameOver = {correct, wrong, wrongQuestionsList  ->
                correctAnswers = correct
                wrongAnswers = wrong
                wrongQuestions = wrongQuestionsList
                screen = "result"
            },
            onCancel = {
                screen = "start"
            }
        )
        "result" -> ResultScreen(
            correctAnswers,
            wrongAnswers,
            wrongQuestions,
            onRestart = {
                screen = "start"
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(onStart: (Int) -> Unit) {
    var input by remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFF595959)), //
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {

                //title
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Math Game\n",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(100.dp)
                    )
                    Text(
                        text = "\n+ - / *",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF00CE00),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(100.dp)
                    )
                }
                //title___END


                Text(text = "Enter a number:")
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val num = input.toIntOrNull()
                        if (num != null && num > 0) onStart(num)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF01DA01),
                        contentColor =  Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Game")
                }
            }
        }
    )
}

@Composable
fun GameScreen(numQuestions: Int, onGameOver: (Int, Int, List<Pair<String, Int>>) -> Unit, onCancel: () -> Unit) {
    var currentQuestion by remember { mutableStateOf(1) }
    var correctAnswers by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var wrongQuestions by remember { mutableStateOf(listOf<Pair<String, Int>>()) } // Stores the failed questions
    var firstNum by remember { mutableStateOf((1..50).random()) }
    var secondNum by remember { mutableStateOf((1..50).random()) }
    var answerInput by remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(start = 40.dp).height(100.dp),
                        text = "Passed: $correctAnswers \t\t Failed: $wrongAnswers",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center,

                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Question $currentQuestion of $numQuestions")

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "What is $firstNum + $secondNum?",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                )


                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            answerInput.toIntOrNull()?.let { userAnswer ->
                                if (userAnswer == firstNum + secondNum){
                                    correctAnswers++
                                }
                                else{
                                    wrongAnswers++
                                    wrongQuestions = wrongQuestions + Pair(
                                        "What is $firstNum + $secondNum?",
                                        firstNum + secondNum
                                    )
                                }

                                if (currentQuestion < numQuestions) {
                                    currentQuestion++
                                    firstNum = (1..50).random()
                                    secondNum = (1..50).random()
                                    answerInput = ""
                                } else {
                                    onGameOver(correctAnswers, wrongAnswers, wrongQuestions)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF01DA01)
                        )
                    ) {
                        Text("Next")
                    }
                }

            }
        }
    )
}

@Composable
fun ResultScreen(
    correct: Int, wrong: Int,
    wrongQuestions: List<Pair<String, Int>>,
    onRestart: () -> Unit

) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Game Over",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(100.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Correct Answers: $correct")
                Text(text = "Wrong Answers: $wrong")

                // Displays correct answer
                if (wrong > 0) {
                    Text(
                        text = "\n Failed Question Answer:",
                        color = Color.Red
                    )
                    wrongQuestions.forEachIndexed { index, question ->
                        Text(
                            text = "\n ${index + 1}. ${question.first} Answer: ${question.second}",
                            color = Color.Red
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF01DA01)
                    )
                ) {
                    Text("Restart")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Practice_7Theme {
        StartScreen(onStart = {})
    }
}

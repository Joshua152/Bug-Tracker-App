package com.example.bugtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.bugtracker.data.data.Bug
import com.example.bugtracker.data.data.Project
import com.example.bugtracker.data.datasource.BugDataSource
import com.example.bugtracker.data.datasource.ProjectDataSource
import com.example.bugtracker.ui.theme.BugTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BugTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val projectDataSource = ProjectDataSource.getInstance("https://e56d-24-7-101-23.ngrok-free.app", context = context)
    val bugDataSource = BugDataSource.getInstance("https://e56d-24-7-101-23.ngrok-free.app", context = context)

    TextButton(
        onClick = {
//            projectDataSource.getProject(3) { project ->
//               println("Project: $project")
//            }

            bugDataSource.deleteBug(30) { response ->
                println("Respones: $response")
            }
        }
    ) {
        Text(text="Query")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BugTrackerTheme {
        Greeting("Android")
    }
}
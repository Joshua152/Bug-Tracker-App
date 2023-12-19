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
import androidx.room.Room
import com.example.bugtracker.data.BugRepository
import com.example.bugtracker.data.local.Database
import com.example.bugtracker.data.network.datasource.BugDataSource
import com.example.bugtracker.data.network.datasource.ProjectDataSource
import com.example.bugtracker.ui.theme.BugTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val localDB = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "database"
        ).allowMainThreadQueries().build()

        val projectDao = localDB.projectDao()
        val bugDao = localDB.bugDao()

//        projectDao.add(listOf(DatabaseProject(projectID = 1000, name = "new project")))
//        val dbProjects = projectDao.getAll()
//        for(dbProject in dbProjects) {
//            println("Project: $dbProject")
//        }
        val bugDataSource = BugDataSource.getInstance("https://68ea-129-2-192-197.ngrok-free.app", context = applicationContext)
        val bugRepo = BugRepository.getInstance(bugDao, bugDataSource)

//        bugDataSource.getBug(681) {
//            println("DATA SOURCE: $it")
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//            val bug = bugRepo.getBug(681)
//            println("LAUNCH: $bug")
//        }

//        runBlocking {
//            val bugs = bugRepo.getAllBugs();
//            println("Bugs: $bugs");
//        }

        GlobalScope.launch {
            bugDao.deleteAll()

//            val b = bugRepo.getProject(426);
//            println("bug: $b");
        }

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

//            bugDataSource.deleteBug(30) { response ->
//                println("Respones: $response")
//            }
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
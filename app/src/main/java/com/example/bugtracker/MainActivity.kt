package com.example.bugtracker

import android.os.Bundle
import android.provider.Settings.Global
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.bugtracker.data.BugRepository
import com.example.bugtracker.data.domain.Bug
import com.example.bugtracker.data.local.Database
import com.example.bugtracker.data.local.dao.BugDao
import com.example.bugtracker.data.local.models.DatabaseProject
import com.example.bugtracker.data.network.datasource.BugDataSource
import com.example.bugtracker.data.network.datasource.ProjectDataSource
import com.example.bugtracker.networkutils.InternetConnectivity
import com.example.bugtracker.ui.theme.BugTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val dbURL = "https://c7f0-67-164-28-229.ngrok-free.app"

var bugDao: BugDao? = null
var bugRepo: BugRepository? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val localDB = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "database"
        ).allowMainThreadQueries().build() //TODO DISALLOW MAIN THREAD QUERIES

        val projectDao = localDB.projectDao()
        bugDao = localDB.bugDao()

        val bugDataSource = BugDataSource.getInstance(dbURL, context = applicationContext)
        val projectDataSource = ProjectDataSource.getInstance(dbURL, context = applicationContext)

        bugRepo = BugRepository.getInstance(bugDao!!, bugDataSource)

        println("Init")

        setContent {
            BugTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Buttons()
                }
            }
        }
    }
}

@Composable
fun Buttons(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column {
        TextButton(
            onClick = {
                GlobalScope.launch {
                    bugRepo!!.refresh()
                }
            }
        ) {
            Text(text="Refresh")
        }
        TextButton(
            onClick = {
                GlobalScope.launch {
                    println("dao: ${bugDao!!.getAll()}")
                    println("bug repo: ${bugRepo!!.getAllBugs()}")
                }
            }
        ) {
            Text(text="Get all")
        }
        TextButton(
            onClick = {
                GlobalScope.launch {
                    println("bug: ${bugRepo!!.getBug(180)}")
                }
            }
        ) {
            Text(text="Get bug #180")
        }
        TextButton(
            onClick = {
                println("Clicked add")
                GlobalScope.launch {
                    bugRepo!!.addBug(
                        Bug(
                            projectID = 3,
                            bugID = 190,
                            title = "Bug 190",
                            description = "Description for bug 190",
                            timeAmt = 0.9,
                            complexity = 0.60
                        )
                    )
                }
            }
        ) {
            Text(text="Add bug #190")
        }
        TextButton(
            onClick = {
                GlobalScope.launch {
                    bugRepo!!.deleteBug(190)
                }
            }
        ) {
            Text(text="Delete bug #190")
        }
        TextButton(
            onClick = {
                GlobalScope.launch {
                    bugRepo!!.deleteAll()
                }
            }
        ) {
            Text(text="Delete all")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    BugTrackerTheme {
        Buttons()
    }
}
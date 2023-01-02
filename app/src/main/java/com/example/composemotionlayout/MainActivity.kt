package com.example.composemotionlayout

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.example.composemotionlayout.ui.theme.ComposeMotionLayoutTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val users = mutableListOf<Int>()
            for(i in 0..100 ){
                users.add(i)
            }

            ComposeMotionLayoutTheme {
                val lazyListState = rememberLazyListState()
                Scaffold(topBar =
                {
                        val progress by animateFloatAsState(
                            targetValue = if (lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0) 0f else 1f,
                            tween(800)
                        )
                    AppBar(progress = progress)
                }
                ) {
                    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                        items(users){
                            Card(modifier = Modifier.padding(4.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(4.dp)) {
                                    Text("User", modifier = Modifier.padding(4.dp))
                                    Text(it.toString(), modifier = Modifier.padding(4.dp))
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMotionApi::class)
@Composable
fun AppBar(progress: Float) {
    val context = LocalContext.current
    val motionSceneContent = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }
    MotionLayout(
        motionScene = MotionScene(motionSceneContent),
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primaryVariant),
    ) {
        val properties = motionProperties(id = "my_text")

        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier
            .layoutId("back_button"), tint = MaterialTheme.colors.background
        )

        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier
            .layoutId("search_button"), tint = MaterialTheme.colors.background
        )

        Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier
            .layoutId("list_button"), tint = MaterialTheme.colors.background
        )

        Avatar("my_image")

        Column( modifier = Modifier
            .layoutId("my_text")) {
            Text(
                text = "Group Name",
                color = properties.value.color("textColor"),
                fontSize = properties.value.fontSize("textSize"),
                fontWeight = FontWeight.SemiBold
                )
            Text(text = "30 users", fontSize = properties.value.fontSize("textSize"), color = Color.LightGray)
        }

        OtherSettings("settings")
    }
}

@Composable
fun Avatar(id: String) {
    Image(imageVector = Icons.Default.Person, contentDescription = null, colorFilter = ColorFilter.tint(
        MaterialTheme.colors.background), contentScale = ContentScale.Crop, modifier = Modifier
        .layoutId(id)
        .clip(CircleShape))
}

@Composable
fun OtherSettings(id: String) {
    Column( modifier = Modifier
        .fillMaxWidth(0.9f)
        .layoutId(id),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row() {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colors.background)
                TextButton(
                    onClick = { /*TODO*/ }) {
                    Text("Audio", color = MaterialTheme.colors.background, fontSize = MaterialTheme.typography.h6.fontSize)
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colors.background)
                TextButton(
                    onClick = { /*TODO*/ }) {
                    Text("Settings", color = MaterialTheme.colors.background, fontSize = MaterialTheme.typography.h6.fontSize)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()) {
            Text("some switch setting", color = MaterialTheme.colors.background)
            Switch(checked = true, onCheckedChange = {})
        }
    }
}
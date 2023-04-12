package com.example.composemotionlayout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.example.composemotionlayout.AppBarState.PreExpand.modify
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

            var expand by remember{ mutableStateOf(true) }

            ComposeMotionLayoutTheme {
                val lazyListState = rememberLazyListState()
                Scaffold(topBar =
                {
                        val progress by animateFloatAsState(
                            targetValue = if (expand) 0f else 1f,
                            tween(800)
                        )
                    AppBar(progress = progress)
                }
                ) {
                    LazyColumn(state = lazyListState,
                               modifier = Modifier.fillMaxWidth()
                                                  .appBarViewState(lazyListState, expand = {
                                                      Log.i("MainActivity", "expand = $it")
                                                      expand = it
                                                  }),
                        horizontalAlignment = Alignment.CenterHorizontally){
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



@SuppressLint("ModifierFactoryUnreferencedReceiver")
@Composable
fun Modifier.appBarViewState(lazyListState: LazyListState, expand: (Boolean) -> Unit): Modifier{
    var state by remember{ mutableStateOf<AppBarState>(AppBarState.Expand) }
    LaunchedEffect(key1 = state) {
        when (state) {
            is AppBarState.Expand -> {
                expand(true)
            }
            is AppBarState.PreExpand -> {
                expand(false)
                lazyListState.stopScroll(scrollPriority = MutatePriority.UserInput)
                lazyListState.scrollToItem(0)
            }
            is AppBarState.Collapse -> {
                expand(false)
            }
        }
    }
val nestedScrollState = object: NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        Log.i("MainActivity", "onPreScroll: consumed = $consumed available = $available, source = $source, state = $state")
        val isMovingDown = when{
            available.y.toInt() > 0 -> false
            consumed.y.toInt() < 0 -> true
            consumed.y.toInt() > 0 -> false
            else -> return Offset.Zero
        }
            state = state.modify(isMovingDown, lazyListState.isStart())
            return Offset.Zero
    }
}
    return this.nestedScroll(nestedScrollState)
}

sealed class AppBarState{
    object Expand : AppBarState()
    object PreExpand : AppBarState()
    object Collapse : AppBarState()

    fun AppBarState.modify(isMovingDown: Boolean, isStart: Boolean): AppBarState{
       return when(this){
            is Expand ->{
                if(isMovingDown) PreExpand
                else Expand
            }
            is PreExpand ->{
                if(isMovingDown) Collapse
                else Expand
            }
            is Collapse ->{
                if(isStart && !isMovingDown) PreExpand
                else Collapse
            }
        }
    }
}

fun LazyListState.isStart(): Boolean {
   return this.firstVisibleItemIndex == 0 && this.firstVisibleItemScrollOffset == 0
}



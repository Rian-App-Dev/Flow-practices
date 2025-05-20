package com.example.flowspractices

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowspractices.ui.theme.FlowsPracticesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class MainActivity : ComponentActivity() {
    val channel = Channel<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FlowsPracticesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                }
            }
        }
//        CoroutineScope(Dispatchers.Main).launch {
//            getUserName().forEach{
//                Log.d("TAG", it)
//            }
//        }
//
//        producer()
//        consumer()
//        GlobalScope.launch(Dispatchers.Main) {
//            val data: Flow<Int> = producer2()
//
//            data
//                .onStart {
//                    emit(0)
//                    Log.d("TAG", "Count started")
//                }.onCompletion {
//                    emit(11)
//                    Log.d("TAG", "Count completed")
//                }.onEach {
//                    Log.d("TAG", "${it + 10}")
//                }.collect {
//                    Log.d("TAG", it.toString())
//                }
//        }
//        GlobalScope.launch(Dispatchers.Main) {
//            producer3()
//                .map {
//                    it * 2
//                }
//                .filter {
//                    it <= 30
//                }
//                .collect{
//                    Log.d("TAG P2", it.toString())
//                    //Log.d("TAG P2", "$it")
//                }
//        }
//        GlobalScope.launch(Dispatchers.Main) {
//            producer2()
//                .flowOn(Dispatchers.IO)
//                .collect {
//                Log.d("TAG", "Collector Thread-${Thread.currentThread().name}")
//            }
//        }
//        GlobalScope.launch(Dispatchers.Main) {
//            val result = sharedFlowProducer()
//            result.collect {
//                Log.d("TAG", "Collector-1 value = $it")
//            }
//        }
//        GlobalScope.launch(Dispatchers.Main) {
//            val result = sharedFlowProducer()
//            delay(2500)
//            result.collect {
//                Log.d("TAG", "Collector-2 value = $it")
//            }
//        }
        GlobalScope.launch(Dispatchers.Main) {
            val result = sharedFlowProducer2()
            result.collect{
                Log.d("TAG", "Collector-1 value = $it")
                Log.d("TAG", Thread.currentThread().name)
            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            val result = sharedFlowProducer2()
            delay(1500)
            result
                .flowOn(Dispatchers.IO)
                .collect{
                    Log.d("TAG", "Collector-2 value $it")
                    Log.d("TAG", Thread.currentThread().name)
                }
        }

    }
    //-----------------------------------------------------------------------------------

    suspend fun getUserName(): List<String> {
        val list = mutableListOf<String>()
        list.add(getUser(1))
        list.add(getUser(2))
        list.add(getUser(3))
        return list
    }

    suspend fun getUser(id: Int): String {
        delay(3000)
        return "userId $id"
    }

    fun producer() {
        CoroutineScope(Dispatchers.Main).launch {
            channel.send(1)
            delay(1000)
            channel.send(2)
            delay(1000)
            channel.send(3)
        }
    }

    fun consumer() {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("TAG", channel.receive().toString())
            Log.d("TAG", channel.receive().toString())
            Log.d("TAG", channel.receive().toString())
        }
    }

    fun producer2(): Flow<Int> {

        var receive: Flow<Int>? = null
        receive = flow<Int> {
//            withContext(Dispatchers.IO){
//                Error show korbe. karon flow j context a create kora hoise er vitor er code sei context ei run hobe.
//                Context switch korar jonno flowOn kaje nei. R flowOn collect er call kora hoi.
//            }
            val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            list.forEach {
                delay(1000)
                Log.d("TAG", "Emitter Thread-${Thread.currentThread().name}")
                emit(it)
            }

        }
        return receive
    }

    fun producer3(): Flow<Int> {
        return flow {
            val list = listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
            list.forEach {
                emit(it)
                delay(1200)
            }
        }
    }

    fun sharedFlowProducer(): Flow<Int> {
        val sharedFlow = MutableSharedFlow<Int>(1)
        GlobalScope.launch {
            val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            list.forEach {
                sharedFlow.emit(it)
                delay(1000)
            }
        }
        return sharedFlow
    }
    fun sharedFlowProducer2(): Flow<Int> {
        val sharedFlow = MutableSharedFlow<Int>()
        GlobalScope.launch() {
            val list = listOf(1,2,3,4,5)
            list.forEach {
                sharedFlow.emit(it)
                Log.d("TAG", "Emitter Thread-${Thread.currentThread().name}")
                delay(1000)
            }
        }

        return sharedFlow
    }
}


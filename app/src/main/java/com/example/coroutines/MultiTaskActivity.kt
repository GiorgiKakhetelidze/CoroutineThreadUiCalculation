package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coroutines.databinding.ActivityMultiTaskBinding
import kotlinx.coroutines.*
import java.math.BigInteger
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class MultiTaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityMultiTaskBinding
    private val data = (0..50).map { Random.nextInt(3000, 5000) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.uiThreadBtn.setOnClickListener { startUiCalculation() }
        binding.threadBtn.setOnClickListener { startThreadCalculation() }
        binding.coroutineBtn.setOnClickListener { startCoroutineCalculation() }
    }

    private fun startUiCalculation() {
        var sum: Int
        val time = measureTimeMillis {
            sum = data.map { calculateFactorial(it) }.sum()
        }

        setResult(sum, time)
    }

    private fun startThreadCalculation() {
        thread {
            var sum = 0
            val time = measureTimeMillis {
                val threads = data.map {
                    thread {
                        sum += calculateFactorial(it)
                    }
                }
                threads.forEach {
                    it.join()
                }
            }
            runOnUiThread { setResult(sum, time) }
        }
    }

    private fun startCoroutineCalculation() {

        GlobalScope.launch {
            var sum = 0
            val time = measureTimeMillis {
                sum = data.map {
                    async { calculateFactorial(it) }
                }.awaitAll().sum()
            }
            runOnUiThread {
                setResult(sum, time)
            }
        }
    }

    private fun setResult(sum: Int, time: Long) {
        binding.result.text = sum.toString()
        binding.time.text = time.toString()
    }

    private fun calculateFactorial(number: Int): Int {
        Log.d("inFactorial", Thread.currentThread().name)
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        }

        return factorial.toString().toCharArray().first().toInt()
    }

}
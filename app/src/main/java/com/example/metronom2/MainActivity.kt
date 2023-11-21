package com.example.metronom2

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.slider.Slider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    //parametry
    private val SAMPLE_DURATION = 14 //ms
    private val UP_FREQ = 0.6F
    private val DOWN_FREQ = 0.3F
    private var BPM = 120

    //widgety
    lateinit var _bpmslider: Slider
    lateinit var _currentbpm: TextView
    lateinit var _mainTitle: TextView

    //1 cwiartka to 1 uderzenie

    //pomocnicze
    private var isPlaying = false
    var metronomeCounter = 0

    //dzwiekowe
    private var beat: SoundPool = SoundPool(1, 3, 0)
    private var beat_stream: Int= 0

    //kolory
    var colorMain: Int = 0
    var colorBlink: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //kolory
        colorMain = resources.getColor(R.color.orange)
        colorBlink = resources.getColor(R.color.white)

        //ustawienia
        val settingsManager = SettingsManager(this)
        BPM = settingsManager.getSetting("BPM", "150").toInt()
        println(BPM)

        //jeszcze raz widgety
        _mainTitle = findViewById(R.id.main_title)
        _bpmslider = findViewById(R.id.bpmSlider)
        _currentbpm = findViewById(R.id.currentBPM)

        _bpmslider.value = BPM.toFloat()
        _currentbpm.setText(BPM.toString())

        _bpmslider.addOnChangeListener{ slider, value, fromUser ->
            //println(slider.getValue())
            BPM = slider.getValue().toInt()
            _currentbpm.setText(slider.getValue().toInt().toString())
            settingsManager.saveSetting("BPM", slider.getValue().toInt().toString())
        }

    }

    fun playButtonClick(view: View){
        if(!isPlaying){
            startMetronome(view.context)
        }
        else{
            stopMetronome()
        }
        return
    }

    fun startMetronome(context: Context){
        isPlaying = true
        val beat_id = beat.load(context, R.raw.beat2, 1)

        val delay_down = ((60000 - BPM*SAMPLE_DURATION*DOWN_FREQ)/(BPM-1)).toLong()
        val delay_up = (SAMPLE_DURATION*DOWN_FREQ - SAMPLE_DURATION*UP_FREQ + delay_down).toLong()
        var delay: Long
        var rate: Float

        metronomeCounter = 0

        CoroutineScope(Dispatchers.IO).launch {
            while(isPlaying){
                rate = 0.5F
                delay = delay_down

                if(metronomeCounter==0){
                    rate = 0.8F
                    delay = delay_up

                    //glowne logo na gorze mryga na akcencie
                    _mainTitle.setTextColor(colorBlink)
                }
                beat_stream = beat.play(beat_id, 1.0F, 1.0F, 0, 0,rate)

                //przez to ze mryga to musimy tez korygowac na domyslny kolor
                _mainTitle.setTextColor(colorMain)

                Thread.sleep(delay)
                metronomeCounter++; metronomeCounter%=4
            }
        }
    }

    fun stopMetronome(){
        isPlaying = false
        beat.stop(beat_stream)
        metronomeCounter = 0
    }
}
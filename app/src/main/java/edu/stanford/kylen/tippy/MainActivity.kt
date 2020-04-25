package edu.stanford.kylen.tippy

import android.animation.ArgbEvaluator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15;
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDesc(INITIAL_TIP_PERCENT)
        setSplitCheckVisibility(false)

        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTipPercent.text = "$progress%"
                computeTipAndTotal()
                updateTipDesc(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        } )

        etBase.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        etNumPeople.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        cbSplitCheck.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                setSplitCheckVisibility(isChecked)
            }
        })
    }

    private fun setSplitCheckVisibility(visible: Boolean) {
        var set = INVISIBLE
        if(visible) {
            set = VISIBLE
        }

        tvNumPeopleLabel.setVisibility(set)
        etNumPeople.setVisibility(set)
        tvTotalPerPerson.setVisibility(set)
        tvTotalPerPersonLabel.setVisibility(set)
        tvPeopleEmojis.setVisibility(set)
    }

    private fun updateTipDesc(tipPercent: Int) {
        val tipDesc : String
        when(tipPercent) {
            in 0..9 -> tipDesc = "Poor"
            in 10..14 -> tipDesc = "Acceptable"
            in 15..19 -> tipDesc = "Good"
            in 20..24 -> tipDesc = "Great"
            else -> tipDesc = "Amazing"
        }

        tvTipDesc.text = tipDesc

        val color = ArgbEvaluator().evaluate(tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int

        tvTipDesc.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if(etBase.text.isEmpty()) {
            tvTipAmount.text = "";
            tvTotalAmount.text = "";
            tvTotalPerPerson.text = "";
            tvPeopleEmojis.text = "";
            return;
        }


        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = seekBarTip.progress

        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount

        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)

        // Calculate total per person
        if(!etNumPeople.text.isEmpty() && !(etNumPeople.text.toString() == "0")) {
            val numPeople = etNumPeople.text.toString().toFloat()
            tvTotalPerPerson.text = "%.2f".format(totalAmount / numPeople)

            val emoji : String
            when(tipPercent) {
                in 0..9 ->  emoji = "\uD83D\uDE10" // 😐
                in 10..14 -> emoji = "\uD83D\uDE42" // 🙂
                in 15..19 -> emoji = "\uD83D\uDE00" // 😀
                in 20..24 -> emoji = "\uD83D\uDE04" // 😄
                else -> emoji = "\uD83E\uDD29" // 🤩
            }

            var emojis = "";
            for(i in 1..numPeople.toInt()) {
                emojis += emoji
            }
            tvPeopleEmojis.text = emojis
        } else {
            tvTotalPerPerson.text = "";
            tvPeopleEmojis.text = "";
        }
    }
}

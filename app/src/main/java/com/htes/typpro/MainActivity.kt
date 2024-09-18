package com.htes.typpro

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT =  15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var roundUp: Button
    private lateinit var roundDown: Button
    private lateinit var seekBarSplit: SeekBar
    private lateinit var tvSplitDescription: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        roundUp = findViewById(R.id.roundUp)
        roundDown = findViewById(R.id.roundDown)
        seekBarSplit = findViewById(R.id.seekBarSplit)
        tvSplitDescription = findViewById(R.id.tvSplitDescription)

        tvTotalAmount.text = "$0.00"
        tvTipAmount.text = "$0.00"
        tvSplitDescription.text = "1"
        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress" )
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "afterTextChanged $p0")
                computeTipAndTotal()
            }

        })
        seekBarSplit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress" )
                val result = progress + 1
                tvSplitDescription.text = "$result"
                computeTipAndTotal()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        roundUp.setOnClickListener {
            computeRoundUp()
        }

        roundDown.setOnClickListener {
            computeRoundDown()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Fair"
            in 15.. 19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.worst),
            ContextCompat.getColor(this, R.color.best)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun updateSplitDescription(splitCount: Int) {
        val splitDescription = splitCount
    }

    private fun computeTipAndTotal() {
        val splitCount = seekBarSplit.progress + 1

        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = "$0.00"
            if (splitCount > 1) {
                tvTotalAmount.text = "$0.00  Per Person"

            }
            else {
                tvTotalAmount.text = "$0.00"
            }
            return
        }

        var baseAmount = 0.0
        if (etBaseAmount.text.startsWith('.')) {
            baseAmount = ("0" + etBaseAmount.text.toString()).toDouble()
        }
        else {
            baseAmount = etBaseAmount.text.toString().toDouble()
        }
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = (baseAmount + tipAmount) / splitCount
        tvTipAmount.text = "$%.2f".format(tipAmount)

        if (splitCount > 1) {
            tvTotalAmount.text = "$%.2f  Per Person".format(totalAmount)

        }
        else {
            tvTotalAmount.text = "$%.2f".format(totalAmount)
        }
    }

    private fun computeRoundUp() {
        val splitCount = seekBarSplit.progress + 1

        if (etBaseAmount.text.isEmpty()) {
            if (splitCount > 1) {
                tvTipAmount.text = "$0.00"
                tvTotalAmount.text = "$0.00  Per Person"
                return
            }
            else {
                tvTipAmount.text = "$0.00"
                tvTotalAmount.text = "$0.00"
                return
            }
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress

        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = (baseAmount + tipAmount) / splitCount

        val totalRem = 1.0 - totalAmount % 1

        if ((baseAmount % 1).equals(0.0)) {
            return
        }

        if (splitCount > 1) {
            tvTipAmount.text = "$%.2f".format(tipAmount + totalRem * splitCount)
            tvTotalAmount.text = "$%.2f  Per Person".format(totalAmount + totalRem)
        }
        else {
            tvTipAmount.text = "$%.2f".format(tipAmount + totalRem)
            tvTotalAmount.text = "$%.2f".format(totalAmount + totalRem)
        }
    }

    private fun computeRoundDown() {
        val splitCount = seekBarSplit.progress + 1

        if (etBaseAmount.text.isEmpty()) {
            if (splitCount > 1) {
                tvTipAmount.text = "$0.00"
                tvTotalAmount.text = "$0.00  Per Person"
                return
            }
            else {
                tvTipAmount.text = "$0.00"
                tvTotalAmount.text = "$0.00"
                return
            }

        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress

        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = (baseAmount + tipAmount) / splitCount

        val totalRem = totalAmount % 1

        if (tipAmount - totalRem * splitCount < 0) {
            return
        }

        if (splitCount > 1) {
            tvTipAmount.text = "$%.2f".format(tipAmount - totalRem  * splitCount)
            tvTotalAmount.text = "$%.2f  Per Person".format(totalAmount - totalRem)
        }
        else {
            tvTipAmount.text = "$%.2f".format(tipAmount - totalRem)
            tvTotalAmount.text = "$%.2f".format(totalAmount - totalRem)
        }
    }
}
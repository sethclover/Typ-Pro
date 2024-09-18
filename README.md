# Typ Pro - Tip Calculator Project
For this project, I aimed to gain valuable experience in `Android Studio` and `Kotlin` to create an appealing and effective tip
calculator app for Android devices. This fully functional app is uploaded to the Google Play Store, where it is available for<br>
closed-access download.

# 1.0 **Interface**

### 1.1 **Base:**
  - Utilizes an `EditText` window for entering the base amount for the bill in dollars

### 1.2 **Tip:**
  - Shows a `Seekbar` to adjust the tip percentage between 0-30%, displaying the value below
  - Above the seeker, text is shown to indicate the value of the tip, ranging from *Poor* to *Amazing*

    ```Kotlin
    val tipDescription = when (tipPercent) {
        in 0..9 -> "Poor"
        in 10..14 -> "Fair"
        in 15.. 19 -> "Good"
        in 20..24 -> "Great"
        else -> "Amazing"
    }
    tvTipDescription.text = tipDescription
    ```

  - Implements an ARGB Evaluator to automatically and smoothly change the color of the above text to match the value of the tip, between <br>
    Red (*Poor*) and Green (*Amazing*)

    ```Kotlin
    val color = ArgbEvaluator().evaluate(
        tipPercent.toFloat() / seekBarTip.max,
        ContextCompat.getColor(this, R.color.worst),
        ContextCompat.getColor(this, R.color.best)
    ) as Int
    tvTipDescription.setTextColor(color)
    ```
    
### 1.3 **Split:**
  - Utilizes a `SeekBar` to allow splitting the bill between 1 and 10 people

### 1.4 **Round Up** 👆
  - Displays a `Button` under the total amount that rounds up the bill

### 1.5 **Round Down** 👇
  - Similarly displays a `Button` under the total amount that instead rounds the bill down

### 1.6 **Footer**
  - Small print showing that it was created by Seth Clover (me!)

# 2.0 **Calculation**

### 2.1 **Tip:**
  - Pulls in the current values for the base amount and tip percentage to simultaneously calculate the current tip and total values in dollars
  - Immediately updates upon change of base amount and tip percentage values

### 2.2 **Total:**
  - Uses current values of base amount, tip percentage, and split count to display the total bill in dollars
  - In the instance that either base amount, tip percentage, or split is changed, updates immediately
  - If the bill is being split between more than one person, it displays the total on a per-person basis
    
    ```Kotlin
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
    ```

### 2.3 **Round Up** 👆
  - Rounds the total bill up to the nearest dollar and updates the tip accordingly
  - If the amount of people splitting the bill is greater than one, it will round to the nearest dollar for the per-person price

    ```Kotlin
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
    ```

### 2.4 **Round Down** 👇
  - Rounds the total bill down to the nearest dollar and updates the tip
  - Only completes the rounding if the tip will not be a non-negative number
  - For splitting the bill between multiple people, rounds the total down for the per-person prices
    
    ```Kotlin
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
    ```

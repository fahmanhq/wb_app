package android.template.feature.weighbridge.ui

import kotlin.text.format
import java.text.DecimalFormat

class WeightFormatter {

    companion object {
        private const val KILOGRAMS_THRESHOLD = 1000.0
        private const val KILOGRAMS_UNIT = "kg"
        private const val TONS_UNIT = "tons"

        private val decimalFormat = DecimalFormat("#,###.###")
    }

    fun format(weight: Double): String {
        return if (weight >= KILOGRAMS_THRESHOLD) {
            val tons = weight / KILOGRAMS_THRESHOLD
            decimalFormat.format(tons) + " " + TONS_UNIT
        } else {
            decimalFormat.format(weight) + " " + KILOGRAMS_UNIT
        }
    }
}
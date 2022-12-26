package day25

import kotlin.math.pow

class FiveBase  {

    companion object {
        fun toLong(s: String): Long {
            var positions = s.length - 1

            var value = 0L

            for (x in s) {
                val x2 = if (x == '=') -2 else if (x == '-') -1 else x.digitToInt().toLong()

                value += x2 * (5).toDouble().pow(positions).toLong()

                positions --
            }

            return value
        }

        fun toFiveBase(l: Long): String {
            val quotient = l / 5
            val reminder = l % 5

            if (quotient == 0L) {
                if (reminder == 4L) return "1-"
                else if (reminder == 3L) return "1="
                else return reminder.toString()
            }

            if (reminder == 4L) {
                return toFiveBase(quotient + 1) +"-"
            } else if (reminder == 3L) {
                return toFiveBase(quotient + 1) +"="
            } else {
                return toFiveBase(quotient) + reminder
            }
        }
    }
}

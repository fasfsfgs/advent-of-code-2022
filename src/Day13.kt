fun main() {
    fun part1(input: String): Int {
        return input
            .split("\n\n")
            .flatMap { it.lines() }
            .map { it.toPacket() }
            .chunked(2)
            .map { Pair(it.first().list!!, it[1].list!!) }
            .map { it.isRightOrder() }
            .mapIndexedNotNull { index, isRightOrder ->
                if (isRightOrder != false) index + 1 else null
            }
            .sum()
    }

    fun part2(input: String): Int {
        val strPackets = input
            .split("\n\n")
            .flatMap { it.lines() }

        val strPacketsWithDividers = strPackets + "[[2]]" + "[[6]]"

        val orderedPackets = strPacketsWithDividers
            .map { it.toPacket().list!! }
            .sortedWith { left, right ->
                val isRightOrder = Pair(left, right).isRightOrder()
                if (isRightOrder != false) -1 else 1
            }

        val firstDivider = "[[2]]".toPacket().list!!
        val firstDividerIndex = orderedPackets.indexOf(firstDivider) + 1

        val secondDivider = "[[6]]".toPacket().list!!
        val secondDividerIndex = orderedPackets.indexOf(secondDivider) + 1

        return firstDividerIndex * secondDividerIndex
    }

    val testInput = readInputAsString("Day13_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInputAsString("Day13")
    println(part1(input))
    println(part2(input))
}

data class Packet(val number: Int?, val list: List<Packet>?) {
    constructor(number: Int) : this(number, null)
    constructor(list: List<Packet>) : this(null, list)
}

fun Pair<List<Packet>, List<Packet>>.isRightOrder(): Boolean? {
    if (first.isEmpty() && second.isEmpty()) return null
    if (first.isEmpty()) return true
    if (second.isEmpty()) return false

    for (i in first.indices) {
        if (second.lastIndex < i) return false // iterating the first list and the second is already over

        // handling numbers
        val firstNumber = first[i].number
        val secondNumber = second[i].number
        if (firstNumber != null && secondNumber != null) {
            if (firstNumber != secondNumber) return firstNumber < secondNumber
            else continue
        }

        // handling lists
        val firstList = first[i].list
        val secondList = second[i].list
        if (firstList != null && secondList != null) {
            val isRightNestedOrder = Pair(firstList, secondList).isRightOrder()
            if (isRightNestedOrder != null) return isRightNestedOrder
            else continue
        }

        // handling different types
        if (firstNumber != null) {
            val isRightNestedOrder = Pair(listOf(Packet(firstNumber)), secondList!!).isRightOrder()
            if (isRightNestedOrder != null) return isRightNestedOrder
            else continue
        }

        val isRightNestedOrder = Pair(firstList!!, listOf(Packet(secondNumber!!))).isRightOrder()
        if (isRightNestedOrder != null) return isRightNestedOrder
        else continue
    }

    return null
}

fun String.toPacket(): Packet {
    val data = mutableListOf<Packet>()
    var strToEval = removeSurrounding("[", "]") // this method always takes a list
    while (strToEval.isNotEmpty()) {
        if (strToEval.first().isDigit()) {
            val strInt = strToEval.takeWhile { it.isDigit() }
            data.add(Packet(strInt.toInt()))
            strToEval = strToEval.removePrefix(strInt).removePrefix(",")
        } else {
            var depth = -1
            var indexEndOfList = -1
            for ((index, c) in strToEval.withIndex()) {
                if (c == ']' && depth == 0) {
                    indexEndOfList = index
                    break
                }
                if (c == '[') depth++
                if (c == ']') depth--
            }
            val strList = strToEval.substring(0, indexEndOfList + 1)
            data.add(strList.toPacket())
            strToEval = strToEval.removePrefix(strList).removePrefix(",")
        }
    }
    return Packet(data)
}

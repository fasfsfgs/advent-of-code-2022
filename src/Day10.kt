fun main() {
    fun part1(input: List<String>): Int {
        val registerValues = input.toRegisterValues()

        return listOf(20, 60, 100, 140, 180, 220)
            .sumOf { registerValues.signalStrength(it) }
    }

    fun part2(input: List<String>) {
        input.toRegisterValues()
            .chunked(40)
            .map { crtLine ->
                crtLine.mapIndexed { clock, register ->
                    val sprite = (register - 1..register + 1)
                    if (clock in sprite) "#" else "."
                }
            }
            .map { it.joinToString("") }
            .forEach { println(it) }
    }

    val testInput00 = readInput("Day10_test00")
//    println(testInput00.toRegisterValues())

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13_140)
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}

fun List<String>.toRegisterValues(): List<Int> {
    var registerValue = 1

    val computedValues = flatMap {
        val splitInstruction = it.split(" ")

        if (splitInstruction[0] == "noop") {
            listOf(registerValue)
        } else {
            val initialRegisterValue = registerValue.toInt()
            registerValue += splitInstruction[1].toInt()
            listOf(initialRegisterValue, registerValue)
        }
    }

    var mutComputedValues = computedValues.toMutableList()
    mutComputedValues.add(0, 1)
    return mutComputedValues
}

fun List<Int>.signalStrength(i: Int): Int = i * this[i - 1]

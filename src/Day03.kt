data class Rucksack(val firstCompartment: String, val secondCompartment: String)

data class RucksackGroup(val firstRucksack: String, val secondRucksack: String, val thirdRucksack: String)

fun createRucksack(allTypes: String): Rucksack {
    val firstCompartment = allTypes.take(allTypes.length / 2)
    val secondCompartment = allTypes.takeLast(allTypes.length / 2)
    return Rucksack(firstCompartment, secondCompartment)
}

fun getCommonType(rucksack: Rucksack): Int {
    val commonChar = rucksack.firstCompartment.find { rucksack.secondCompartment.contains(it) }
        ?: throw IllegalArgumentException("This rucksack (${rucksack}) must contain a common type")

    return charToPriority(commonChar)
}

fun charToPriority(char: Char): Int {
    return if (char.isUpperCase())
        char.code - 'A'.code + 27
    else
        char.code - 'a'.code + 1
}

fun getCommonType(rucksackGroup: RucksackGroup): Int {
    val commonChar = rucksackGroup.firstRucksack
        .find { rucksackGroup.secondRucksack.contains(it) && rucksackGroup.thirdRucksack.contains(it) }
        ?: throw IllegalArgumentException("This rucksack group (${rucksackGroup}) must contain a common type")

    return charToPriority(commonChar)
}

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { createRucksack(it) }
            .sumOf { getCommonType(it) }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3)
            .map { RucksackGroup(it[0], it[1], it[2]) }
            .sumOf { getCommonType(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

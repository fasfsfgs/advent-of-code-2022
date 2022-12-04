fun checkOverlap(pair: Pair<IntRange, IntRange>, completely: Boolean = true): Boolean {
    val elf1 = pair.first
    val elf2 = pair.second
    return if (completely) {
        val smallest = minOf(elf1.last - elf1.first + 1, elf2.last - elf2.first + 1)
        smallest == elf1.intersect(elf2).size
    } else {
        elf1.intersect(elf2).isNotEmpty()
    }
}

fun strToElfPair(strInput: String): Pair<IntRange, IntRange> {
    val (firstElf, secondElf) = strInput.split(',')
        .map { elf ->
            val (start, end) = elf.split('-').map { it.toInt() }
            start..end
        }
    return Pair(firstElf, secondElf)
}

fun main() {
    fun part1(input: List<String>): Int {
        val redundantPairs = input
            .map { strToElfPair(it) }
            .map { checkOverlap(it) }
            .filter { it }
        return redundantPairs.size
    }

    fun part2(input: List<String>): Int {
        val redundantPairs = input
            .map { strToElfPair(it) }
            .map { checkOverlap(it, false) }
            .filter { it }
        return redundantPairs.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

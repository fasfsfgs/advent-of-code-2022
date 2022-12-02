fun getElves(input: List<String>):List<Int> {
    return input
        .joinToString { it.ifBlank { "*" } }
        .split('*')
        .map { it.split(',') }
        .map { getIntList(it) }
        .map { it.sum() }
}

fun getIntList(strList: List<String>): List<Int> {
    return strList
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toInt() }
}

fun main() {
    fun part1(input: List<String>): Int {
        return getElves(input)
            .max()
    }

    fun part2(input: List<String>): Int {
        return getElves(input)
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

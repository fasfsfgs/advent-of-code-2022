fun main() {
    fun part1(input: List<String>): Int {
        val verticalSlice = input.toLines().toVerticalSlice()
        return releaseSand(verticalSlice)
    }

    fun part2(input: List<String>): Int {
        val verticalSlice = input.toLines().toVerticalSlice()
        verticalSlice.addFloor()

        return releaseSand(verticalSlice)
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

data class LineCoords(val start: Pair<Int, Int>, val end: Pair<Int, Int>)

fun List<String>.toLines() = flatMap { it.toLines() }

fun String.toLines() = split(" -> ")
    .map {
        val (xLine, yLine) = it.split(",")
        Pair(xLine.toInt(), yLine.toInt())
    }
    .windowed(2)
    .map { LineCoords(it[0], it[1]) }

data class VerticalSlice(val scan: MutableMap<Pair<Int, Int>, Structure>, var floorY: Int? = null) {

    fun addFloor() {
        floorY = scan.keys.map { it.second }.max() + 2
    }

}

fun List<LineCoords>.toVerticalSlice(): VerticalSlice {
    val scan = mutableMapOf<Pair<Int, Int>, Structure>()

    forEach {
        if (it.start.first == it.end.first) {
            val x = it.start.first
            val minY = it.start.second.coerceAtMost(it.end.second)
            val maxY = it.start.second.coerceAtLeast(it.end.second)
            for (y in minY..maxY) scan[Pair(x, y)] = Structure.ROCK
        } else {
            val y = it.start.second
            val minX = it.start.first.coerceAtMost(it.end.first)
            val maxX = it.start.first.coerceAtLeast(it.end.first)
            for (x in minX..maxX) scan[Pair(x, y)] = Structure.ROCK
        }
    }

    return VerticalSlice(scan)
}

enum class Structure(val representation: Char) {
    SAND('o'),
    ROCK('#'),
    AIR('.')
}

fun releaseSand(verticalSlice: VerticalSlice): Int {
    var restedSand = 0

    var sandOverflown = false
    while (!sandOverflown) {
        val newSand = dropNewSand(verticalSlice) ?: return restedSand

        verticalSlice.scan[newSand] = Structure.SAND
        restedSand++

        if (newSand == Pair(500, 0)) return restedSand
    }

    return restedSand
}

fun dropNewSand(verticalSlice: VerticalSlice): Pair<Int, Int>? {
    var sand = Pair(500, 0)

    val maxY = verticalSlice.scan.keys.map { it.second }.max()

    var sandRested = false
    while (!sandRested) {
        if (verticalSlice.floorY == null && sand.second == maxY) return null

        if (sand.second + 1 == verticalSlice.floorY) {
            sandRested = true
            continue
        }

        if (verticalSlice.scan[Pair(sand.first, sand.second + 1)] == null) {
            sand = sand.copy(second = sand.second + 1)
            continue
        }
        if (verticalSlice.scan[Pair(sand.first - 1, sand.second + 1)] == null) {
            sand = sand.copy(sand.first - 1, sand.second + 1)
            continue
        }
        if (verticalSlice.scan[Pair(sand.first + 1, sand.second + 1)] == null) {
            sand = sand.copy(sand.first + 1, sand.second + 1)
            continue
        }
        sandRested = true
    }

    return sand
}

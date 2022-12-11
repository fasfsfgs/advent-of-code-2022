import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val initialHead = Knot()

        val headPath = input.toMotions()
            .fold(listOf(initialHead)) { acc, motion -> acc.plus(acc.last().move(motion)) }

        return headPath.follower()
            .distinct()
            .size
    }

    fun part2(input: List<String>): Int {
        val initialHead = Knot()

        val headPath = input.toMotions()
            .fold(listOf(initialHead)) { acc, motion -> acc.plus(acc.last().move(motion)) }

//        val paths = mutableListOf<List<Knot>>()

        val tailPath = List(9) { it }
            .fold(headPath) { acc, _ ->
                val follower = acc.follower()
//                paths.add(follower)
                follower
            }

//        paths.add(0, headPath)
//        visualize(paths)

        return tailPath
            .distinct()
            .size
    }

    val testInput01 = readInput("Day09_test01")
    check(part1(testInput01) == 13)
    check(part2(testInput01) == 1)

    val testInput02 = readInput("Day09_test02")
    check(part2(testInput02) == 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

fun List<String>.toMotions(): List<String> = flatMap {
    val (direction, strTimes) = it.split(" ")
    List(strTimes.toInt()) { direction }
}

fun List<Knot>.follower() =
    fold(listOf<Knot>()) { acc, knot ->
        if (acc.isEmpty()) listOf(knot) else acc.plus(acc.last().follow(knot))
    }

data class Knot(val x: Int = 0, val y: Int = 0) {

    fun move(motion: String): Knot = when (motion) {
        "U" -> Knot(x, y + 1)
        "D" -> Knot(x, y - 1)
        "L" -> Knot(x - 1, y)
        "R" -> Knot(x + 1, y)
        else -> error("unknown motion ($motion)")
    }

    fun follow(head: Knot): Knot = when {
        abs(head.x - x) <= 1 && abs(head.y - y) <= 1 -> copy()

        x == head.x && head.y == y + 2 -> copy(y = y + 1)
        x == head.x && head.y == y - 2 -> copy(y = y - 1)
        y == head.y && head.x == x + 2 -> copy(x = x + 1)
        y == head.y && head.x == x - 2 -> copy(x = x - 1)

        abs(head.x - x) == 1 && head.y == y + 2 -> head.copy(y = y + 1)
        abs(head.x - x) == 1 && head.y == y - 2 -> head.copy(y = y - 1)
        abs(head.y - y) == 1 && head.x == x + 2 -> head.copy(x = x + 1)
        abs(head.y - y) == 1 && head.x == x - 2 -> head.copy(x = x - 1)

        head.x == x + 2 && head.y == y + 2 -> copy(x = x + 1, y = y + 1)
        head.x == x + 2 && head.y == y - 2 -> copy(x = x + 1, y = y - 1)
        head.x == x - 2 && head.y == y + 2 -> copy(x = x - 1, y = y + 1)
        head.x == x - 2 && head.y == y - 2 -> copy(x = x - 1, y = y - 1)

        else -> error("$this can't follow $head")
    }

}

fun visualize(paths: List<List<Knot>>) {
    val numberOfMotions = paths.first().size

    val flattenKnots = paths.flatten()
    val maxX = flattenKnots.map { it.x }.max()
    val maxY = flattenKnots.map { it.y }.max()

    for (i in 0 until numberOfMotions) {
        val state = paths.map { it[i] }
        visualize(state, maxX, maxY, i)
    }
}

fun visualize(state: List<Knot>, maxX: Int, maxY: Int, iteration: Int = 0) {
    println("ITERATION $iteration")
    val visualArray = MutableList(maxY + 1) { MutableList(maxX + 1) { "." } }

    for (i in state.lastIndex downTo 0) {
        val knot = state[i]
        visualArray[knot.y][knot.x] = i.toString()
    }

    visualArray
        .reversed()
        .forEach { println(it) }

    println()
}

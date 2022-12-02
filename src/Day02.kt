import java.lang.IllegalArgumentException

enum class Shape(val points: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3),
}

fun getOpponentsShape(strShape: String): Shape {
    return when (strShape) {
        "A" -> Shape.ROCK
        "B" -> Shape.PAPER
        "C" -> Shape.SCISSORS
        else -> throw IllegalArgumentException()
    }
}

fun getMyShape(strShape: String): Shape {
    return when (strShape) {
        "X" -> Shape.ROCK
        "Y" -> Shape.PAPER
        "Z" -> Shape.SCISSORS
        else -> throw IllegalArgumentException()
    }
}

class Play(private val opponentsShape: Shape, val myShape: Shape) {

    fun getResult(): Int {
        return when {
            opponentsShape == myShape -> 3
            myShape == Shape.ROCK && opponentsShape == Shape.SCISSORS -> 6
            myShape == Shape.SCISSORS && opponentsShape == Shape.PAPER -> 6
            myShape == Shape.PAPER && opponentsShape == Shape.ROCK -> 6
            else -> 0
        }
    }

}

fun getMyShape(opponentsShape: Shape, strOutcome: String): Shape {
    val outcome = when (strOutcome) {
        "X" -> 0
        "Y" -> 3
        "Z" -> 6
        else -> throw IllegalArgumentException()
    }

    return Shape.values()
        .find { Play(opponentsShape, it).getResult() == outcome } ?: throw Exception()
}

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map {
                val (strOpponent, strMe) = it.split(" ")
                Play(getOpponentsShape(strOpponent), getMyShape(strMe))
            }
            .sumOf { it.getResult() + it.myShape.points }
    }

    fun part2(input: List<String>): Int {
        return input
            .map {
                val (strOpponent, strOutcome) = it.split(" ")
                val opponentsShape = getOpponentsShape(strOpponent)
                val myShape = getMyShape(opponentsShape, strOutcome)
                Play(getOpponentsShape(strOpponent), myShape)
            }
            .sumOf { it.getResult() + it.myShape.points }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

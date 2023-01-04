const val CHAMBER_WIDTH = 7

fun main() {
    fun part1(input: String): Int {
        return simulateChamber(input, 2022)
    }

    fun part2(input: String): Int {
        return input.length
    }

    val testInput = readInputAsString("Day17_test")
    check(part1(testInput) == 3068)
//    check(part2(testInput) == 0)

    val input = readInputAsString("Day17")
    println(part1(input))
//    println(part2(input))

}

fun simulateChamber(input: String, numberOfRocks: Int): Int {
    val restedRocks = List(CHAMBER_WIDTH) { Pos(it, 0) }.toMutableSet()

    val jetProducer = JetProducer.build(input)
    val rockProducer = RockProducer.build()

    repeat(numberOfRocks) {
        dropRock(restedRocks, rockProducer.getNext(), jetProducer)
    }

    return restedRocks.highest()
}

fun dropRock(restedRocks: MutableSet<Pos>, rock: Rock, jetProducer: JetProducer) {
    var rockFalling = rock.movedX(2).movedY(restedRocks.highest() + 4)

    var rockRested = false
    while (!rockRested) {
        val jet = jetProducer.getNext()
        rockFalling = rockFalling.movedX(restedRocks, jet)

        if (rockFalling.canFall(restedRocks))
            rockFalling = rockFalling.movedY(-1)
        else
            rockRested = true
    }

    restedRocks.addAll(rockFalling.pieces)
}

fun Set<Pos>.highest() = maxOf { it.y }

enum class Jet { LEFT, RIGHT }

class JetProducer(private val jets: List<Jet>) {

    private var nextIndex = 0

    companion object {
        fun build(input: String): JetProducer {
            val jets = input.map { if (it == '>') Jet.RIGHT else Jet.LEFT }
            return JetProducer(jets)
        }
    }

    fun getNext(): Jet {
        if (nextIndex > jets.lastIndex) nextIndex = 0
        return jets[nextIndex++]
    }

}

data class Pos(val x: Int, val y: Int)

data class Rock(val pieces: Set<Pos>) {
    constructor(vararg pieces: Pos) : this(pieces.toSet())

    fun movedX(restedRocks: Set<Pos>, jet: Jet): Rock {
        val imaginaryRock = if (jet == Jet.LEFT) movedX(-1) else movedX(1)

        val isRockOutOfChamber = imaginaryRock.pieces
            .map { it.x }
            .any { it !in (0 until CHAMBER_WIDTH) }

        if (isRockOutOfChamber)
            return this

        if (imaginaryRock.pieces.intersect(restedRocks).isNotEmpty())
            return this

        return imaginaryRock
    }

    fun movedX(delta: Int): Rock {
        val newPieces = pieces.map { it.copy(x = it.x + delta) }.toSet()
        return Rock(newPieces)
    }

    fun movedY(delta: Int): Rock {
        val newPieces = pieces.map { it.copy(y = it.y + delta) }.toSet()
        return Rock(newPieces)
    }

    fun canFall(levelHeight: Set<Pos>): Boolean {
        val imaginaryRock = this.movedY(-1)
        return imaginaryRock.pieces.intersect(levelHeight).isEmpty()
    }

}

class RockProducer(private val rocks: List<Rock>) {

    private var nextIndex = 0

    constructor(vararg rocks: Rock) : this(rocks.toList())

    companion object {
        fun build(): RockProducer {
            val rock0 = Rock(
                Pos(0, 0), // ####
                Pos(1, 0),
                Pos(2, 0),
                Pos(3, 0)
            )
            val rock1 = Rock(
                Pos(1, 0), // .#.
                Pos(1, 0), // ###
                Pos(0, 1), // .#.
                Pos(1, 1),
                Pos(2, 1),
                Pos(1, 2)
            )
            val rock2 = Rock(
                Pos(0, 0), // ..#
                Pos(1, 0), // ..#
                Pos(2, 0), // ###
                Pos(2, 1),
                Pos(2, 2)
            )
            val rock3 = Rock(
                Pos(0, 0), // #
                Pos(0, 1), // #
                Pos(0, 2), // #
                Pos(0, 3)  // #
            )
            val rock4 = Rock(
                Pos(0, 0), // ##
                Pos(1, 0), // ##
                Pos(0, 1),
                Pos(1, 1)
            )

            return RockProducer(rock0, rock1, rock2, rock3, rock4)
        }
    }

    fun getNext(): Rock {
        if (nextIndex > rocks.lastIndex) nextIndex = 0
        return rocks[nextIndex++]
    }

}

fun main() {
    fun part1(input: List<String>): Int {
        val heightMap = HeightMap.fromInput(input)
        val result = findPath(heightMap) { it == heightMap.start }
        return result.getLength()
    }

    fun part2(input: List<String>): Int {
        val heightMap = HeightMap.fromInput(input)
        val result = findPath(heightMap) { heightMap.grid[it.first][it.second] == 0 }
        return result.getLength()
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
    // For this day, I watched AoC Kotlin live to learn any algorithm for searching.
}

fun findPath(heightMap: HeightMap, destinationFn: (Pair<Int, Int>) -> Boolean): Step {
    val visitedGridSquares = mutableSetOf<Pair<Int, Int>>()
    val queue = ArrayDeque<Step>()

    visitedGridSquares.add(heightMap.target)
    queue.add(Step(heightMap.target))

    while (queue.isNotEmpty()) {
        val step = queue.removeFirst()
        if (destinationFn(step.coords)) return step

        heightMap.getNeighbours(step.coords)
            .filter { !visitedGridSquares.contains(it) }
            .filter {
                val stepHeight = heightMap.grid[step.coords.first][step.coords.second]
                val neighboursHeight = heightMap.grid[it.first][it.second]
                stepHeight <= neighboursHeight + 1
            }
            .map { Step(it, step) }
            .forEach {
                visitedGridSquares.add(it.coords)
                queue.add(it)
            }
    }

    error("No paths found.")
}

data class Step(val coords: Pair<Int, Int>, val previousStep: Step? = null) {

    fun getLength(): Int {
        var pathLength = 0
        var runPath = this
        while (runPath.previousStep != null) {
            pathLength++
            runPath = runPath.previousStep!!
        }

        return pathLength
    }

}

data class HeightMap(val grid: List<List<Int>>, val start: Pair<Int, Int>, val target: Pair<Int, Int>) {

    companion object {
        fun fromInput(input: List<String>): HeightMap {
            var start: Pair<Int, Int> = -1 to -1
            var target: Pair<Int, Int> = -1 to -1

            val grid = input
                .mapIndexed { i, s ->
                    s.mapIndexed { j, c ->
                        when (c) {
                            'S' -> {
                                start = i to j
                                'a' - 'a'
                            }

                            'E' -> {
                                target = i to j
                                'z' - 'a'
                            }

                            else -> {
                                c - 'a'
                            }
                        }
                    }
                }
            return HeightMap(grid, start, target)
        }
    }

    fun getNeighbours(spot: Pair<Int, Int>): List<Pair<Int, Int>> {
        val maxI = grid.lastIndex
        val maxJ = grid[0].lastIndex
        val i = spot.first
        val j = spot.second

        val neighbours = mutableListOf<Pair<Int, Int>>()

        if (i > 0) neighbours.add(i - 1 to j)
        if (i < maxI) neighbours.add(i + 1 to j)
        if (j > 0) neighbours.add(i to j - 1)
        if (j < maxJ) neighbours.add(i to j + 1)

        return neighbours.toList()
    }

}

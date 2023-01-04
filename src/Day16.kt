fun main() {
    fun part1(input: List<String>): Int {
        val valves = input.parseReport()
        return findBestPath(valves).cumulativePressure
    }

    fun part2(input: List<String>): Int {
        val valves = input.parseReport()
        return findBestPathInPair(valves).cumulativePressure
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}

fun findBestPathInPair(valves: List<Valve>): PathInPair {
    val start = valves.find { it.name == "AA" } ?: error("start not found")
    val closedValves = valves.filter { it.flow > 0 }
    val firstPath = PathInPair(start, start, closedValves, 26)
    val possiblePaths = mutableListOf(firstPath)
    var bestPath = firstPath

    while (possiblePaths.isNotEmpty()) {
        val pathToWorkOn = possiblePaths.removeLast()

        if (pathToWorkOn.cumulativePressure > bestPath.cumulativePressure) {
            bestPath = pathToWorkOn
            println(bestPath.cumulativePressure)
        }

        if (!isItPossibleToTopBestPathInPair(bestPath, pathToWorkOn))
            continue

        val nextPossiblePaths = pathToWorkOn.nextSteps(valves)
        possiblePaths.addAll(nextPossiblePaths)
    }

    return bestPath
}

fun isItPossibleToTopBestPathInPair(bestPath: PathInPair, path: PathInPair): Boolean {
    val theoreticalCumulativePressureOfClosedValves = path.valvesToOpen.asSequence()
        .sortedByDescending { it.flow }
        .chunked(2)
        .mapIndexed { index, valves ->
            if (valves.size == 1)
                (path.timeLeft - ((index * 2) + 1)) * valves[0].flow
            else
                (path.timeLeft - ((index * 2) + 1)) * valves[0].flow + (path.timeLeft - ((index * 2) + 1)) * valves[1].flow
        }
        .filter { it > 0 }
        .sum()

    return (path.cumulativePressure + theoreticalCumulativePressureOfClosedValves) > bestPath.cumulativePressure
}

data class PathInPair(
    val pos1: Valve,
    val pos2: Valve,
    val valvesToOpen: List<Valve>,
    val timeLeft: Int,
    val cumulativePressure: Int = 0,
    val visitedValvesWithPressure: Map<Valve, Int> = mapOf(pos1 to cumulativePressure)
)

fun PathInPair.nextSteps(valves: List<Valve>): List<PathInPair> {
    if (valvesToOpen.isEmpty() || timeLeft == 0) return listOf()
    val timeLeft = timeLeft - 1

    val canOpenPos1 = valvesToOpen.any { it == pos1 }
    val canOpenPos2 = pos1 != pos2 && valvesToOpen.any { it == pos2 }

    val newPathsNoValvesOpen = pos1.destinationIds
        .flatMap { pos1Dest -> pos2.destinationIds.map { Pair(pos1Dest, it) } }
        .map { Pair(valves.findByName(it.first), valves.findByName(it.second)) }
        .filter { visitedValvesWithPressure[it.first] != cumulativePressure && visitedValvesWithPressure[it.second] != cumulativePressure }
        .map {
            copy(
                pos1 = it.first, pos2 = it.second,
                timeLeft = timeLeft,
                visitedValvesWithPressure = visitedValvesWithPressure + (it.first to cumulativePressure) + (it.second to cumulativePressure)
            )
        }

    if (!canOpenPos1 && !canOpenPos2 && pos1 == pos2)
        return newPathsNoValvesOpen.distinctBy { setOf(it.pos1, it.pos2) }

    if (!canOpenPos1 && !canOpenPos2)
        return newPathsNoValvesOpen

    var nextSteps = newPathsNoValvesOpen

    if (canOpenPos1) {
        val cumulativePressure = cumulativePressure + (pos1.flow * timeLeft)
        val newPathsOnlyValvePos1Open = pos2.destinationIds
            .map { valves.findByName(it) }
            .map {
                copy(
                    pos2 = it,
                    valvesToOpen = valvesToOpen - pos1,
                    timeLeft = timeLeft,
                    cumulativePressure = cumulativePressure,
                    visitedValvesWithPressure = visitedValvesWithPressure + (pos1 to cumulativePressure) + (pos2 to cumulativePressure)
                )
            }
        nextSteps = nextSteps + newPathsOnlyValvePos1Open
    }

    if (canOpenPos2) {
        val cumulativePressure = cumulativePressure + (pos2.flow * timeLeft)
        val newPathsOnlyValvePos2Open = pos1.destinationIds
            .map { valves.findByName(it) }
            .map {
                copy(
                    pos1 = it,
                    valvesToOpen = valvesToOpen - pos2,
                    timeLeft = timeLeft,
                    cumulativePressure = cumulativePressure,
                    visitedValvesWithPressure = visitedValvesWithPressure + (pos1 to cumulativePressure) + (pos2 to cumulativePressure)
                )
            }
        nextSteps = nextSteps + newPathsOnlyValvePos2Open
    }

    if (canOpenPos1 && canOpenPos2) {
        val cumulativePressure = cumulativePressure + (pos1.flow * timeLeft) + (pos2.flow * timeLeft)
        val bothPosOpen = copy(
            valvesToOpen = valvesToOpen - pos1 - pos2,
            timeLeft = timeLeft,
            cumulativePressure = cumulativePressure,
            visitedValvesWithPressure = visitedValvesWithPressure + (pos1 to cumulativePressure) + (pos2 to cumulativePressure)
        )

        nextSteps = nextSteps + bothPosOpen
    }

    return nextSteps
}

fun findBestPath(valves: List<Valve>): Path {
    val start = valves.find { it.name == "AA" } ?: error("start not found")
    val closedValves = valves.filter { it.flow > 0 }
    val firstPath = Path(start, closedValves, 30)
    val possiblePaths = mutableListOf(firstPath)
    var bestPath = firstPath

    while (possiblePaths.isNotEmpty()) {
        val pathToWorkOn = possiblePaths.removeLast()

        if (pathToWorkOn.cumulativePressure > bestPath.cumulativePressure)
            bestPath = pathToWorkOn

        if (!isItPossibleToTopBestPath(bestPath, pathToWorkOn))
            continue

        pathToWorkOn.nextSteps(valves)
            .forEach { possiblePaths.add(it) }
    }

    return bestPath
}

fun isItPossibleToTopBestPath(bestPath: Path, path: Path): Boolean {
    val theoreticalCumulativePressureOfClosedValves = path
        .valvesToOpen
        .sortedByDescending { it.flow }
        .mapIndexed { index, valve -> (path.timeLeft - ((index * 2) + 1)) * valve.flow }
        .filter { it > 0 }
        .sum()

    return (path.cumulativePressure + theoreticalCumulativePressureOfClosedValves) > bestPath.cumulativePressure
}

data class Path(
    val pos: Valve,
    val valvesToOpen: List<Valve>,
    val timeLeft: Int,
    val cumulativePressure: Int = 0,
    val visitedValvesWithPressure: Map<Valve, Int> = mapOf(pos to cumulativePressure)
)

fun Path.nextSteps(valves: List<Valve>): List<Path> {
    if (valvesToOpen.isEmpty() || timeLeft == 0) return listOf()
    val timeLeft = timeLeft - 1

    val openedPath = if (valvesToOpen.contains(pos)) {
        val pressure = pos.flow * timeLeft
        val cumulativePressure = cumulativePressure + pressure
        copy(
            valvesToOpen = valvesToOpen - pos,
            timeLeft = timeLeft,
            cumulativePressure = cumulativePressure,
            visitedValvesWithPressure = visitedValvesWithPressure + (pos to cumulativePressure)
        )
    } else {
        null
    }

    val nextPaths = pos.destinationIds
        .map { valves.findByName(it) }
        .filter { visitedValvesWithPressure[it] != cumulativePressure }
        .map {
            copy(
                pos = it,
                timeLeft = timeLeft,
                visitedValvesWithPressure = visitedValvesWithPressure + (it to cumulativePressure)
            )
        }

    return if (openedPath != null) {
        nextPaths + openedPath
    } else {
        nextPaths
    }
}

fun List<String>.parseReport(): List<Valve> = map {
    val name = it.substringAfter(' ').substringBefore(' ')
    val flow = it.substringAfter('=').substringBefore(';').toInt()
    val destinationIds = it.substringAfterLast("valve").substringAfter(' ').split(", ")
    Valve(name, flow, destinationIds)
}

data class Valve(val name: String, val flow: Int, val destinationIds: List<String>)

fun List<Valve>.findByName(name: String): Valve = find { it.name == name } ?: error("Valve of name $name not found.")

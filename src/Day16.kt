fun main() {
    fun part1(input: List<String>): Int {
        val valves = input.parseReport()

        val bestPath = findBestPath(valves)
        return bestPath.cumulativePressure
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 1651)
//    check(part2(testInput) == 0)

    val input = readInput("Day16")
    println(part1(input))
//    println(part2(input))
}

fun findBestPath(valves: List<Valve>): Path {
    val start = valves.find { it.name == "AA" } ?: error("start not found")

    val closedValves = valves.filter { it.flow > 0 }
    val firstPath = Path(listOf(start), closedValves, 30)

    val possiblePaths = mutableListOf(firstPath)

    var bestPath = firstPath

    while (possiblePaths.isNotEmpty()) {
        val pathToWorkOn = possiblePaths.removeLast()
        val nextPossiblePaths = pathToWorkOn.nextSteps(valves)

        val bestCompletePath = nextPossiblePaths
            .filter { it.cumulativePressure > bestPath.cumulativePressure }
            .maxByOrNull { it.cumulativePressure }

        if (bestCompletePath != null)
            bestPath = bestCompletePath

        nextPossiblePaths
            .filter { isItPossibleToTopBestPath(bestPath, it) }
            .forEach { possiblePaths.add(it) }
    }

    return bestPath
}

fun isItPossibleToTopBestPath(bestPath: Path, path: Path): Boolean {
    val theoreticalCumulativePressureOfClosedValves = path
        .valvesToOpen
        .sortedByDescending { it.flow }
        .mapIndexed { index, valve -> (path.timeLeft - (index + 1)) * valve.flow }
        .filter { it > 0 }
        .sum()

    return (path.cumulativePressure + theoreticalCumulativePressureOfClosedValves) > bestPath.cumulativePressure
}

data class Path(
    val steps: List<Valve>,
    val valvesToOpen: List<Valve>,
    val timeLeft: Int,
    val cumulativePressure: Int = 0,
    val visitedValvesWithPressure: MutableMap<Valve, Int> = mutableMapOf(steps.last() to cumulativePressure)
)

fun Path.nextSteps(valves: List<Valve>): List<Path> {
    if (valvesToOpen.isEmpty() || timeLeft == 0) return listOf()

    val timeLeft = timeLeft - 1
    val lastStep = steps.last()

    val openedPath = if (valvesToOpen.contains(lastStep)) {
        val pressure = lastStep.flow * timeLeft
        val cumulativePressure = cumulativePressure + pressure
        val openedPathVisitedValvesWithPressure = visitedValvesWithPressure.toMutableMap()
        openedPathVisitedValvesWithPressure[lastStep] = cumulativePressure
        copy(
            valvesToOpen = valvesToOpen.minus(lastStep),
            timeLeft = timeLeft,
            cumulativePressure = cumulativePressure,
            visitedValvesWithPressure = openedPathVisitedValvesWithPressure
        )
    } else {
        null
    }

    val bestValveToOpen = valvesToOpen.maxBy { it.flow }
    if (openedPath?.steps?.last() == bestValveToOpen) return listOf(openedPath)

    val nextPaths = lastStep.destinationIds
        .map { valves.findByName(it) }
        .filter { visitedValvesWithPressure[it] != cumulativePressure }
        .map {
            val newPathVisitedValvesWithPressure = visitedValvesWithPressure.toMutableMap()
            newPathVisitedValvesWithPressure[it] = cumulativePressure
            copy(steps = steps.plus(it), timeLeft = timeLeft, visitedValvesWithPressure = newPathVisitedValvesWithPressure)
        }
        .sortedBy { it.valvesToOpen.contains(it.steps.last()) }

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

fun List<Valve>.findByName(name: String): Valve =
    find { it.name == name } ?: error("Valve of name $name not found.")

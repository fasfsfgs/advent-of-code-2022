import kotlin.streams.toList

data class ProcedureStep(val src: Int, val dest: Int, val times: Int)

fun getProcedure(strProcedure: String): List<ProcedureStep> = strProcedure
    .lines()
    .map { it.toProcedureStep() }

fun String.toProcedureStep() = run {
    val times = getInstructionPart(this, "move")
    val src = getInstructionPart(this, "from")
    val dest = getInstructionPart(this, "to")
    ProcedureStep(src, dest, times)
}

fun getInstructionPart(strInstruction: String, part: String): Int = strInstruction
    .substringAfter("$part ")
    .takeWhile { it.isLetterOrDigit() }
    .toInt()

fun getStacks(strStacks: String): List<ArrayDeque<Char>> {
    val numberOfStacks = strStacks
        .lines()
        .last() // stack numbers
        .trim()
        .chunked(4)
        .map { it.trim().toInt() }
        .last()

    val stacks = (1..numberOfStacks).map { ArrayDeque<Char>() }

    strStacks
        .lines()
        .reversed()
        .drop(1) // stack numbers
        .map { getStackRow(it) }
        .forEach { stackRow ->
            stackRow.forEachIndexed { index, c ->
                if (c.isLetterOrDigit()) stacks[index].addLast(c)
            }
        }

    return stacks
}

fun getStackRow(strStackRow: String): List<Char> = strStackRow
    .chunked(4)
    .map { it[1] }

fun operateOverStacks(stacks: List<ArrayDeque<Char>>, procedure: List<ProcedureStep>) = procedure
    .forEach { step ->
        repeat(step.times) { stacks[step.dest - 1].addLast(stacks[step.src - 1].removeLast()) }
    }

fun operateOverStacksWithCrateMover9001(stacks: List<ArrayDeque<Char>>, procedure: List<ProcedureStep>) = procedure
    .forEach { step ->
        (1..step.times)
            .map { stacks[step.src - 1].removeLast() }
            .reversed()
            .forEach { stacks[step.dest - 1].addLast(it) }
    }

fun List<ArrayDeque<Char>>.topCrates() = map { it.last() }.joinToString("")

fun main() {
    fun part1(input: String): String {
        val (strStacks, strProcedure) = input.split("\n\n")
        val procedure = getProcedure(strProcedure)
        val stacks = getStacks(strStacks)
        operateOverStacks(stacks, procedure)
        return stacks.topCrates()
    }

    fun part2(input: String): String {
        val (strStacks, strProcedure) = input.split("\n\n")
        val procedure = getProcedure(strProcedure)
        val stacks = getStacks(strStacks)
        operateOverStacksWithCrateMover9001(stacks, procedure)
        return stacks.topCrates()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInputAsString("Day05")
    println(part1(input))
    println(part2(input))
}

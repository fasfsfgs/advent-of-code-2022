data class AoCFile(val name: String, val size: Long, val dir: String)

fun List<AoCFile>.totalSize() = sumOf { it.size }

fun List<AoCFile>.dirSizes() = asSequence()
    .flatMap { it.dir.possibleDirs() }
    .distinct()
    .map { dir -> this.filter { it.dir == dir || it.dir.startsWith("$dir/") } }
    .map { files ->
        files.sumOf { it.size }
    }

fun List<AoCFile>.totalSizeDirsSmallerThan(limit: Long) = dirSizes()
    .filter { it <= limit }
    .sum()

fun List<AoCFile>.smallestDirSizeLargerThan(minSize: Long) = dirSizes()
    .sorted()
    .find { it > minSize } ?: error("Cannot find a single directory that has the minimum required size.")

fun String.possibleDirs() = split('/')
    .drop(1)
    .fold(listOf("")) { acc, it -> acc.plus(acc.last() + "/$it") }

fun List<String>.toAoCFiles() = run {
    var currDir = ""

    drop(1) // $ cd /
        .mapNotNull { line ->
            var file: AoCFile? = null
            when {
                line == "$ cd .." -> currDir = currDir.substringBeforeLast("/")

                line.startsWith("$ cd ") -> currDir = currDir + '/' + line.substring(5)

                line.first().isDigit() -> {
                    val (strFileSize, name) = line.split(" ")
                    file = AoCFile(name, strFileSize.toLong(), currDir)
                }
            }
            file
        }
}

fun main() {
    fun part1(input: List<String>) = input.toAoCFiles().totalSizeDirsSmallerThan(100_000)

    fun part2(input: List<String>): Long {
        val availableSpace = 70_000_000 - 30_000_000

        val files = input.toAoCFiles()
        val usedSpace = files.totalSize()

        val spaceToFreeUp = usedSpace - availableSpace

        return files.smallestDirSizeLargerThan(spaceToFreeUp)
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)
    check(part2(testInput) == 24933642L)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

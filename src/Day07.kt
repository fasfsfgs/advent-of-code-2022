data class AoCFile(val name: String, val size: Long, val dir: String)

fun getAoCFiles(input: List<String>): List<AoCFile> {
    var currDir: String = ""

    return input
        .drop(1) // $ cd /
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

fun getTotalSizeDirsSmallerThan(files: List<AoCFile>, limit: Long): Long = files
    .map { it.dir }
    .distinct()
    .onEach { println(it) }
    .map { dir -> dir to files.filter { it.dir == dir || it.dir.startsWith("$dir/") } }
    .map { dirWithFiles -> dirWithFiles.first to dirWithFiles.second.sumOf { it.size } }
    .filter { it.second <= limit }
    .sumOf { it.second }

fun main() {
    fun part1(input: List<String>): Long {
        val files = getAoCFiles(input)
        return getTotalSizeDirsSmallerThan(files, 100000)
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L)

    println("---------------------------")

    val input = readInput("Day07")
    println(part1(input)) // 1331842 is wrong
//    println(part2(input))
}

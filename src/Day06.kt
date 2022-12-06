fun String.findMarker(markerSize: Int) =
    windowed(markerSize)
        .first { window ->
            val repeatedChar = window.find { testedChar -> window.count { testedChar == it } > 1 }
            repeatedChar == null
        }

fun main() {
    fun part1(input: String): Int {
        val marker = input.findMarker(4)
        return input.indexOf(marker) + marker.length
    }

    fun part2(input: String): Int {
        val marker = input.findMarker(14)
        return input.indexOf(marker) + marker.length
    }

    val testInput = readInputAsString("Day06_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 19)

    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)

    val input = readInputAsString("Day06")
    println(part1(input))
    println(part2(input))
}
